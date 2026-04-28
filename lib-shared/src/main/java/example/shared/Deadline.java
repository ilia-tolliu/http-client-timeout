package example.shared;

import java.time.Duration;
import java.time.Instant;

public class Deadline {

  private final Instant startedAt = Instant.now();

  private final Instant deadline;

  private volatile boolean isCancelledByDeadline = false;

  private volatile boolean isCancelled = false;

  public Deadline(Instant deadline) {
    this.deadline = deadline;
  }

  public static Deadline after(Duration duration) {
    var deadline = Instant.now().plus(duration);
    return new Deadline(deadline);
  }

  public Instant getDeadline() {
    return deadline;
  }

  public boolean isCancelledByDeadline() {
    return isCancelledByDeadline;
  }

  public void cancelByDeadline() {
    isCancelledByDeadline = true;
  }

  public boolean isCancelled() {
    return isCancelled;
  }

  public void cancel() {
    isCancelled = true;
  }

  public Instant getStartedAt() {
    return startedAt;
  }
}
