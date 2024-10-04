package com.example.swifttalk.logic.models.Messages;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import org.parceler.Parcel;

@Parcel
public class LastMessage {
  private final String context;
  private final Timestamp timestamp;

  public LastMessage(String context, Timestamp timestamp) {
    this.context = context;
    this.timestamp = timestamp;
  }

  public static LastMessage createFromDatabase(DocumentSnapshot document) {
    String context = document.getString("context");
    Timestamp timestamp = document.getTimestamp("timestamp");

    return new LastMessage(context, timestamp);
  }

  public String getContext() {
    return context;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }
}
