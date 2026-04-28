package example.client.app;

import java.net.http.HttpClient;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import example.client.impl.apache.ApacheClientImpl;
import example.client.impl.jdk.JdkClientImpl;
import example.shared.DeadlineException;

import static java.lang.Thread.sleep;

public class ClientApp {

  private static final Duration READ_TIMEOUT = Duration.ofMillis(200);
  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(30);

  private static final Logger LOGGER = LoggerFactory.getLogger(ClientApp.class);

  public static void main(String[] args) throws InterruptedException {
    runWithJdkClient();

    sleep(Duration.ofSeconds(5));

    runWithApacheClient();

    sleep(Duration.ofSeconds(5));
  }

  static void runWithJdkClient() {
    LOGGER.info("Running with JDK HTTP Client");

    try (var client = HttpClient.newBuilder().build()) {
      var jdkClientImpl = new JdkClientImpl(client);
      var response = jdkClientImpl.getSlowResource(READ_TIMEOUT, REQUEST_TIMEOUT);

      LOGGER.info("JDK HTTP Client processed complete request in {}", response.duration());
    } catch (DeadlineException e) {
      LOGGER.error("JDK HTTP Client got incomplete request in {}", e.getIncompleteResponse().duration(), e);
    }
  }

  static void runWithApacheClient() {
    LOGGER.info("Running with Apache HTTP Client");

    try {
      var apacheClientImpl = new ApacheClientImpl();
      var response = apacheClientImpl.getSlowResource(READ_TIMEOUT, REQUEST_TIMEOUT);

      LOGGER.info("Apache HTTP Client processed complete request in {}", response.duration());
    } catch (DeadlineException e) {
      LOGGER.error("Apache HTTP Client got incomplete request in {}", e.getIncompleteResponse().duration(), e);
    }
  }
}
