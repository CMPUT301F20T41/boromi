package com.team41.boromi;

import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team41.boromi.dbs.BookDB;
import com.team41.boromi.dbs.BookReturnDB;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookReturn;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.team41.boromi.constants.CommonConstants.DB_TIMEOUT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Test for BookReturnsDB
 */
public class BookReturnsDBTest {

  BookReturnDB bookReturnDB;
  BookDB bookDB;
  ArrayList<Book> books = new ArrayList<>();
  FirebaseFirestore db;

  @Before
  public void setup() {
    db = FirebaseFirestore.getInstance();
    bookDB = new BookDB(db);
    bookReturnDB = new BookReturnDB(db);
    Book book1 = new Book(UUID.randomUUID().toString(), "testBook1", "testAuthor1", "1111");
    Book book2 = new Book(UUID.randomUUID().toString(), "testBook2", "testAuthor2", "2222");
    books.add(book1);
    books.add(book2);
  }

  @After
  public void tearDown() {
    for (Book book : books) {
      try {
        Tasks.await(
                db.collection("bookReturns").document(book.getBookId()).delete(), DB_TIMEOUT,
                TimeUnit.MILLISECONDS);
        Tasks.await(db.collection("books").document(book.getBookId()).delete(), DB_TIMEOUT,
                TimeUnit.MILLISECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        Log.w("REQUSTDB TEST", e.getCause());
      }
    }
  }

  @Test
  public void addRequest() throws InterruptedException {
    for (Book b : books) {
      BookReturn bookReturn = new BookReturn(b.getBookId(), "ReturnTest", "ReturneeTest");
      bookReturnDB.addReturnRequest(bookReturn);
      BookReturn bookReturn1 = bookReturnDB.getReturnRequest(bookReturn.getBookId());
      Thread.sleep(DB_TIMEOUT);
      assertEquals(bookReturn.getBookId(), bookReturn1.getBookId());
      assertEquals(bookReturn.getOwner(), bookReturn1.getOwner());
      assertEquals(bookReturn.getReturnee(), bookReturn1.getReturnee());
    }
  }

  @Test
  public void deleteRequest() throws InterruptedException {
    Book b = books.get(0);
    BookReturn bookReturn = new BookReturn(b.getBookId(), "ReturnTest", "ReturneeTest");
    bookReturnDB.addReturnRequest(bookReturn);
    Thread.sleep(DB_TIMEOUT);
    assertNotNull(bookReturnDB.getReturnRequest(bookReturn.getBookId()));
    bookReturnDB.deleteReturnRequest(bookReturn.getBookId());
    Thread.sleep(DB_TIMEOUT);
    assertNull(bookReturnDB.getReturnRequest(bookReturn.getBookId()));
  }
}
