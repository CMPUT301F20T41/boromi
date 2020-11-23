package com.team41.boromi.controllers;

import com.google.android.gms.maps.model.LatLng;
import com.team41.boromi.callbacks.ReturnCallback;
import com.team41.boromi.constants.CommonConstants.BookStatus;
import com.team41.boromi.constants.CommonConstants.BookWorkflowStage;
import com.team41.boromi.dbs.BookDB;
import com.team41.boromi.dbs.BookReturnDB;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookReturn;
import com.team41.boromi.models.User;
import java.util.Date;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This Controller deals with logic that involving returning a book. NOT IMPLEMENTED YET
 */
@Singleton
public class BookReturnController {

  private final static String TAG = "ReturnController";
  final Executor executor;
  BookReturnDB returnDB;
  BookDB bookDB;
  User currentUser;

  @Inject
  BookReturnController(BookReturnDB bookReturnDB, BookDB bookDB, Executor executor, User user) {
    this.executor = executor;
    this.bookDB = bookDB;
    returnDB = bookReturnDB;
    currentUser = user;
  }

  /**
   * Adds an entry to bookreturn db and updated book workflow to pending return
   *
   * @param book           book to return
   * @param returnDate     return date
   * @param location       location to return
   * @param returnCallback callback to execute success or failure
   */
  public void addReturnRequest(Book book, Date returnDate, LatLng location,
      final ReturnCallback returnCallback) {
    final BookReturn bookReturn = new BookReturn(book.getBookId(), book.getOwner(),
        currentUser.getUUID().toString(),
        returnDate, location);
    executor.execute(() -> {
      if (returnDB.addReturnRequest(bookReturn)) {
        // RETURN REQUEST SUCCESS
        Book updatedBook = bookDB.getBook(book.getBookId());
        if (updatedBook != null) {
          updatedBook.setWorkflow(BookWorkflowStage.PENDINGRETURN);
          if (bookDB.pushBook(updatedBook) == null) {
            returnCallback.onFailure();
            return;
          }
        } else {
          returnCallback.onFailure();
          return;
        }
        returnCallback.onSuccess();
      } else {
        // RETURN REQUEST FAIL
        returnCallback.onFailure();
      }
    });
  }

  /**
   * Cancels a return request. Removes from bookrequest db and updates workflow to Borrowed
   *
   * @param bookID         id of book to cancel
   * @param returnCallback callback to execute success or failure
   */
  public void cancelReturnRequest(String bookID, final ReturnCallback returnCallback) {
    executor.execute(() -> {
      if (returnDB.deleteReturnRequest(bookID)) {
        // RETURN CANCEL REQUEST SUCCESS
        Book book = bookDB.getBook(bookID);
        if (book != null) {
          book.setWorkflow(BookWorkflowStage.BORROWED);
          if (bookDB.pushBook(book) == null) {
            returnCallback.onFailure();
            return;
          }
        } else {
          returnCallback.onFailure();
          return;
        }
        returnCallback.onSuccess();

      } else {
        // RETURN CANCEL REQUEST FAIL
        returnCallback.onFailure();
      }
    });
  }

  /**
   * Accepts a return request. Removes from bookrequest db and updates bookdb
   *
   * @param bookID         id of the book
   * @param returnCallback callback to execute success or failure
   */
  public void acceptReturnRequest(String bookID, final ReturnCallback returnCallback) {
    executor.execute(() -> {
      if (returnDB.deleteReturnRequest(bookID)) {
        // RETURN ACCEPT RETURN SUCCESS
        Book book = bookDB.getBook(bookID);
        if (book != null) {
          book.setWorkflow(BookWorkflowStage.AVAILABLE);
          book.setStatus(BookStatus.AVAILABLE);
          book.setBorrower(null);
          if (bookDB.pushBook(book) == null) {
            returnCallback.onFailure();
            return;
          }
        } else {
          returnCallback.onFailure();
          return;
        }
        returnCallback.onSuccess();
      } else {
        // RETURN ACCEPT RETURN FAIL
        returnCallback.onFailure();
      }
    });
  }
}
