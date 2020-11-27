package com.team41.boromi.models;

import com.google.android.gms.maps.model.LatLng;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Class representing a request made for a book.
 */
public class BookRequest implements Serializable {

  private static final long serialVersionUID = 8005430L; // bookReq lookin ish haha

  private String requestId = UUID.randomUUID().toString();
  private String requestor;
  private String requestorName;
  private String bookId;
  private Date requestDate;
  private String owner;
  private LatLng location;
  // TODO
  // Again I did not include the status to allow the person implementing to make the choice
  // on the logic


  /**
   * Constructor with requestorNamee requestId.
   *
   * @param requestor username
   * @param bookId    bookUuid
   */
  public BookRequest(String requestorName, String requestor, String bookId, String owner) {
    this.requestorName = requestorName;
    this.requestor = requestor;
    this.bookId = bookId;
    this.requestDate = new Date();
    this.owner = owner;
  }

  public BookRequest() {
  }

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

  public void setRequestDate(Date requestDate) {
    this.requestDate = requestDate;
  }

  public String getOwner() {
    return owner;
  }

  public String getRequestorName() {
    return requestorName;
  }

  public void setRequestorName(String requestorName) {
    this.requestorName = requestorName;
  }

  public LatLng getLocation() {
    return location;
  }

  public void setLocation(LatLng location) {
    this.location = location;
  }

}
