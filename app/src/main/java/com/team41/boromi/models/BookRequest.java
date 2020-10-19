package com.team41.boromi.models;

import androidx.annotation.NonNull;

import java.util.Date;
import java.util.UUID;

/** Class representing a request made for a book. */
public class BookRequest {
  @NonNull private UUID requestId;
  @NonNull private String requestor;
  @NonNull private UUID bookId;
  @NonNull private Date requestDate;

  // TODO
  // Again I did not include the status to allow the person implementing to make the choice
  // on the logic

  /**
   * Constructor given requestor and book ID, randomizing UUID and date.
   *
   * @param requestor username of the requestor
   * @param bookId bookUuid
   */
  public BookRequest(String requestor, UUID bookId) {
    this.requestor = requestor;
    this.bookId = bookId;
    this.requestId = UUID.randomUUID();
    this.requestDate = new Date();
  }

  /**
   * Constructor with deterministic requestId.
   *
   * @param requestId uuid for request
   * @param requestor username
   * @param bookId bookUuid
   */
  public BookRequest(UUID requestId, String requestor, UUID bookId) {
    this.requestor = requestor;
    this.bookId = bookId;
    this.requestId = requestId;
    this.requestDate = new Date();
  }

  // Getter / Setter start
  @NonNull
  public UUID getRequestId() {
    return requestId;
  }

  @NonNull
  public String getRequestor() {
    return requestor;
  }

  @NonNull
  public UUID getBookId() {
    return bookId;
  }

  @NonNull
  public Date getRequestDate() {
    return requestDate;
  }

  public void setRequestDate(@NonNull Date requestDate) {
    this.requestDate = requestDate;
  }
}
