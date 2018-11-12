import ch.qos.logback.classic.Level;
import configuration.NotiConsumerConfiguration;
import infrastructure.Message;
import infrastructure.MessageService;
import infrastructure.ShortMessageService;
import io.dropwizard.logging.BootstrapLogging;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class NotiConsumer extends Application<NotiConsumerConfiguration> {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotiConsumer.class);

  @Override
  public String getName() {
    return "noti-consumer";
  }

  @Override
  public void run(NotiConsumerConfiguration configuration, Environment environment)
      throws Exception {

    BootstrapLogging.bootstrap(Level.INFO);

    List<ApplicationModule> modules = new ArrayList<>();
    modules.add(new MetricsModule(configuration, environment));
    modules.add(new KafkaModule(configuration, environment));
    modules.add(new InfrastructureModule(configuration, environment));

    for (ApplicationModule module : modules) {
      module.configure();
    }

    this.consume(environment);
  }

  public void consume(Environment environment) {

    MessageService messageService = environment.resolve(MessageService.class);
    ShortMessageService shortMessageService = environment.resolve(ShortMessageService.class);
    KafkaConsumer<String, GenericRecord> consumer =
        environment.<KafkaConsumer<String, GenericRecord>>resolve(KafkaConsumer.class);

    try {
      while (true) {
        ConsumerRecords<String, GenericRecord> records = consumer.poll(100);
        for (ConsumerRecord<String, GenericRecord> record : records) {
          LOGGER.debug("Processing record...");
          LOGGER.debug(record.toString());

          // read contents of message.
          LOGGER.info("Processing message...");
          UUID notificationUUID =
              UUID.fromString((String) record.value().get("notificationUUID").toString());
          Integer messageID = (Integer) record.value().get("messageID");

          // retrieve message from noti.
          Message message = messageService.getMessage(notificationUUID, messageID);

          // send message.
          message = shortMessageService.send(message);

          // update message in noti.
          messageService.updateMessage(notificationUUID, message);

          LOGGER.info("Processing complete.");
        }
      }
    } finally {
      consumer.close();
    }
  }

  @Override
  public Class<NotiConsumerConfiguration> getConfigurationClass() {
    return NotiConsumerConfiguration.class;
  }

  public static void main(String[] args) throws Exception {
    // run application.
    new NotiConsumer().run(args);
  }
}
