package example.shared;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;

public final class Util {

  private Util() {
  }

  public static void closeUnchecked(Closeable closeable) {
    try {
      closeable.close();
    } catch (IOException e) {
      throw new RuntimeException("Failed to close", e);
    }
  }

  public static Duration durationSince(Temporal start) {
    return Duration.between(start, Instant.now());
  }
}
