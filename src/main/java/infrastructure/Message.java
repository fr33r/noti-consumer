package infrastructure;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class Message {

  private Integer id;
  private String content;
  private String to;
  private String from;
  private MessageStatus status;
  private String externalID;

  public Message() {}

  @JsonProperty("id")
  public Integer id() {
    return this.id;
  }

  public void setID(Integer id) {
    this.id = id;
  }

  @JsonProperty("content")
  public String content() {
    return this.content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @JsonProperty("to")
  public String to() {
    return this.to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  @JsonProperty("from")
  public String from() {
    return this.from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  @JsonProperty("status")
  public MessageStatus status() {
    return this.status;
  }

  public void setStatus(MessageStatus status) {
    this.status = status;
  }

  @JsonProperty("externalID")
  public String externalID() {
    return this.externalID;
  }

  public void setExternalID(String externalID) {
    this.externalID = externalID;
  }
}
