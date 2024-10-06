package com.example.swifttalk.logic.models.Messages;
import java.net.URI;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

public class ImageMessage extends Message {
  private final URI imageUrl;

  public static ImageMessage createFromDatabase(DocumentSnapshot document) {
    String id = document.getId();
    String sender = document.getString("user");
    Timestamp timestamp = document.getTimestamp("timestamp");
    URI imageUrl = URI.create(document.getString("context"));

    return new ImageMessage(id, sender, timestamp, imageUrl);
  }

  public ImageMessage(String id, String sender, Timestamp timestamp, URI imageUrl) {
    super(id, sender, timestamp);
    this.imageUrl = imageUrl;
  }

  public URI getImageUrl() {
    return imageUrl;
  }
}
