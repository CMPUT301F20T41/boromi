package com.team41.boromi.models;

import androidx.annotation.NonNull;
import java.util.Date;
import java.util.UUID;

/**
 * A class showing intent to return book
 */
public class BookReturn {

  @NonNull
  private final UUID returnId;
  @NonNull
  private final String returnee;
  @NonNull
  private final UUID bookId;
  @NonNull
  private final Date returnDate;

  /**
   * Constructor that generates random returnId
   *
   * @param returnee
   * @param bookId
   */
  public BookReturn(@NonNull String returnee, @NonNull UUID bookId) {
    this.returnee = returnee;
    this.bookId = bookId;
    this.returnId = UUID.randomUUID();
    this.returnDate = new Date();
  }

  /**
   * Deterministic returnId
   *
   * @param returnId uuid of return id
   * @param returnee username
   * @param bookId   bookuuid
   */
  public BookReturn(@NonNull UUID returnId, @NonNull String returnee, @NonNull UUID bookId) {
    this.returnId = returnId;
    this.returnee = returnee;
    this.bookId = bookId;
    this.returnDate = new Date();
  }
}
