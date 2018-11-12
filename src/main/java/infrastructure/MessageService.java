package infrastructure;

import java.util.UUID;

public interface MessageService {

  Message findMessage(UUID notificationUUID, String externalID);

  Message getMessage(UUID notificationUUID, Integer id);

  void updateMessage(UUID notificationUUID, Message message);
}
