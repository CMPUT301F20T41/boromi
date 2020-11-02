package com.team41.boromi;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.team41.boromi.callbacks.BookCallback;
import com.team41.boromi.callbacks.BookRequestCallback;
import com.team41.boromi.controllers.BookController;
import com.team41.boromi.controllers.BookRequestController;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.team41.boromi.constants.CommonConstants.DB_TIMEOUT;
import static com.team41.boromi.constants.CommonConstants.REQUEST_IMAGE_CAPTURE;

public class TestRequestBookActivity extends AppCompatActivity {
  private static final String TAG = "TestRequestBookActivity";

  ImageView testImgView;

  @Inject
  BookController bookController;
  @Inject
  BookRequestController bookRequestController;

  Book selectedBook;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_test_request_book);

    ((BoromiApp) getApplicationContext()).appComponent.inject(this);

    testImgView = findViewById(R.id.testImageView);
//    Book iWantThisBook = new Book("q72ERIcWNyXpZRjt3XAowwn4hJ22", "MINGDUMMY2");
//    try {
//      Field bookIdField = iWantThisBook.getClass().getDeclaredField("bookId");
//      bookIdField.setAccessible(true);
//      bookIdField.set(iWantThisBook, "MINGDUMMY1");
//    } catch (NoSuchFieldException | IllegalAccessException e) {
//      e.printStackTrace();
//      throw new RuntimeException("CANT SET THE BOOK ID FOR THIS EXAMPLE");
//    }
//
//    bookRequestController.makeRequestOnBook(iWantThisBook);
//    try {
//      Thread.sleep(DB_TIMEOUT);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
//
//    bookRequestController.getRequestedBooks(new BookRequestCallback() {
//      @Override
//      public void onComplete(List<BookRequest> bookRequests, Map<String, Book> bookMap) {
//        for (BookRequest br : bookRequests) {
//          Log.d(TAG, br.getRequestId());
//          Log.d(TAG, bookMap.get(br.getBookId()).getBookId());
//        }
//      }
//    });
//
//    bookRequestController.getRequestOnOwnedBooks(new BookRequestCallback() {
//      @Override
//      public void onComplete(List<BookRequest> bookRequests, Map<String, Book> bookMap) {
//      }
//    });
//
//    BookRequest acceptingThisBR = new BookRequest("rG2jAk3j1EcoTzCs9auojLPd1gI3", "MINGDUMMY1", "q72ERIcWNyXpZRjt3XAowwn4hJ22");
//    bookRequestController.acceptBookRequest(acceptingThisBR);
//
//    try {
//      Thread.sleep(1000);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
//  }
    // start of photo testing

    selectedBook = new Book("MingPhotoBook", "MINGPHOTOBOOK");

    testImgView.setScaleType(ImageView.ScaleType.FIT_XY);
    dispatchTakePictureIntent();

  }


  private void dispatchTakePictureIntent() {
    if(selectedBook == null) {
      // user MUST have selected book first.
      throw new RuntimeException("nope no book selected");
    }
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    try {
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    } catch (ActivityNotFoundException e) {
      // display error state to the user
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
      Bundle extras = data.getExtras();
      Bitmap imageBitmap = (Bitmap) extras.get("data");
      bookController.addPhotoToBook(imageBitmap, selectedBook);
    }

    try {
      Thread.sleep(5000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // now lets try to get book, decode it and set the image view to it
    bookController.getOwnedBooks("MingPhotoBook", new BookCallback() {
      @Override
      public void onSuccess(ArrayList<Book> books) {
        runOnUiThread(() -> {
          testImgView.setImageBitmap(bookController.decodeBookImage(books.get(0)));
        });
      }

      @Override
      public void onFailure(Exception e) {

      }
    });

  }


}