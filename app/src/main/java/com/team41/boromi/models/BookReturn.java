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

  /**
   * Constructor that generates random returnId
   *
   * @param returnee
   * @param bookId
   */
  public BookReturn(String bookId, String owner, String returnee) {
    this.returnee = returnee;
    this.bookId = bookId;
    this.owner = owner;
  }

  public BookReturn(Book book, String returnee) {
    this.bookId = book.getBookId();
    this.owner = book.getOwner();
    this.returnee = returnee;
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

}
