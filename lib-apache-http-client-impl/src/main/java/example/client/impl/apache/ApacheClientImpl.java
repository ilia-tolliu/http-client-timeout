package example.client.impl.apache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.Duration;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import example.shared.CompletedResponse;
import example.shared.Deadline;
import example.shared.DeadlineException;
import example.shared.IncompleteResponse;

import static example.shared.DeadlineCanceller.registerDeadlineCancellation;
import static example.shared.Util.durationSince;
import static java.lang.System.lineSeparator;
import static java.util.Objects.isNull;

public class ApacheClientImpl {

  public CompletedResponse getSlowResource(
    Duration socketTimeout,
    Duration fullTimeout
  ) {
    var deadline = Deadline.after(fullTimeout);

    try (var client = HttpClientBuilder.create().build()) {
      var request = new HttpGet(URI.create("http://localhost:8000/slow"));
      registerDeadlineCancellation(deadline, request::abort);

      var requestConfig = RequestConfig.custom()
        .setSocketTimeout(socketTimeout.toMillisPart())
        .build();
      request.setConfig(requestConfig);

      return client.execute(request, httpResponse -> {
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        var bodyBuilder = new StringBuilder();

        try (
          var inputStream = httpResponse.getEntity().getContent();
          var inputStreamReader = new InputStreamReader(inputStream);
          var lineReader = new BufferedReader(inputStreamReader)
        ) {
          String line;
          while (true) {
            line = lineReader.readLine();
            if (isNull(line)) {
              break;
            }

            bodyBuilder.append(line).append(lineSeparator());
            System.out.println(line);
          }
        } catch (Exception e) {
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
      });
    } catch (IOException e) {
      throw new RuntimeException("Apache HTTP Client request failed", e);
    }
  }
}
