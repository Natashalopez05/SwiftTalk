package com.example.swifttalk.logic.models.Messages;

import com.google.firebase.Timestamp;

public abstract class Message {
  private final String id;
  private final String userId;
  private final Timestamp timestamp;

  public Message(String id, String userId, Timestamp timestamp) {
    this.id = id;
    this.userId = userId;
    this.timestamp = timestamp;
  }

  public String getId() {
    return id;
  }

  public String getUserId() {
    return userId;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }
}
