package infrastructure;

/** */
public interface ShortMessageService {

  /**
   * Sends the provided message.
   *
   * @param message The message content that is sent.
   */
  Message send(final Message message);
}
