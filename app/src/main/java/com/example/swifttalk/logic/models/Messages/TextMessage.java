package com.example.swifttalk.logic.models.Messages;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class TextMessage extends Message {
  private final String context;

  public TextMessage(String id, String sender, Timestamp timestamp, String context) {
    super(id, sender, timestamp);
    this.context = context;
  }

  public static TextMessage createFromDatabase(DocumentSnapshot document) {
    String id = document.getId();
    String sender = document.getString("userId");
    Timestamp timestamp = document.getTimestamp("timestamp");
    String context = document.getString("context");

    return new TextMessage(id, sender, timestamp, context);
  }

  public String getContext() {
    return context;
  }
}
