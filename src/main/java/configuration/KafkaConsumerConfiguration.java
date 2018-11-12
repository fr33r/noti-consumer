package configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public final class KafkaConsumerConfiguration {

  private List<String> bootstrapServers;
  private String schemaRegistryURL;
  private String groupID;

  @JsonProperty("group.id")
  public String getGroupID() {
    return this.groupID;
  }

  @JsonProperty("group.id")
  public void setGroupID(String groupID) {
    this.groupID = groupID;
  }

  @JsonProperty("bootstrap.servers")
  public List<String> getBootstrapServers() {
    return this.bootstrapServers;
  }

  @JsonProperty("bootstrap.servers")
  public void setBootstrapServers(List<String> bootstrapServers) {
    this.bootstrapServers = bootstrapServers;
  }

  @JsonProperty("schema.registry.url")
  public String getSchemaRegistryURL() {
    return this.schemaRegistryURL;
  }

  @JsonProperty("schema.registry.url")
  public void setSchemaRegistryURL(String schemaRegistryURL) {
    this.schemaRegistryURL = schemaRegistryURL;
  }
}
