package infrastructure;

// https://support.twilio.com/hc/en-us/articles/223134347-What-do-the-SMS-statuses-mean-
public enum MessageStatus {

  // waiting to be handed off to Twilio; only for delayed messages;
  PENDING("PENDING"),

  // sent to twilio.
  SENT("SENT"),

  // delivered to twilios dependency.
  DELIVERED("DELIVERED"),

  // didn't deliver for whatever reason.
  FAILED("FAILED");

  private String status;

  private MessageStatus(String status) {
    this.status = status;
  }

  @Override
  public String toString() {
    return this.status;
  }
}
