package example.client.impl.jdk;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
    var start = Instant.now();
    var deadline = start.plus(fullTimeout);

    var request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:8000/slow"))
      .timeout(firstByteTimeout)
      .build();

    try {
      var httpResponse = client.send(request, BodyHandlers.ofInputStream());

      int statusCode = httpResponse.statusCode();
      var bodyBuilder = new StringBuilder();

      var canceller = new ReadCanceller();

      try (
        var in = httpResponse.body();
        canceller;
        var streamReader = new InputStreamReader(in);
        var lineReader = new BufferedReader(streamReader)
      ) {
        canceller.start(in, deadline);

        String line;
        while (true) {
          line = lineReader.readLine();
          if (isNull(line)) {
            break;
          }

          bodyBuilder.append(line);
          System.out.println(line);
        }
      } catch (IOException e) {
        if (canceller.isCancelled()) {
          var response = new IncompleteResponse(statusCode, bodyBuilder.toString(), durationFrom(start));
          throw new RequestTimeoutException(response, e);
        }

        throw e;
      }

      return new CompletedResponse(statusCode, bodyBuilder.toString(), durationFrom(start));
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private Duration durationFrom(Temporal start) {
    return Duration.between(start, Instant.now());
  }

  public record CompletedResponse(
    int statusCode,
    String body,
    Duration duration
  ) {
  }

  public record IncompleteResponse(
    int statusCode,
    String incompleteBody,
    Duration duration
  ) {
  }

  public static class RequestTimeoutException extends RuntimeException {
    private final IncompleteResponse incompleteResponse;

    public RequestTimeoutException(IncompleteResponse incompleteResponse,  Exception cause) {
      super(cause);

      this.incompleteResponse = incompleteResponse;
    }

    public IncompleteResponse getIncompleteResponse() {
      return incompleteResponse;
    }
  }

}

class ReadCanceller implements AutoCloseable {
  private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  private ScheduledFuture<?> future  = null;
  private volatile boolean isCancelled = false;

  void start(Closeable cancellable, Temporal deadline) {
    var duration = Duration.between(Instant.now(), deadline);
    if (!duration.isPositive()) {
      cancel(cancellable);
      return;
    }

    future = scheduler.schedule(
      () -> cancel(cancellable),
      duration.toMillis(),
      TimeUnit.MILLISECONDS
    );
  }

  private void cancel(Closeable cancellable) {
    try {
      isCancelled = true;
      cancellable.close();
    } catch (IOException e) {
      throw new RuntimeException("Failed to cancel", e);
    }
  }

  boolean isCancelled() {
    return isCancelled;
  }

  @Override
  public void close() {
    if (isNull(future)) {
      return;
    }

    future.cancel(false);
  }
}