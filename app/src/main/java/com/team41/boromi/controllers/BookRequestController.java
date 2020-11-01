package com.team41.boromi.controllers;

import com.team41.boromi.callbacks.BookRequestCallback;
import com.team41.boromi.constants.CommonConstants;
import com.team41.boromi.dbs.BookDB;
import com.team41.boromi.dbs.BookRequestDB;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;
import com.team41.boromi.models.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Does some logic to grab books
 * This is going to be some "dumb" logic. Basically, BookRequest stores the requester and
 * requestee with the UUID. All book requests signify that there is an active request for that book.
 * Deleting the request basically means "rejecting" and
 **/
@Singleton
public class BookRequestController {
  private final static String TAG = "BookRequestController";
  final Executor executor;
  private final BookRequestDB brDB;
  private final BookDB bookDB;
  User user;

  @Inject
  public BookRequestController(Executor executor, User user, BookRequestDB brDB, BookDB bookDB) {
    this.executor = executor;
    this.user = user;
    this.brDB = brDB;
    this.bookDB = bookDB;
  }

  // Gets books that you have requested
  public void getRequestedBooks(final BookRequestCallback bookRequestCallback) {
    executor.execute(() -> {
      List<BookRequest> bookRequestsFromUser = brDB.getBookRequests(user.getUUID());
      Map<String, Book> requestedBooks = bookDB.getBooksByBookRequestList(bookRequestsFromUser);
      bookRequestCallback.onComplete(bookRequestsFromUser, requestedBooks);
    });
  }

  /**
   * Makes a request on a book
   * Async completes the request
   *
   * @param book
   */
  public void makeRequestOnBook(Book book) {
    BookRequest request = new BookRequest(user.getUUID(), book.getBookId(), book.getOwner());
    executor.execute(() -> {
      brDB.pushBookRequest(request);
    });
  }

  public void getRequestOnOwnedBooks(final BookRequestCallback bookRequestCallback) {
    executor.execute(() -> {
      List<BookRequest> bookRequests = brDB.getBookRequestsForOwner(user.getUUID());
      Map<String, Book> requestedBooks = bookDB.getBooksByBookRequestList(bookRequests);
      bookRequestCallback.onComplete(bookRequests, requestedBooks);
    });
  }

  // TODO : NEED TO NOTIFY USERS THAT BOOK IS CANCELLED OR ACCEPTED
  // remove all requests on the book and set the book to your current borrowed
  public void acceptBookRequest(BookRequest bookRequest) {
    executor.execute(() -> {
      brDB.deleteRequestsForBook(bookRequest.getBookId());
      Book acceptedBook = bookDB.getBookById(bookRequest.getBookId());
      acceptedBook.setWorkflow(CommonConstants.BookWorkflowStage.BORROWED);
      acceptedBook.setBorrower(bookRequest.getRequestor());
      bookDB.pushBook(acceptedBook);
    });
  }

  // TODO : NOTIFY USER THEIR BOOK REQUEST WAS CANCELLED
  public void declineBookRequest(BookRequest bookRequest) {
    executor.execute(() -> {
      brDB.deleteBookRequest(bookRequest.getBookId());
    });
  }
}