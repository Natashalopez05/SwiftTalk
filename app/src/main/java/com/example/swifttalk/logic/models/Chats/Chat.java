package com.example.swifttalk.logic.models.Chats;
import com.example.swifttalk.logic.models.Messages.LastMessage;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Objects;
import java.util.Set;

public abstract class Chat {
  private String id;
  private String userCover;
  private LastMessage lastMessage;
  private Timestamp timestamp;
  private Set<String> users;

  public Chat(String id, String userCover, LastMessage lastMessage, Timestamp timestamp, Set<String> users) {
    this.id = id;
    this.userCover = userCover;
    this.lastMessage = lastMessage;
    this.timestamp = timestamp;
    this.users = users;
  }

  public Chat() {
  }

  public String getId() {
    return id;
  }

  public String getUserCover() {
    return userCover;
  }

  public LastMessage getLastMessage() {
    return lastMessage;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public Set<String> getMembers() {
    return users;
  }

  public static Chat createFromDatabase(DocumentSnapshot document, String userEmail) {
    int amountOfUsers = Objects.requireNonNull(document.get("users")).toString().split(",").length;
    return PrivateChat.createFromDatabase(document, userEmail);

    //TODO IF GREATER THAN 2 THEN GROUP CHAT
  }
}
