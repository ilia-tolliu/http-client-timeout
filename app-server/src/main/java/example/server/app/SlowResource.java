package example.server.app;

import java.io.PrintStream;
import java.time.Duration;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

import static java.lang.Thread.currentThread;

@Path("/slow")
public class SlowResource {

  @ConfigProperty(name = "example.server.app.delay.initial")
  Duration initialDelay;

  @ConfigProperty(name = "example.server.app.delay.running")
  Duration runningDelay;

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response hello() {
    sleep(initialDelay);

    StreamingOutput streamingOutput = outputStream -> {
      try (var printStream = new PrintStream(outputStream)) {
        RESPONSE.lines()
          .forEach(line -> {
            printStream.println(line);
            printStream.flush();

            sleep(runningDelay);
          });
      }
    };

    return Response.ok(streamingOutput).build();
  }

  private void sleep(Duration duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      currentThread().interrupt();
    }
  }

  private static final String RESPONSE = """
    1. The wind whispered softly through the hollow trees.
    2. A flicker of light danced across the quiet lake.
    3. Numbers drifted like clouds in an absent sky.
    4. The clock ticked louder in the empty room.
    5. A single leaf spiraled down without a sound.
    6. Shadows stretched as the day folded into night.
    7. The paper crinkled beneath uncertain hands.
    8. A distant echo hinted at forgotten places.
    9. Colors blurred in a slow and steady fade.
    10. Footsteps paused at the edge of nowhere.
    11. A quiet hum filled the silent corridor.
    12. Words formed but never left the lips.
    13. The horizon shimmered with quiet promise.
    14. Dust settled on memories left untouched.
    15. The air carried a trace of something unknown.
    16. Time slipped unnoticed between moments.
    17. A door creaked open to nothing at all.
    18. The sky dimmed in gradual surrender.
    19. A thought lingered longer than expected.
    20. Silence spoke in ways sound never could.
    21. The glass reflected a version slightly off.
    22. A breeze turned pages without permission.
    23. The ground felt steady but uncertain.
    24. A pattern emerged and vanished just as fast.
    25. The candle flickered against invisible currents.
    26. A shadow moved without a source.
    27. The echo arrived before the sound itself.
    28. A line drawn never reached its end.
    29. The stars blinked in quiet rhythm.
    30. A question floated without an answer.
    31. The river curved around unseen obstacles.
    32. A distant light pulsed in steady intervals.
    33. The walls seemed closer than before.
    34. A faint melody played without origin.
    35. The horizon refused to stay still.
    36. A whisper crossed the edge of awareness.
    37. The ink spread slowly across the page.
    38. A memory shifted shape over time.
    39. The floor creaked under invisible weight.
    40. A reflection lingered after turning away.
    41. The sky held its breath for a moment.
    42. A sound repeated without variation.
    43. The air thickened with quiet tension.
    44. A step forward felt like standing still.
    45. The light dimmed but never disappeared.
    46. A line of thought unraveled mid-sentence.
    47. The mirror showed less than expected.
    48. A door closed somewhere far away.
    49. The wind carried fragments of nothing.
    50. A pause stretched longer than intended.
    51. The ceiling seemed higher than before.
    52. A flicker passed just out of sight.
    53. The silence deepened with each second.
    54. A trace of warmth lingered briefly.
    55. The path split without warning.
    56. A sound echoed from no direction.
    57. The air shifted almost imperceptibly.
    58. A word repeated until it lost meaning.
    59. The light bent around unseen edges.
    60. A presence faded as it was noticed.
    61. The walls held secrets without telling.
    62. A breath lingered in the cold air.
    63. The horizon blurred into uncertainty.
    64. A pattern tried to form but failed.
    65. The room felt larger on the inside.
    66. A movement caught attention too late.
    67. The silence grew louder over time.
    68. A shadow stretched beyond its limit.
    69. The ground felt slightly uneven.
    70. A flicker of memory passed quickly.
    71. The air carried a subtle vibration.
    72. A thought began but never finished.
    73. The space between moments expanded.
    74. A sound lingered longer than expected.
    75. The edges softened into nothingness.
    76. A reflection appeared without cause.
    77. The wind shifted direction suddenly.
    78. A quiet tension filled the void.
    79. The light pulsed once and stopped.
    80. A line curved when it should not.
    81. The silence broke without sound.
    82. A step echoed without movement.
    83. The horizon tilted ever so slightly.
    84. A trace of something remained undefined.
    85. The air felt heavier than before.
    86. A shadow lingered beyond its time.
    87. The sound faded into an empty space.
    88. A flicker hinted at something hidden.
    89. The walls seemed to listen closely.
    90. A breath was held indefinitely.
    91. The path vanished behind each step.
    92. A thought echoed without origin.
    93. The sky dimmed without reason.
    94. A movement went unnoticed until later.
    95. The silence returned stronger than before.
    96. A line of light split the darkness.
    97. The air shimmered with subtle change.
    98. A pause interrupted the flow of time.
    99. The edges of reality softened slightly.
    100. A final moment lingered without end.
    """;
}
