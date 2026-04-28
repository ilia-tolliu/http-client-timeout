package example.client.impl.jdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

import example.shared.CompletedResponse;
import example.shared.Deadline;
import example.shared.DeadlineException;
import example.shared.IncompleteResponse;

import static example.shared.DeadlineCanceller.registerDeadlineCancellation;
import static example.shared.Util.closeUnchecked;
import static example.shared.Util.durationSince;
import static java.lang.System.lineSeparator;
import static java.util.Objects.isNull;

public class JdkClientImpl {

  private final HttpClient client;

  public JdkClientImpl(HttpClient client) {
    this.client = client;
  }

  public CompletedResponse getSlowResource(
    Duration firstByteTimeout,
    Duration fullTimeout
  ) {
    var deadline = Deadline.after(fullTimeout);

    var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:8000/slow"))
      .timeout(firstByteTimeout)
      .build();

    try {
      var httpResponse = client.send(request, BodyHandlers.ofInputStream());

      int statusCode = httpResponse.statusCode();
      var bodyBuilder = new StringBuilder();

      try (
        var in = httpResponse.body();
        var streamReader = new InputStreamReader(in);
        var lineReader = new BufferedReader(streamReader)
      ) {
        registerDeadlineCancellation(deadline, () -> closeUnchecked(in));

        String line;
        while (true) {
          line = lineReader.readLine();
          if (isNull(line)) {
            break;
          }

          bodyBuilder.append(line).append(lineSeparator());
          System.out.println(line);
        }
      } catch (IOException e) {
        if (deadline.isCancelledByDeadline()) {
          var response = new IncompleteResponse(
            statusCode,
            bodyBuilder.toString(),
            durationSince(deadline.getStartedAt())
          );
          throw new DeadlineException(response, e);
        }

        throw e;
      } finally {
        deadline.cancel();
      }

      return new CompletedResponse(
        statusCode,
        bodyBuilder.toString(),
        durationSince(deadline.getStartedAt())
      );
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException("JDK HTTP Client request failed", e);
    }
  }
}
