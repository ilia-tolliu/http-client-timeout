package example.client.impl.apache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.Duration;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

public class ApacheClientImpl {

  public void getSlowResource(Duration requestTimeout) {
    try (var client = HttpClientBuilder.create().build()) {
      var request = new HttpGet(URI.create("http://localhost:8000/slow"));
      var requestConfig = RequestConfig.custom()
        .setConnectionRequestTimeout(requestTimeout.toMillisPart())
        .build();
      request.setConfig(requestConfig);

      client.execute(request, response -> {
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200) {
          throw new RuntimeException("Response is not Ok: %d".formatted(statusCode));
        }

        try(
          var inputStream = response.getEntity().getContent();
          var inputStreamReader = new InputStreamReader(inputStream);
          var lineReader = new BufferedReader(inputStreamReader)
        ) {
          lineReader.lines().forEach(System.out::println);
        }

        return statusCode;
      });
    } catch (IOException e) {
      throw new RuntimeException("Request failed", e);
    }
  }
}
