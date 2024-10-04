package com.example.swifttalk.logic.models.Chats;
import com.example.swifttalk.logic.models.Messages.LastMessage;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import org.parceler.Parcel;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Parcel
public class PrivateChat extends Chat {

  public PrivateChat(String id, String userCover, LastMessage lastMessage, Timestamp createdAt, Set<String> users) {
    super(id, userCover, lastMessage, createdAt, users);
  }

  public static PrivateChat createFromDatabase(DocumentSnapshot document, String userEmail) {
    String id = document.getId();

    List<String> userList = (List<String>) Objects.requireNonNull(document.get("users"));
    Set<String> users = new HashSet<>(userList);

    String userCover = users.stream().filter(user -> !user.equals(userEmail)).findFirst().orElse(null);

    DocumentSnapshot lastMessageDocument = (DocumentSnapshot) Objects.requireNonNull(document.get("last_Message"));
    LastMessage lastMessage = LastMessage.createFromDatabase(lastMessageDocument);

    Timestamp createdAt = document.getTimestamp("createdAt");

    return new PrivateChat(id,userCover, lastMessage, createdAt, users);
  }

  public String getOtherUser(String userEmail) {
    return getMembers().stream().filter(user -> !user.equals(userEmail)).findFirst().orElse(null);
  }
}
