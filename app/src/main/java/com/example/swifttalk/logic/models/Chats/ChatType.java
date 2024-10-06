package com.example.swifttalk.logic.models.Chats;

public enum ChatType {
  GROUP("group"),
  PRIVATE("private");

  private final String type;

  ChatType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
