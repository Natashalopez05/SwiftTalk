package com.example.swifttalk.logic.models.Chats;
import android.util.Log;
import com.example.swifttalk.logic.models.Messages.LastMessage;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import org.parceler.Parcel;

import java.util.*;

@Parcel
public class PrivateChat extends Chat {
  public PrivateChat() {}

  public PrivateChat(String id, String userCover, LastMessage lastMessage, Timestamp createdAt, Set<String> users) {
    super(id, userCover, lastMessage, createdAt, users);
  }

  public static PrivateChat createFromDatabase(DocumentSnapshot document, String userEmail) {
    String id = document.getId();
    LastMessage lastMessage = null;

    List<String> userList = (List<String>) Objects.requireNonNull(document.get("users"));
    Set<String> users = new HashSet<>(userList);

    String userCover = users.stream()
      .filter(user -> !user.equals(userEmail))
      .findFirst()
      .map(user -> user.split("@")[0])
      .orElse(null);

    Map<String, Object> lastMessageDocument = (Map<String, Object>) document.get("last_message");

    if (lastMessageDocument != null) {
      Map<String, Object> lastMessageData = lastMessageDocument;
      lastMessage = LastMessage.createFromDatabase(lastMessageData);
    }

    Timestamp createdAt = document.getTimestamp("createdAt");

    return new PrivateChat(id,userCover, lastMessage, createdAt, users);
  }

  public String getOtherUser(String userEmail) {
    return getMembers().stream().filter(user -> !user.equals(userEmail)).findFirst().orElse(null);
  }
}
