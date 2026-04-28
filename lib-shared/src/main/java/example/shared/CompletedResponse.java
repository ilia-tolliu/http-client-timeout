package example.shared;

import java.time.Duration;

public record CompletedResponse(
  int statusCode,
  String body,
  Duration duration
) {
}
