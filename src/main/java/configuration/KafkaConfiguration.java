package configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class KafkaConfiguration {

  private KafkaConsumerConfiguration consumerConfiguration;

  @JsonProperty("consumer")
  public KafkaConsumerConfiguration getConsumerConfiguration() {
    return this.consumerConfiguration;
  }

  @JsonProperty("consumer")
  public void setConsumerConfiguration(KafkaConsumerConfiguration consumerConfiguration) {
    this.consumerConfiguration = consumerConfiguration;
  }
}
