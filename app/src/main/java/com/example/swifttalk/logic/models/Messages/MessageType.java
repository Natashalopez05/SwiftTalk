package com.example.swifttalk.logic.models.Messages;

public enum MessageType {
  IMAGE("image"),
  TEXT("text"),
  VIDEO("video");

  private final String type;

  MessageType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }

  public static MessageType fromString(String text) {
    for (MessageType messageType : MessageType.values()) {
      if (messageType.type.equalsIgnoreCase(text)) {
        return messageType;
      }
    }
    throw new IllegalArgumentException("Invalid message type: " + text);
  }
}