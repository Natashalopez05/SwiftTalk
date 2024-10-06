package com.example.swifttalk.logic.models.Messages;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public abstract class Message {
  private final String id;
  private final String userEmail;
  private final Timestamp timestamp;

  public Message(String id, String userEmail, Timestamp timestamp) {
    this.id = id;
    this.userEmail = userEmail;
    this.timestamp = timestamp;
  }

  public static Message createFromDatabase(DocumentSnapshot document) {
    String typeOfMessage = document.getString("type");
    MessageType messageType = MessageType.fromString(typeOfMessage);

    if (messageType == MessageType.TEXT) {
      return TextMessage.createFromDatabase(document);
    } else if (messageType == MessageType.IMAGE) {
      return ImageMessage.createFromDatabase(document);
    } else {
      throw new IllegalArgumentException("Invalid message type");
    }
  }

  public String getId() {
    return id;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }
}
