package com.example.swifttalk.logic.models.Messages;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import org.parceler.Parcel;

import java.util.Map;

@Parcel
public class LastMessage {
  private String context;
  private Timestamp timestamp;

  public LastMessage() {
  }

  public LastMessage(String context, Timestamp timestamp) {
    this.context = context;
    this.timestamp = timestamp;
  }

  public static LastMessage createFromDatabase(Map<String, Object> document) {
    String context = (String) document.get("context");
    Timestamp timestamp = (Timestamp) document.get("timestamp");

    return new LastMessage(context, timestamp);
  }

  public String getContext() {
    return context;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }
}
