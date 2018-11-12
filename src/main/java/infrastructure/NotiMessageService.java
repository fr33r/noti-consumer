package infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import siren.EmbeddedLinkSubEntity;
import siren.Entity;
import siren.EntityBase;

// https://hc.apache.org/httpcomponents-client-ga/tutorial/html/fundamentals.html#d5e74
public final class NotiMessageService implements MessageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(NotiMessageService.class);
  private final CloseableHttpClient httpClient;
  private final String notiURL;
  private final String contentType;
  private final ObjectMapper objectMapper;

  public NotiMessageService(CloseableHttpClient client) {
    this.httpClient = client;
    this.notiURL = "http://noti:8080";
    this.contentType = "application/vnd.siren+json";
    this.objectMapper = new ObjectMapper();
  }

  private <T> T get(String url) throws HttpException, IOException {

    // issue the request.
    HttpGet get = new HttpGet(url);
    try (CloseableHttpResponse response = this.httpClient.execute(get)) {
      HttpEntity httpEntity = response.getEntity();
      try (InputStream is = httpEntity.getContent()) {
        return this.objectMapper.readValue(is, new TypeReference<T>() {});
      }
    }
  }

  private URI getNotificationsCollectionURI() throws HttpException, IOException {
    URI notificationsCollectionURI = null;
    Entity notiEntity = this.<Entity>get(this.notiURL);
    EmbeddedLinkSubEntity notificationsCollectionEntity = null;

    for (EntityBase entity : notiEntity.getEntities()) {
      // TODO - hate this downcast - any other way to handle this?
      EmbeddedLinkSubEntity subEntity = (EmbeddedLinkSubEntity) entity;
      if (subEntity.getKlass().contains("notification")
          && subEntity.getKlass().contains("collection")) {
        notificationsCollectionEntity = subEntity;
        break;
      }
    }
    return notificationsCollectionEntity == null ? null : notificationsCollectionEntity.getHref();
  }

  private URI getMatchingNotificationURI(URI notificationsCollectionURI, String externalID)
      throws HttpException, IOException {

    // should instead read a link with relations of 'SEARCH'.
    String notificationsCollectionSearchURI =
        String.format("%s?externalID=%s", notificationsCollectionURI.toString(), externalID);
    Entity notificationCollectionEntity = this.<Entity>get(notificationsCollectionSearchURI);

    if (notificationCollectionEntity.getEntities().size() == 0) {
      return null;
    }

    // TODO
    EmbeddedLinkSubEntity notificationSubEntity =
        (EmbeddedLinkSubEntity) notificationCollectionEntity.getEntities().get(0);

    return notificationSubEntity.getHref();
  }

  private URI getMessagesCollectionURI(URI notificationURI) throws HttpException, IOException {

    Entity notificationEntity = this.<Entity>get(notificationURI.toString());
    EmbeddedLinkSubEntity messagesCollectionEntity = null;

    for (EntityBase entity : notificationEntity.getEntities()) {
      // TODO - hate this downcast - any other way to handle this?
      EmbeddedLinkSubEntity subEntity = (EmbeddedLinkSubEntity) entity;
      if (subEntity.getKlass().contains("message") && subEntity.getKlass().contains("collection")) {
        messagesCollectionEntity = subEntity;
        break;
      }
    }
    return messagesCollectionEntity == null ? null : messagesCollectionEntity.getHref();
  }

  private Message getMatchingMessage(URI messageCollectionURI, String externalID)
      throws HttpException, IOException {

    Entity messageCollectionEntity = this.<Entity>get(messageCollectionURI.toString());
    for (EntityBase entity : messageCollectionEntity.getEntities()) {
      // TODO - hate this downcast - any other way to handle this?
      EmbeddedLinkSubEntity subEntity = (EmbeddedLinkSubEntity) entity;
      Message message = this.getMessage(subEntity.getHref());
      if (message.externalID() != null && message.externalID().equals(externalID)) {
        return message;
      }
    }
    return null;
  }

  private Message getMessage(URI messageURI) throws HttpException, IOException {

    Entity messageEntity = this.<Entity>get(messageURI.toString());
    Map<String, Object> entityProperties = messageEntity.getProperties();
    Message message = new Message();
    message.setID(Integer.parseInt((String) entityProperties.get("id")));
    message.setExternalID((String) entityProperties.get("externalID"));
    message.setContent((String) entityProperties.get("content"));
    message.setTo((String) entityProperties.get("to"));
    message.setFrom((String) entityProperties.get("from"));
    String messageStatus = (String) entityProperties.get("status");
    message.setStatus(MessageStatus.valueOf(messageStatus));
    return message;
  }

  // 1. request root resource.
  // 2. request notification collection resource.
  // 3. request message collection resource.
  // 4. iterate through messages collection? can't, they are links. oyy.
  @Override
  public Message findMessage(UUID notificationUUID, String externalID) {

    try {
      // 1
      URI notificationsCollectionURI = this.getNotificationsCollectionURI();

      // 2
      URI notificationURI = this.getMatchingNotificationURI(notificationsCollectionURI, externalID);

      // 3
      URI messagesCollectionURI = this.getMessagesCollectionURI(notificationURI);

      // 4
      Message message = this.getMatchingMessage(messagesCollectionURI, externalID);
      return message;
    } catch (HttpException h) {
      throw new RuntimeException(h);
    } catch (IOException x) {
      throw new RuntimeException(x);
    }
  }

  @Override
  public Message getMessage(UUID notificationUUID, Integer id) {
    // need a search by UUID on the notifications collection.
    // need a search by ID on the messages collection for a notification.
    // without these, its not feasible to utilize Siren - it would require
    // requesting each element in the collection (because they are all links)
    // and checking to see if the value matches.
    // in the meantime, i'll use JSON.

    try {
      // issue the request.
      HttpGet get =
          new HttpGet(
              String.format(
                  "%s/notifications/%s/messages/%s/", this.notiURL, notificationUUID, id));
      get.setHeader("Accept", "application/json");
      try (CloseableHttpResponse response = this.httpClient.execute(get)) {
        if (response.getStatusLine().getStatusCode() != 200) {
          // log.
          LOGGER.error(
              "Unable to retrieve message. CODE=" + response.getStatusLine().getStatusCode());
          return null;
        }
        HttpEntity httpEntity = response.getEntity();
        try (InputStream is = httpEntity.getContent()) {
          return this.objectMapper.readValue(is, Message.class);
        }
      }
    } catch (IOException x) {
      throw new RuntimeException(x);
    }
  }

  @Override
  public void updateMessage(UUID notificationUUID, Message message) {

    LOGGER.info("Updating message...");
    try {
      // serialize the message.
      String messageJSON = this.objectMapper.writeValueAsString(message);

      // issue the request.
      HttpPut put =
          new HttpPut(
              String.format(
                  "%s/notifications/%s/messages/%s/",
                  this.notiURL, notificationUUID, message.id()));

      put.setEntity(new StringEntity(messageJSON, ContentType.create("application/json")));
      try (CloseableHttpResponse response = this.httpClient.execute(put)) {
        if (response.getStatusLine().getStatusCode() != 200) {
          // log.
          LOGGER.error(
              "Unable to update message. CODE=" + response.getStatusLine().getStatusCode());
        }
      }
    } catch (IOException x) {
      throw new RuntimeException(x);
    }
  }
}
