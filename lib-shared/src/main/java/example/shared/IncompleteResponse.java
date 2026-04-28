package example.shared;

import java.time.Duration;

public record IncompleteResponse(
  int statusCode,
  String incompleteBody,
  Duration duration
) {
}

