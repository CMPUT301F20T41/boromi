package com.team41.boromi.models;

import androidx.annotation.NonNull;

/** A user class. Each user class must have a username assigned */
public class User implements OwnerInterface, BorrowerInterface {
  @NonNull private String username;
  private String email;

  /**
   * constructor
   *
   * @param username
   */
  public User(String username) {
    this.username = username;
  }

  /**
   * constructor
   *
   * @param username
   * @param email
   */
  public User(String username, String email) {
    this.username = username;
    this.email = email;
  }

  @NonNull
  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}
