package com.team41.boromi;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.team41.boromi.callbacks.BookRequestCallback;
import com.team41.boromi.controllers.BookRequestController;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.team41.boromi.constants.CommonConstants.DB_TIMEOUT;

public class TestRequestBookActivity extends AppCompatActivity {
  private static final String TAG = "TestRequestBookActivity";

  @Inject
  BookRequestController bookRequestController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test_request_book);


    ((BoromiApp) getApplicationContext()).appComponent.inject(this);

//    bookRequestController.req
    // Book object to get
    Book iWantThisBook = new Book("q72ERIcWNyXpZRjt3XAowwn4hJ22", "MINGDUMMY2");
    try {
      Field bookIdField = iWantThisBook.getClass().getDeclaredField("bookId");
      bookIdField.setAccessible(true);
      bookIdField.set(iWantThisBook, "MINGDUMMY1");
    } catch (NoSuchFieldException | IllegalAccessException e) {
      e.printStackTrace();
      throw new RuntimeException("CANT SET THE BOOK ID FOR THIS EXAMPLE");
    }

    bookRequestController.makeRequestOnBook(iWantThisBook);
//     give the time for the request to go through
    try {
      Thread.sleep(DB_TIMEOUT);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    bookRequestController.getRequestedBooks(new BookRequestCallback() {
      @Override
      public void onComplete(List<BookRequest> bookRequests, Map<String, Book> bookMap) {
        for (BookRequest br : bookRequests) {
          Log.d(TAG, br.getRequestId());
          Log.d(TAG, bookMap.get(br.getBookId()).getBookId());
        }
      }
    });

    // problem callback is not called
    bookRequestController.getRequestOnOwnedBooks(new BookRequestCallback() {
      @Override
      public void onComplete(List<BookRequest> bookRequests, Map<String, Book> bookMap) {
      }
    });

    BookRequest acceptingThisBR = new BookRequest("rG2jAk3j1EcoTzCs9auojLPd1gI3", "MINGDUMMY1", "q72ERIcWNyXpZRjt3XAowwn4hJ22");
//     hardcoded cus lazy
    bookRequestController.acceptBookRequest(acceptingThisBR);

    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}