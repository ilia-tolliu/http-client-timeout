package example.shared;

import java.time.Instant;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DeadlineCanceller {

  private static final Queue<DeadlineEntry> deadlineQueue = new ConcurrentLinkedQueue<>();

  private static final Timer timer = new Timer(true);

  static {
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        var now = Instant.now();

        var iterator = deadlineQueue.iterator();
        while (iterator.hasNext()) {
          var entry = iterator.next();
          if (entry.deadline.getDeadline().isAfter(now)) {
            continue;
          }

          if (entry.deadline.isCancelled()) {
            iterator.remove();
            continue;
          }

          entry.deadline.cancelByDeadline();
          iterator.remove();
          entry.onDeadline.run();
        }
      }
    }, 0L, 100L);
  }

  public static void registerDeadlineCancellation(Deadline deadline, Runnable onDeadline) {
    var entry = new DeadlineEntry(deadline, onDeadline);
    deadlineQueue.add(entry);
  }

  record DeadlineEntry(
    Deadline deadline,
    Runnable onDeadline
  ) {
  }
}