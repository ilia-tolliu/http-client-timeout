package example.client.impl.jdk;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

public class JdkClientImpl {
  public void getSlowResource(Duration requestTimeout) {
    try (var client = HttpClient.newBuilder().build()) {
      var request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:8000/slow"))
        .timeout(requestTimeout)
        .build();

      var response = client.send(request, BodyHandlers.ofLines());

      int statusCode = response.statusCode();
      if (statusCode != 200) {
        throw new RuntimeException("Response is not Ok: %d".formatted(statusCode));
      }

      response.body().forEach(System.out::println);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException("Request failed", e);
    }
  }

}
