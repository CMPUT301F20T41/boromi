package com.team41.boromi.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Class representing a request made for a book.
 */
public class BookRequest implements Serializable {
  private static final long serialVersionUID = 8005430L; // bookReq lookin ish haha

  private String requestId;
  private String requestor;
  private String bookId;
  private Date requestDate;
  private String owner;
  // TODO
  // Again I did not include the status to allow the person implementing to make the choice
  // on the logic

  /**
   * Constructor given requestor and book ID, randomizing UUID and date.
   *  @param requestor username of the requestor
   * @param bookId    bookUuid
   */
  public BookRequest(String requestor, String bookId, String owner) {
    this.requestor = requestor;
    this.bookId = bookId;
    this.owner = owner;
    this.requestId = UUID.randomUUID().toString();
    this.requestDate = new Date();
  }

  /**
   * Constructor with deterministic requestId.
   *
   * @param requestId uuid for request
   * @param requestor username
   * @param bookId    bookUuid
   */
  public BookRequest(String requestId, String requestor, String bookId, String owner) {
    this.requestor = requestor;
    this.bookId = bookId;
    this.requestId = requestId;
    this.requestDate = new Date();
    this.owner = owner;
  }

  public BookRequest() {}

  // Getter / Setter start
  public String getRequestId() {
    return requestId;
  }

  public String getRequestor() {
    return requestor;
  }

  public String getBookId() {
    return bookId;
  }

  public Date getRequestDate() {
    return requestDate;
  }

  public String getOwner() {return owner;}

  public void setRequestDate(Date requestDate) {
    this.requestDate = requestDate;
  }
}
