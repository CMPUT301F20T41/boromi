package com.team41.boromi.controllers;

import com.team41.boromi.callbacks.BookRequestCallback;
import com.team41.boromi.constants.CommonConstants;
import com.team41.boromi.constants.CommonConstants.BookWorkflowStage;
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
 * Does some logic to grab books This is going to be some "dumb" logic. Basically, BookRequest
 * stores the requester and requestee with the UUID. All book requests signify that there is an
 * active request for that book. Deleting the request basically means "rejecting" and
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
      Map<Book, List<BookRequest>> booksWithRequestList = bookDB
          .getBooksWithRequestList(bookRequestsFromUser);
      bookRequestCallback.onComplete(booksWithRequestList);
    });
  }

  /**
   * Makes a request on a book Async completes the request
   *
   * @param book
   */
  public void makeRequestOnBook(Book book) {
    BookRequest request = new BookRequest(user.getUsername(), user.getUUID(), book.getBookId(),
        book.getOwner());
    book.setStatus(CommonConstants.BookStatus.REQUESTED);
    executor.execute(() -> {
      bookDB.pushBook(book);    // update the books status
      brDB.pushBookRequest(request);
    });
  }

  public void getRequestOnOwnedBooks(final BookRequestCallback bookRequestCallback) {
    executor.execute(() -> {
      List<BookRequest> bookRequestsFromUser = brDB.getBookRequestsForOwner(user.getUUID());
      Map<Book, List<BookRequest>> booksWithRequestList = bookDB
          .getBooksWithRequestList(bookRequestsFromUser);
      bookRequestCallback.onComplete(booksWithRequestList);
    });
  }

  // TODO : NEED TO NOTIFY USERS THAT BOOK IS CANCELLED OR ACCEPTED
  // remove all requests on the book and set the book to your current borrowed
  public void acceptBookRequest(BookRequest bookRequest, BookRequestCallback bookRequestCallback) {
    executor.execute(() -> {
      brDB.deleteRequestsForBook(bookRequest.getBookId());
      Book acceptedBook = bookDB.getBookById(bookRequest.getBookId());
      acceptedBook.setStatus(CommonConstants.BookStatus.ACCEPTED);      // sets the book to accepted
      acceptedBook.setWorkflow(BookWorkflowStage.PENDINGBORROW);
      acceptedBook.setBorrower(bookRequest.getRequestor());
      bookDB.pushBook(acceptedBook);
      bookRequestCallback.onComplete(null);
    });
  }

  // TODO : NOTIFY USER THEIR BOOK REQUEST WAS CANCELLED
  // can also be used to "cancel" bookRequests
  public void declineBookRequest(BookRequest bookRequest) {
    executor.execute(() -> {
      brDB.deleteBookRequest(bookRequest.getRequestId());
    });
  }
}
