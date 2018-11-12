package configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Represents the SMS configuration for Noti. */
public class SMSConfiguration extends Configuration {

  private String accountSID;
  private String authToken;

  @JsonProperty("accountSID")
  public String getAccountSID() {
    return this.accountSID;
  }

  @JsonProperty("accountSID")
  public void setAccountSID(String accountSID) {
    this.accountSID = accountSID;
  }

  @JsonProperty("authToken")
  public String getAuthToken() {
    return this.authToken;
  }

  @JsonProperty("authToken")
  public void setAuthToken(String authToken) {
    this.authToken = authToken;
  }
}
