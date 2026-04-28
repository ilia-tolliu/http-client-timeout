package example.shared;

public class DeadlineException extends RuntimeException {

  private final IncompleteResponse incompleteResponse;

  public DeadlineException(IncompleteResponse incompleteResponse, Exception cause) {
    super(cause);

    this.incompleteResponse = incompleteResponse;
  }

  public IncompleteResponse getIncompleteResponse() {
    return incompleteResponse;
  }
}
