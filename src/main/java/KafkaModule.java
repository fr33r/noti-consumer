import configuration.KafkaConsumerConfiguration;
import configuration.NotiConsumerConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public final class KafkaModule extends NotiConsumerModule {

  // Kafka properties.
  private static final String KAFKA_PROPERTY_BOOTSTRAP_SERVERS = "bootstrap.servers";
  private static final String KAFKA_PROPERTY_SCHEMA_REGISTRY_URL = "schema.registry.url";
  private static final String KAFKA_PROPERTY_KEY_DESERIALIZER = "key.deserializer";
  private static final String KAFKA_PROPERTY_VALUE_DESERIALIZER = "value.deserializer";
  private static final String KAFKA_PROPERTY_GROUP_ID = "group.id";

  // Kafka property values.
  private static final String KEY_DESERIALIZER =
      "io.confluent.kafka.serializers.KafkaAvroDeserializer";
  private static final String VALUE_DESERIALIZER =
      "io.confluent.kafka.serializers.KafkaAvroDeserializer";

  public KafkaModule(NotiConsumerConfiguration configuration, Environment environment) {
    super(configuration, environment);
  }

  @Override
  public void configure() {

    // create KafkaConsumer.
    KafkaConsumerConfiguration consumerConfiguration =
        this.getConfiguration().getKafkaConfiguration().getConsumerConfiguration();
    String bootstrapServers = String.join(",", consumerConfiguration.getBootstrapServers());
    String groupID = consumerConfiguration.getGroupID();
    String schemaRegistryURL = consumerConfiguration.getSchemaRegistryURL();

    Properties consumerProperties = new Properties();
    consumerProperties.put(KAFKA_PROPERTY_GROUP_ID, groupID);
    consumerProperties.put(KAFKA_PROPERTY_KEY_DESERIALIZER, KEY_DESERIALIZER);
    consumerProperties.put(KAFKA_PROPERTY_VALUE_DESERIALIZER, VALUE_DESERIALIZER);
    consumerProperties.put(KAFKA_PROPERTY_BOOTSTRAP_SERVERS, bootstrapServers);
    consumerProperties.put(KAFKA_PROPERTY_SCHEMA_REGISTRY_URL, schemaRegistryURL);

    KafkaConsumer<String, GenericRecord> consumer =
        new KafkaConsumer<String, GenericRecord>(consumerProperties);

    List<String> topics = new ArrayList<String>();
    topics.add("sms");
    consumer.subscribe(topics);

    this.getEnvironment().register(KafkaConsumer.class, consumer);
  }
}
