package com.team41.boromi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team41.boromi.constants.CommonConstants.BookStatus;
import com.team41.boromi.dbs.BookDB;
import com.team41.boromi.models.Book;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class BookDBTest {

  BookDB bookDB;
  ArrayList<Book> testBooks = new ArrayList<>();

  @Before
  public void setup() {
    bookDB = new BookDB(FirebaseFirestore.getInstance());

    // Push a book to firestore so each test has something to work with
    // This assumes correct functionality of the pushBook method but
    // I didn't know a better way to automate the process of setting up
    // A consistent testing environment
    Book book = new Book(
        "DaGQbX2HLfditCUDKq9X1bRnXQ82",
        "This should be Acepted",
        "JK Rowling",
        "123456789012"
    );
    book.setStatus(BookStatus.ACCEPTED);
    testBooks.add(bookDB.pushBook(book));
  }

  @After
  public void tearDown() {
    // Deletes all the test books from firestore to rollback the changes made during the test
    // This assumes correct functionality of the deleteBook method but
    // I didn't know a better way to automate the process of setting up
    // A consistent testing environment
//		for (Book book : testBooks)
//			bookDB.deleteBook(book.getBookId());

    testBooks.clear();
  }

  @Test
  public void testPushBook() {
    Book book = new Book("testuser1", "The Hobbit", "JK Rowling", "123456789012");
    Book resultBook = bookDB.pushBook(book);
    testBooks.add(resultBook);

    assertNotNull(resultBook);
  }

  @Test
  public void testGetUsersOwnedBooks() {
    ArrayList<Book> ownedBooks = bookDB.getUsersOwnedBooks("testuser1");
    assertEquals(1, ownedBooks.size(), 0);
  }

  @Test
  public void testDeleteBook() {
    boolean deleteSuccessful = bookDB.deleteBook(testBooks.get(0).getBookId());
    assertTrue(deleteSuccessful);
  }

  @Test
  public void testEditBook() {
    // Modifies the book created in setup
    Book book = testBooks.get(0);
    book.setAuthor("Brock Chelle");

    Book resultBook = bookDB.pushBook(book);
    assertNotNull(resultBook);
  }

  @Test
  public void testFunctionalityTogether() {
    // Starts by reading the users books, ensuring that the count is one
    ArrayList<Book> ownedBooks = bookDB.getUsersOwnedBooks("testuser1");
    assertEquals(1, ownedBooks.size(), 0);

    Book book = ownedBooks.get(0);
    assertEquals("testuser1", book.getOwner());

    // Edit the author of the book push the change to firebase
    book.setAuthor("Brock Chelle");
    bookDB.pushBook(book);

    // Gets the modified book from firestore
    ownedBooks = bookDB.getUsersOwnedBooks("testuser1");
    assertEquals(1, ownedBooks.size());

    // Asserts that the edit was successful
    book = ownedBooks.get(0);
    assertEquals("Brock Chelle", book.getAuthor());

    // Pushes a new book to firestore
    testBooks.add(bookDB.pushBook(new Book(
        "testuser1",
        "Harry Potter",
        "J.R.R. Tolkien",
        "8167893451982"
    )));

    // Gets the new books from firestore to ensure the count was incremented
    ownedBooks = bookDB.getUsersOwnedBooks("testuser1");
    assertEquals(2, ownedBooks.size());

    // Deletes a book from firestore
    boolean deleteSuccessful = bookDB.deleteBook(testBooks.get(1).getBookId());
    assertTrue(deleteSuccessful);

    // Gets the updated books from firestore to ensure the count was decremented
    ownedBooks = bookDB.getUsersOwnedBooks("testuser1");
    assertEquals(1, ownedBooks.size());
  }
}
