package example.client.app;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import example.client.impl.apache.ApacheClientImpl;
import example.client.impl.jdk.JdkClientImpl;
import example.client.impl.jdk.JdkClientImpl.RequestTimeoutException;

public class ClientApp {

  private static final Duration READ_TIMEOUT = Duration.ofMillis(200);
  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientApp.class);

  public static void main(String[] args) throws InterruptedException {
    try {
      runWithJdkClient();
    } catch (Exception e) {
      LOGGER.error("JDK HTTP Client failed", e);
    }

    try {
      runWithApacheClient();
    } catch (Exception e) {
      LOGGER.error("Apache HTTP Client failed", e);
    }

    Thread.sleep(Duration.ofSeconds(10));
  }

  static void runWithJdkClient() {
    LOGGER.info("Running with JDK HTTP Client");

    try (var client = HttpClient.newBuilder().build()) {
      var jdkClientImpl = new JdkClientImpl(client);
      var response = jdkClientImpl.getSlowResource(READ_TIMEOUT, REQUEST_TIMEOUT);

      LOGGER.info("JDK HTTP Client processed complete request in {}", response.duration());
    } catch (RequestTimeoutException e) {
      LOGGER.error("JDK HTTP Client got incomplete request in {}", e.getIncompleteResponse().duration(), e);
    }
  }

  static void runWithApacheClient() {
    LOGGER.info("Running with Apache HTTP Client");

    var apacheClientImpl = new ApacheClientImpl();
    var apacheClientStart = Instant.now();

    apacheClientImpl.getSlowResource(REQUEST_TIMEOUT);

    var jdkClientElapsed = Duration.between(apacheClientStart, Instant.now());
    LOGGER.info("Apache HTTP Client processed the request in {}", jdkClientElapsed);
  }
}
