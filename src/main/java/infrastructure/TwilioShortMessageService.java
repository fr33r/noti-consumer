package infrastructure;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the Twilio SMS implementation. This service is responsible for directly interfacing
 * with Twilio APIs to send text messages.
 */
public final class TwilioShortMessageService implements ShortMessageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TwilioShortMessageService.class);
  private final String accountSID;
  private final String authToken;
  private final MetricRegistry metricRegistry;
  private final Meter createTwilioMessageMeter;
  private final Timer createTwilioMessageTimer;

  /**
   * Construct a {@link TwilioShortMessageService} instance.
   *
   * @param accountSID Used to exercise the REST API.
   * @param authToken Used by Twilio to authenticate requests.
   */
  public TwilioShortMessageService(
      final MetricRegistry metricRegistry, final String accountSID, final String authToken) {
    this.metricRegistry = metricRegistry;
    this.createTwilioMessageMeter =
        this.metricRegistry.meter(
            MetricRegistry.name(
                TwilioShortMessageService.class, "twilio", "message", "create", "success"));
    this.createTwilioMessageTimer =
        this.metricRegistry.timer(
            MetricRegistry.name(TwilioShortMessageService.class, "twilio", "message", "create"));
    this.accountSID = accountSID;
    this.authToken = authToken;
    Twilio.init(this.accountSID, this.authToken);
  }

  /** Sends the provided message. */
  @Override
  public infrastructure.Message send(final infrastructure.Message message) {

    // start timer.
    final Timer.Context timerContext = this.createTwilioMessageTimer.time();

    LOGGER.info("Sending message to Twilio...");
    Message twilioMessage =
        Message.creator(
                new PhoneNumber(message.to()), new PhoneNumber(message.from()), message.content())
            .create();
    LOGGER.info("Message sent.");

    // stop the timer.
    timerContext.stop();

    // record a successful request with Twilio.
    this.createTwilioMessageMeter.mark();

    MessageStatus status;

    switch (twilioMessage.getStatus()) {
      case QUEUED:
      case SENDING:
        status = MessageStatus.PENDING;
        break;
      case SENT:
        status = MessageStatus.SENT;
        break;
      case DELIVERED:
        status = MessageStatus.DELIVERED;
        break;
      default:
        status = MessageStatus.FAILED;
    }

    // should be a factory here?
    message.setExternalID(twilioMessage.getSid());
    message.setStatus(status);
    return message;
  }
}
