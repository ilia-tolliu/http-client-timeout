package example.client.app;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Instant;

import example.client.impl.apache.ApacheClientImpl;
import example.client.impl.jdk.JdkClientImpl;
import example.client.impl.jdk.JdkClientImpl.RequestTimeoutException;

public class ClientApp {

  private static final Duration READ_TIMEOUT = Duration.ofMillis(200);
  private static final Duration REQUEST_TIMEOUT = Duration.ofSeconds(3);

  public static void main(String[] args) throws InterruptedException {
    try {
      runWithJdkClient();
    } catch (Exception e) {
      System.err.println("JDK HTTP Client failed");
      e.printStackTrace(System.err);
    }

    try {
      runWithApacheClient();
    } catch (Exception e) {
      System.err.println("Apache HTTP Client failed");
      e.printStackTrace(System.err);
    }

    Thread.sleep(Duration.ofSeconds(10));
  }

  static void runWithJdkClient() {
    System.out.println("Running with JDK HTTP Client\n");

    try (var client = HttpClient.newBuilder().build()) {
      var jdkClientImpl = new JdkClientImpl(client);
      var response = jdkClientImpl.getSlowResource(READ_TIMEOUT, REQUEST_TIMEOUT);

      System.out.printf("%n%nJDK HTTP Client processed complete request in %s%n%n", response.duration());
    } catch (RequestTimeoutException e) {
      System.out.printf("%n%nJDK HTTP Client got incomplete request in %s%n%n", e.getIncompleteResponse().duration());
      throw e;
    }
  }

  static void runWithApacheClient() {
    System.out.println("Running with Apache HTTP Client\n");

    var apacheClientImpl = new ApacheClientImpl();
    var apacheClientStart = Instant.now();

    apacheClientImpl.getSlowResource(REQUEST_TIMEOUT);

    var jdkClientElapsed = Duration.between(apacheClientStart, Instant.now());
    System.out.printf("%n%nApache HTTP Client processed the request in %s%n%n", jdkClientElapsed);
  }
}
