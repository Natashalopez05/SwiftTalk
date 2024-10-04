package com.example.swifttalk.logic.models.Chats;
import com.example.swifttalk.logic.models.Messages.LastMessage;
import com.google.firebase.Timestamp;
import java.util.Set;

public abstract class Chat {
  private final String id;
  private final String userCover;
  private final LastMessage lastMessage;
  private final Timestamp timestamp;
  private final Set<String> users;

  public Chat(String id, String userCover, LastMessage lastMessage, Timestamp timestamp, Set<String> users) {
    this.id = id;
    this.userCover = userCover;
    this.lastMessage = lastMessage;
    this.timestamp = timestamp;
    this.users = users;
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
}
