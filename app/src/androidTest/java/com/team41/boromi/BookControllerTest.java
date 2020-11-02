package com.team41.boromi;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;
import com.team41.boromi.controllers.BookController;
import com.team41.boromi.dbs.BookDB;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.team41.boromi.constants.CommonConstants.BookStatus;
import static com.team41.boromi.constants.CommonConstants.DB_TIMEOUT;
import com.team41.boromi.callbacks.BookCallback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class BookControllerTest {
    private static BookDB bookDB;
    private static BookController bookController;
    private final static String TAG = "BookControllerTest";

    BookStatus status;
    ArrayList<Book> testBooks = new ArrayList<>();

    private static String brock_uuid;
    private static String ben_uuid;
    private static String ming_uuid;
    private static String andy_uuid;

    /**
     * Setting up users for uuid, books for test cases and adding them into db
     */
    @Before
    public void setup() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        bookDB = new BookDB(db);
        ExecutorService executorService = new ThreadPoolExecutor(1, 6, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>() );
        bookController = new BookController(bookDB, executorService, db);

        User lil_brock = new User("1234a", "Lil Brock", "lilbrock@gmail.com");
        User lil_ming = new User("5678b", "Lil Ming", "lilming@gmail.com");
        User lil_ben = new User("8901c", "Lil Ben", "lilben@gmail.com");
        User lil_andy = new User("2345d", "Lil Andy", "lilandy@gmail.com");

        brock_uuid = lil_brock.getUUID();
        ben_uuid = lil_ben.getUUID();
        ming_uuid = lil_ming.getUUID();
        andy_uuid = lil_andy.getUUID();

        Book booktest1 = new Book(brock_uuid, "Narnia 1", "JK Rowling", "123456789020");
        booktest1.setStatus(status.AVAILABLE);

        Book booktest2 = new Book(brock_uuid, "Narnia 2", "JK Rowling", "123456789021");
        booktest2.setStatus(status.REQUESTED);

        Book booktest3 = new Book(brock_uuid, "Narnia 3", "JK Rowling", "123456789022");
        booktest3.setStatus(status.ACCEPTED);

        Book booktest4 = new Book(brock_uuid, "Narnia 4", "JK Rowling", "123456789023");
        booktest4.setStatus(status.BORROWED);

        Book booktest5 = new Book(ming_uuid, "Harry Potter and the Chamber of Secrets", "JK Rowling", "123456789020");

        Book booktest6 = new Book(ming_uuid, "Harry Potter and the Philosopher's Stone", "JK Rowling", "123456789021");

        Book booktest7 = new Book(ming_uuid, "Toy Story 3", "Ming", "123456789022");

        Book booktest8 = new Book(ben_uuid, "Damn Ben Dipped", "Ming", "123456789023");

        testBooks.add(booktest1);
        testBooks.add(booktest2);
        testBooks.add(booktest3);
        testBooks.add(booktest4);
        testBooks.add(booktest5);
        testBooks.add(booktest6);
        testBooks.add(booktest7);
        testBooks.add(booktest8);

        for (Book eachBook : testBooks) {
            bookDB.pushBook(eachBook);
        }
    }

    /**
     * Clears DB, and array list of books
     */
    @After
    public void tearDown() {
        for (Book eachBook : testBooks) {
            bookDB.deleteBook(eachBook.getBookId());
        }
        testBooks.clear();
    }

    /**
     * Test case for adding new book and pushing to DB
     * @throws InterruptedException
     */
    @Test
    public void testAdd() throws InterruptedException {
        String author = "Test Add";
        String owner = "lilravi121";
        String ISBN = "127837122";
        String title = "FooBar";
        Log.d(TAG, "==== TEST ADD ====");
        bookController.addBook(owner, author, ISBN, title, new BookCallback() {
            @Override
            public void onSuccess(ArrayList<Book> books) {
                ArrayList<Book> test = bookDB.getUsersOwnedBooks(owner);
                assertEquals(test.size(), 1);
                bookDB.deleteBook(books.get(0).getBookId());
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "FAILED");
                fail();
            }
        });
        Thread.sleep(DB_TIMEOUT);
        Log.d(TAG, "==== END TEST ADD ====");
    }

    /**
     * Testing for edit
     * @throws InterruptedException
     */
    @Test
    public void testEdit() throws InterruptedException{
        Log.d(TAG, "==== TEST EDIT ====");
        String title = "Test Ming";
        String author = "Ming";
        String ISBN = "123456789022";
        Book booktest = new Book(ming_uuid, title, author, ISBN);
        bookDB.pushBook(booktest);
        String bookID = booktest.getBookId();
        title = "Test Edit";
        bookController.editBook(bookID, author, ISBN, title, new BookCallback() {
            @Override
            public void onSuccess(ArrayList<Book> books) {
                Book booktest = bookDB.getBook(bookID);
                Log.d(TAG, "Success");
                assertEquals(booktest.getTitle(), "Test Edit");
                bookDB.deleteBook(books.get(0).getBookId());
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "FAILED");
                fail();
            }
        });
        Thread.sleep(DB_TIMEOUT);
        Log.d(TAG, "==== END TEST EDIT ====");
    }

    /**
     * Testing for editing book and pushing to DB
     * @throws InterruptedException
     */
    @Test
    public void testDelete() throws InterruptedException {
        Log.d(TAG, "==== TEST DELETE ====");
        Book booktest = new Book("MingDelete", "Toy Story 3", "Ming", "123456789022");
        bookDB.pushBook(booktest);
        String bookID = booktest.getBookId();
        bookController.deleteBook(bookID, new BookCallback() {
            @Override
            public void onSuccess(ArrayList<Book> books) {
                Book book = bookDB.getBook(bookID);
                assertNull(book);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Success");
                Log.d(TAG, "FAILED");
                fail();
            }
        });
        Thread.sleep(DB_TIMEOUT);
        Book test = bookDB.getBookById(bookID);
        assertNull(test);
        Log.d(TAG, "==== END TEST DELETE ====");
    }

    /**
     * Testing where the filter functions work. (Filter by book statuses)
     * @throws InterruptedException
     */

    @Test
    public void testOwnerBooksByFilterStatus() throws InterruptedException {
        Log.d(TAG, "==== TEST FILTER OWNER BY STATUS ====");
        bookController.getOwnerAvailableBooks(brock_uuid, new BookCallback() {
            @Override
            public void onSuccess(ArrayList<Book> books) {
                Log.d(TAG, "Success: Lil Brock & AVAILABLE");
                assertEquals(books.size(), 1);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "FAILED");
                fail();
            }
        });
        Thread.sleep(DB_TIMEOUT);

        bookController.getOwnerRequestedBooks(brock_uuid, new BookCallback() {
            @Override
            public void onSuccess(ArrayList<Book> books) {
                Log.d(TAG, "Success: Lil Brock & REQUESTED");
                assertEquals(books.size(), 1);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "FAILED");
                fail();
            }
        });
        Thread.sleep(DB_TIMEOUT);

        bookController.getOwnerAcceptedBooks(brock_uuid, new BookCallback() {
            @Override
            public void onSuccess(ArrayList<Book> books) {
                Log.d(TAG, "Success: Lil Brock & ACCEPTED");
                assertEquals(books.size(), 1);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "FAILED");
                fail();
            }
        });
        Thread.sleep(DB_TIMEOUT);

        bookController.getOwnerBorrowedBooks(brock_uuid, new BookCallback() {
            @Override
            public void onSuccess(ArrayList<Book> books) {
                Log.d(TAG, "Success: Lil Brock & BORROWED");
                assertEquals(books.size(), 1);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "FAILED");
                fail();
            }
        });
        Thread.sleep(DB_TIMEOUT);

        bookController.getOwnerAvailableBooks(andy_uuid, new BookCallback() {
            @Override
            public void onSuccess(ArrayList<Book> books) {
                Log.d(TAG, "Success: NO RESULTS FOR LIL ANDREW");
                assertEquals(books.size(), 0);
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Failed");
                fail();
            }
        });
        Thread.sleep(DB_TIMEOUT);

        Log.d(TAG, "==== END TEST FILTER OWNER BY STATUS ====");
    }

    /**
     * Test to see if it returns owner specified books
     * @throws InterruptedException
     */
    @Test
    public void testGetOwnedBooks() throws InterruptedException {
        Log.d(TAG, "==== TEST GET BOOKS BY OWNER ====");
        bookController.getOwnedBooks(ben_uuid, new BookCallback() {
            @Override
            public void onSuccess(ArrayList<Book> books) {
                String title_1 = books.get(0).getTitle();
                Log.d(TAG, "Success, here are the results: "+ title_1);
                assertEquals(books.size(), 1);
                assertEquals(title_1, "Damn Ben Dipped");
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "FAILED!");
                fail();
            }
        });
        Thread.sleep(DB_TIMEOUT);
        Log.d(TAG, "==== END GET BOOKS BY OWNER ====");
    }

    /**
     * Test if search book function works.
     * @throws InterruptedException
     */
    @Test
    public void testFindBooks() throws InterruptedException {
        Log.d(TAG, "==== TEST FIND BOOKS USING KEYWORDS ====");
        bookController.findBooks("Harry", new BookCallback() {
            @Override
            public void onSuccess(ArrayList<Book> books) {
                Collections.sort(books, (book_1, book_2) -> book_1.getTitle().compareTo(book_2.getTitle()));
                String title_2 = books.get(1).getTitle();
                String title_1 = books.get(0).getTitle();
                Log.d(TAG, "Success! Here are the results: " + title_1 + ", " + title_2);
                assertEquals(books.size(), 2);
                assertEquals(title_2, "Harry Potter and the Philosopher's Stone");
                assertEquals(title_1, "Harry Potter and the Chamber of Secrets");
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "FAILED");
                fail();
            }
        });

        Thread.sleep(DB_TIMEOUT);

        bookController.findBooks("Story", new BookCallback() {
            @Override
            public void onSuccess(ArrayList<Book> books) {
                String title_1 = books.get(0).getTitle();
                Log.d(TAG, "Success! Here are the results :"+title_1);
                assertEquals(books.size(), 1);
                assertEquals(title_1, "Toy Story 3");
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "FAILED");
                fail();
            }
        });

        Thread.sleep(DB_TIMEOUT);

        bookController.findBooks("Ming", new BookCallback() {
            @Override
            public void onSuccess(ArrayList<Book> books) {
                Log.d(TAG, "Failed!");
                fail();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "No results");
                assertTrue(true);
            }
        });

        Thread.sleep(DB_TIMEOUT);
        Log.d(TAG, "==== END TEST FIND BOOKS USING KEYWORDS ====");
    }

}
