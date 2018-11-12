import com.codahale.metrics.MetricRegistry;
import configuration.NotiConsumerConfiguration;
import configuration.SMSConfiguration;
import infrastructure.MessageService;
import infrastructure.NotiMessageService;
import infrastructure.ShortMessageService;
import infrastructure.TwilioShortMessageService;
import org.apache.http.impl.client.HttpClients;

public final class InfrastructureModule extends NotiConsumerModule {

  public InfrastructureModule(NotiConsumerConfiguration configuration, Environment environment) {
    super(configuration, environment);
  }

  @Override
  public void configure() {

    // extract configuration.
    SMSConfiguration smsConfiguration = this.getConfiguration().getSMSConfiguration();
    String authToken = smsConfiguration.getAuthToken();
    String accountSID = smsConfiguration.getAccountSID();

    // construct infrastructure services.
    MessageService messageService = new NotiMessageService(HttpClients.createDefault());
    ShortMessageService shortMessageService =
        new TwilioShortMessageService(
            this.getEnvironment().resolve(MetricRegistry.class), accountSID, authToken);

    this.getEnvironment().register(ShortMessageService.class, shortMessageService);
    this.getEnvironment().register(MessageService.class, messageService);
  }
}
