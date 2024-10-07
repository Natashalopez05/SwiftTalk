package com.example.swifttalk.logic.models;

import com.google.firebase.firestore.DocumentSnapshot;

public class User {
  private String id;
  private String email;
  private String name;
  private String fmcToken;

  public User(String id, String email, String name, String fmcToken) {
    this.id = id;
    this.email = email;
    this.name = name;
    this.fmcToken = fmcToken;
  }

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public String getFmcToken() {
    return fmcToken;
  }

  public void setFmcToken(String fmcToken) {
    this.fmcToken = fmcToken;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setId(String id) {
    this.id = id;
  }

  public static User createFromDatabase(DocumentSnapshot document) {
    String id = document.getId();
    String email = document.getString("email");
    String name = document.getString("name");
    String fmcToken = document.getString("fmcToken");

    return new User(id, email, name, fmcToken);
  }
}
