package com.team41.boromi.models;

import com.google.android.gms.maps.model.LatLng;
import java.io.Serializable;
import java.util.Date;

/**
 * A class showing intent to return book
 */
public class BookReturn implements Serializable {

  private static final long serialVersionUID = 8003437042L;

  private String returnee;
  private String owner;
  private String bookId;
  private Date returnDate;
  private LatLng location;

  /**
   * Constructor that generates random returnId
   *
   * @param returnee
   * @param bookId
   */
  public BookReturn(String bookId, String owner, String returnee, Date returnDate,
      LatLng location) {
    this.returnee = returnee;
    this.bookId = bookId;
    this.owner = owner;
    // TODO change to date of meetup
    this.returnDate = new Date();
    // TODO change to actual location later
    this.location = null;
  }

  public BookReturn(Book book, String returnee, Date returnDate, LatLng location) {
    this.bookId = book.getBookId();
    this.owner = book.getOwner();
    this.returnee = returnee;
    // TODO change to date of meetup
    this.returnDate = new Date();
    // TODO change to actual location later
    this.location = null;
  }

  public BookReturn() {
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public String getReturnee() {
    return returnee;
  }

  public String getOwner() {
    return owner;
  }

  public String getBookId() {
    return bookId;
  }

  public Date getReturnDate() {
    return returnDate;
  }

  public LatLng getLocation() {
    return location;
  }

  public void setLocation(LatLng location) {
    this.location = location;
  }
}
