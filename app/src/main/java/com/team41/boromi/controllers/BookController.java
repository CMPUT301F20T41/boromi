package com.team41.boromi.controllers;

import static com.team41.boromi.constants.CommonConstants.BookStatus;
import static com.team41.boromi.utility.Utility.isNotNullOrEmpty;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import androidx.annotation.NonNull;
import com.team41.boromi.callbacks.BookCallback;
import com.team41.boromi.constants.CommonConstants.BookWorkflowStage;
import com.team41.boromi.dbs.BookDB;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.commons.lang3.StringUtils;

@Singleton
public class BookController {

  private final static String TAG = "BookController";
  protected BookStatus status;
  protected BookWorkflowStage workflow;
  protected Executor executor;

  BookDB bookDB;
  User user;

  @Inject
  public BookController(BookDB bookDB, Executor executor, User user) {
    this.bookDB = bookDB;
    this.executor = executor;
    this.user = user;
  }

  /**
   * Adds Book to DB asynchronously. On Success or Failure, it will have a callback to let the ui
   * know
   *
   * @param owner
   * @param author
   * @param ISBN
   * @param title
   */
  public void addBook(String owner, String author, String ISBN, String title, String image,
      final BookCallback bookCallback) {
    if (isNotNullOrEmpty(author) && isNotNullOrEmpty(ISBN) && isNotNullOrEmpty(title)) {
      Book addingBook = new Book(owner, title, author, ISBN);
      addingBook.setStatus(status.AVAILABLE);
      addingBook.setWorkflow(workflow.AVAILABLE);
      addingBook.setImg64(image);
      executor.execute(() -> {
        ArrayList<Book> addedBook = new ArrayList<>();
        addedBook.add(bookDB.pushBook(addingBook));
        if (addedBook != null) {
          Log.d(TAG, " book add success");
          bookCallback.onSuccess(addedBook);
        } else {
          Log.d(TAG, " book add error");
          bookCallback.onFailure(new IllegalArgumentException());
        }
      });
    } else {
      Log.d(TAG, " Error in one of the columns");
      bookCallback.onFailure(new IllegalArgumentException());
    }
  }

  public void addBook(String author, String ISBN, String title, Bitmap image,
      final BookCallback bookCallback) {
    if (isNotNullOrEmpty(author) && isNotNullOrEmpty(ISBN) && isNotNullOrEmpty(title)) {
      Book addingBook = new Book(user.getUUID(), title, author, ISBN);
      addingBook.setStatus(status.AVAILABLE);
      addingBook.setWorkflow(workflow.AVAILABLE);

      addingBook.setImg64(encodeToBase64(image));

      executor.execute(() -> {
        ArrayList<Book> addedBook = new ArrayList<>();
        addedBook.add(bookDB.pushBook(addingBook));
        if (addedBook != null) {
          Log.d(TAG, " book add success");
          bookCallback.onSuccess(addedBook);
        } else {
          Log.d(TAG, " book add error");
          bookCallback.onFailure(new IllegalArgumentException());
        }
      });
    } else {
      Log.d(TAG, " Error in one of the columns");
      bookCallback.onFailure(new IllegalArgumentException());
    }
  }

  /**
   * same add function as above, but this time owner is set automatically by whoever is logged in
   * Just some polymorphism to have less UI handling logic
   */
  public void addBook(String author, String ISBN, String title, String image,
      final BookCallback bookCallback) {
    if (isNotNullOrEmpty(author) && isNotNullOrEmpty(ISBN) && isNotNullOrEmpty(title)) {
      Book addingBook = new Book(user.getUUID(), title, author, ISBN);
      addingBook.setStatus(status.AVAILABLE);
      addingBook.setWorkflow(workflow.AVAILABLE);
      addingBook.setImg64(image);
      executor.execute(() -> {
        ArrayList<Book> addedBook = new ArrayList<>();
        addedBook.add(bookDB.pushBook(addingBook));
        if (addedBook != null) {
          Log.d(TAG, " book add success");
          bookCallback.onSuccess(addedBook);
        } else {
          Log.d(TAG, " book add error");
          bookCallback.onFailure(new IllegalArgumentException());
        }
      });
    } else {
      Log.d(TAG, " Error in one of the columns");
      bookCallback.onFailure(new IllegalArgumentException());
    }
  }

  /**
   * Edits book description by getting book from BookDB
   *
   * @param bookID
   * @param author
   * @param ISBN
   * @param title
   * @return
   */
  public void editBook(String bookID, String author, String ISBN, String title,
      final BookCallback bookCallback) {
    if (isNotNullOrEmpty(author) && isNotNullOrEmpty(ISBN) && isNotNullOrEmpty(title)) {
      executor.execute(() -> {
        ArrayList<Book> edited = new ArrayList<>();
        Book editingBook = bookDB.getBook(bookID);
        if (editingBook != null) {
          editingBook.setTitle(title);
          editingBook.setAuthor(author);
          editingBook.setISBN(ISBN);
          edited.add(bookDB.pushBook(editingBook));
          Log.d(TAG, " book edit success");

          bookCallback.onSuccess(edited);
        } else {
          Log.d(TAG, " book edit error");
          bookCallback.onFailure(new IllegalArgumentException());
        }
      });
    }
  }

  /**
   * Get Owner's Books
   *
   * @param owner
   */
  public void getOwnedBooks(String owner, final BookCallback bookCallback) {
    if (isNotNullOrEmpty(owner)) {
      executor.execute(() -> {
        ArrayList<Book> ownedBooks = bookDB.getUsersOwnedBooks(owner);
        if (ownedBooks != null) {
          Log.d(TAG, " get owner books success");
          bookCallback.onSuccess(ownedBooks);
        } else {
          Log.d(TAG, " get owner books error");
          bookCallback.onFailure(new IllegalArgumentException());
        }
      });
    } else {
      Log.d(TAG, " Error in one of the columns");
      bookCallback.onFailure(new IllegalArgumentException());
    }
  }

  /**
   * Same as above, just already been configured for logged in useer
   */
  public void getOwnedBooks(final BookCallback bookCallback) {
    executor.execute(() -> {
      ArrayList<Book> ownedBooks = bookDB.getUsersOwnedBooks(user.getUUID());
      if (ownedBooks != null) {
        Log.d(TAG, " get owner books success");
        bookCallback.onSuccess(ownedBooks);
      } else {
        Log.d(TAG, " get owner books error");
        bookCallback.onFailure(new IllegalArgumentException());
      }
    });
  }

  /**
   * Deletes book using bookID
   *
   * @param bookID
   * @return
   */
  public void deleteBook(String bookID, final BookCallback bookCallback) {
    if (isNotNullOrEmpty(bookID)) {
      executor.execute(() -> {
        Boolean result = bookDB.deleteBook(bookID);
        if (result) {
          Log.d(TAG, " book delete success");
          bookCallback.onSuccess(null);
        } else {
          Log.d(TAG, " book delete error");
          bookCallback.onFailure(new IllegalArgumentException());
        }
      });
    } else {
      Log.d(TAG, " Error in one of the columns");
      bookCallback.onFailure(new IllegalArgumentException());
    }
  }

  /**
   * Gets all the books from book DB and searches by comparing title and keyword
   *
   * @param keywords
   * @return
   */
  public void findBooks(String keywords, final BookCallback bookCallback) {
    ArrayList<Book> searchedBooks = new ArrayList<Book>();
    if (isNotNullOrEmpty(keywords)) {
      executor.execute(() -> {
        ArrayList<Book> allBooks = bookDB.getAllBooks();
        if (allBooks != null) {
          for (Book eachBook : allBooks) {
            String title = eachBook.getTitle();
            if (StringUtils.containsIgnoreCase(title, keywords)) {
              searchedBooks.add(eachBook);
            }
          }
          if (searchedBooks.size() > 0) {
            bookCallback.onSuccess(searchedBooks);
          } else {
            bookCallback.onFailure(new NullPointerException());
          }
        } else {
          Log.d(TAG, " Error in one of the columns");
          bookCallback.onFailure(new IllegalArgumentException());
        }
      });
    } else {
      Log.d(TAG, "Keyword is empty or null");
      bookCallback.onFailure(new IllegalArgumentException());
    }
  }

  /**
   * Gets all the owner books that are requested
   *
   * @param owner
   * @return
   */
  public void getOwnerRequestedBooks(String owner, final BookCallback bookCallback) {
    if (isNotNullOrEmpty(owner)) {
      executor.execute(() -> {
        ArrayList<Book> requestedBooks = bookDB.getOwnerRequestedBooks(owner);
        if (requestedBooks != null) {
          Log.d(TAG, " get requested books success");
          bookCallback.onSuccess(requestedBooks);
        } else {
          Log.d(TAG, " get requested books error");
          bookCallback.onFailure(new IllegalArgumentException());
        }
      });
    } else {
      Log.d(TAG, " Error in one of the columns");
      bookCallback.onFailure(new IllegalArgumentException());
    }
  }

  /**
   * Gets all the owner books that are borrowed
   *
   * @param owner
   * @return
   */
  public void getOwnerBorrowedBooks(String owner, final BookCallback bookCallback) {
    if (isNotNullOrEmpty(owner)) {
      executor.execute(() -> {
        ArrayList<Book> borrowedBooks = bookDB.getOwnerBorrowedBooks(owner);
        if (borrowedBooks != null) {
          Log.d(TAG, " get borrowed books success");
          bookCallback.onSuccess(borrowedBooks);
        } else {
          Log.d(TAG, " get borrowed books error");
          bookCallback.onFailure(new IllegalArgumentException());
        }
      });
    } else {
      Log.d(TAG, " Error in one of the columns");
      bookCallback.onFailure(new IllegalArgumentException());
    }
  }

  /**
   * Gets all the owner books that are accepted
   *
   * @param owner
   * @return
   */
  public void getOwnerAcceptedBooks(String owner, final BookCallback bookCallback) {
    if (isNotNullOrEmpty(owner)) {
      executor.execute(() -> {
        ArrayList<Book> acceptedBooks = bookDB.getOwnerAcceptedBooks(owner);
        if (acceptedBooks != null) {
          Log.d(TAG, " get accepted books success");
          bookCallback.onSuccess(acceptedBooks);
        } else {
          Log.d(TAG, " get accepted books error");
          bookCallback.onFailure(new IllegalArgumentException());
        }
      });
    } else {
      Log.d(TAG, " Error in one of the columns");
      bookCallback.onFailure(new IllegalArgumentException());
    }
  }

  /**
   * Gets all the owner books that are available
   *
   * @param owner
   * @return
   */
  public void getOwnerAvailableBooks(String owner, final BookCallback bookCallback) {
    if (isNotNullOrEmpty(owner)) {
      executor.execute(() -> {
        ArrayList<Book> availableBooks = bookDB.getOwnerAvailableBooks(owner);
        if (availableBooks != null) {
          Log.d(TAG, " get available books success");
          bookCallback.onSuccess(availableBooks);
        } else {
          Log.d(TAG, " get available books error");
          bookCallback.onFailure(new IllegalArgumentException());
        }
      });
    } else {
      Log.d(TAG, " Error in one of the columns");
      bookCallback.onFailure(new IllegalArgumentException());
    }
  }

  /**
   * A function that confirms a book has been borrowed and not just "accepted" dont pass a null book
   * here
   */
  public void confirmBookReceived(@NonNull Book book) {
    if (book.getBorrower() == user.getUUID()) {
      book.setStatus(BookStatus.BORROWED);
      bookDB.pushBook(book);
    } else {
      Log.d(TAG, "Bad Request on" + book.getBookId());
      Log.d(TAG, "This should never happen, confirmed book that wasn't your borrowed");
      throw new RuntimeException("Confirmed you received a book that isn't yours");
    }
  }

  /**
   * this is a synchronous task except for the pushing to db portion This might take a while, so it
   * may be worth to show user a spinny circly
   *
   * @param bmap
   * @param book
   */
  // TODO ENHANCE LATER WITH INTEGRATION EDIT BOOK
  public void addPhotoToBook(Bitmap bmap, Book book) {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
    String base64img = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    book.setImg64(base64img);
    executor.execute(() -> {
      bookDB.pushBook(book);
    });
    bmap.recycle();
  }

  public String encodeToBase64(Bitmap bmap) {

    if (bmap == null) {
      return null;
    }

    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
    String base64img = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);
    bmap.recycle();
    return base64img;
  }

  /**
   * This should probably be in editBook.
   *
   * @param base64img
   * @param book
   */
  public void addBase64PhotoToBook(String base64img, Book book) {
    book.setImg64(base64img);
    // TODO SHOULD NOT PUSH UNLESS USER SAVES EDIT BOOK
    executor.execute(() -> {
      bookDB.pushBook(book);
    });
  }

  public Bitmap decodeBookImage(Book book) {
    if (book == null || book.getImg64() == null) {
      return null;
    }
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
        Base64.decode(book.getImg64(), Base64.DEFAULT));
    return BitmapFactory.decodeStream(byteArrayInputStream);
  }

  /***
   * just sets a book img to null, but actually you still need to update the book somehow
   * @param book
   */
  public void deleteBookImage(Book book) {
    book.setImg64(null);
    executor.execute(() -> {
      bookDB.pushBook(book);
    });
  }
}
