package example.client.app;

import java.time.Duration;
import java.time.Instant;

import example.client.impl.apache.ApacheClientImpl;
import example.client.impl.jdk.JdkClientImpl;

public class ClientApp {

  private static final Duration REQUEST_TIMEOUT = Duration.ofMillis(60);

  public static void main(String[] args) {
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
  }

  static void runWithJdkClient() {
    System.out.println("Running with JDK HTTP Client\n");

    var jdkClientImpl = new JdkClientImpl();
    var jdkClientStart = Instant.now();

    jdkClientImpl.getSlowResource(REQUEST_TIMEOUT);

    var jdkClientElapsed = Duration.between(jdkClientStart, Instant.now());
    System.out.printf("%n%nJDK HTTP Client processed the request in %s%n%n", jdkClientElapsed);
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
