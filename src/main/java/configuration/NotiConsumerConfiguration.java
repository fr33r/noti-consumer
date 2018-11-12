package configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class NotiConsumerConfiguration extends Configuration {

  private KafkaConfiguration kafkaConfiguration;
  private SMSConfiguration smsConfiguration;

  @JsonProperty("kafka")
  public KafkaConfiguration getKafkaConfiguration() {
    return this.kafkaConfiguration;
  }

  @JsonProperty("kafka")
  public void setKafkConfiguration(KafkaConfiguration kafkaConfiguration) {
    this.kafkaConfiguration = kafkaConfiguration;
  }

  @JsonProperty("sms")
  public SMSConfiguration getSMSConfiguration() {
    return this.smsConfiguration;
  }

  @JsonProperty("sms")
  public void setSMSConfiguration(SMSConfiguration smsConfiguration) {
    this.smsConfiguration = smsConfiguration;
  }
}
