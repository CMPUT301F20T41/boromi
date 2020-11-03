package com.team41.boromi.controllers;

import com.google.type.LatLng;
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
   * @param bookID
   * @param owner
   * @param returnDate
   * @param location
   * @param returnCallback
   */
  public void addReturnRequest(String bookID, String owner, Date returnDate, LatLng location,
      final ReturnCallback returnCallback) {
    final BookReturn bookReturn = new BookReturn(bookID, owner, currentUser.getUUID().toString(),
        returnDate, location);
    executor.execute(() -> {
      if (returnDB.addReturnRequest(bookReturn)) {
        // RETURN REQUEST SUCCESS
        Book book = bookDB.getBook(bookID);
        if (book != null) {
          book.setWorkflow(BookWorkflowStage.PENDINGRETURN);
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
        // RETURN REQUEST FAIL
        returnCallback.onFailure();
      }
    });
  }

  /**
   * Cancels a return request. Removes from bookrequest db and updates workflow to Borrowed
   *
   * @param bookID
   * @param returnCallback
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
   * @param bookID
   * @param returnCallback
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
  // get your returns
//  public ArrayList<Book> getYourRequestedReturns() {
//    executor.execute(() -> {
//
//    });
//  }
}
