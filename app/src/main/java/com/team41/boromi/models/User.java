package com.team41.boromi.models;

import androidx.annotation.NonNull;

/**
 * A user class. Each user class must have a username assigned
 */
public class User implements OwnerInterface, BorrowerInterface {

  @NonNull
  private final String username;
  @NonNull
  private final String email;
  @NonNull
  private final String UUID;

  /**
   * constructor
   *
   * @param username
   * @param email
   */
  public User(String UUID, String username, String email) {
    this.UUID = UUID;
    this.username = username;
    this.email = email;
  }


  // getter start
  @NonNull
  public String getUsername() {
    return username;
  }

  @NonNull
  public String getEmail() {
    return email;
  }

  @NonNull
  public String getUUID() {
    return UUID;
  }
}