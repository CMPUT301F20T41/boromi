package com.team41.boromi.controllers;

import android.util.Log;


import com.google.firebase.firestore.FirebaseFirestore;

import com.team41.boromi.callbacks.BookCallback;
import com.team41.boromi.constants.CommonConstants.BookWorkflowStage;
import com.team41.boromi.models.Book;
import com.team41.boromi.dbs.BookDB;
import static com.team41.boromi.constants.CommonConstants.BookStatus;
import static com.team41.boromi.utility.Utility.isNotNullOrEmpty;

import org.apache.commons.lang3.StringUtils; // For case sensitive keyword search

import java.util.ArrayList;
import java.util.concurrent.Executor;

import javax.inject.Inject;

public class BookController {

    private final static String TAG = "BookController";
    protected BookStatus status;
    protected BookWorkflowStage workflow;
    protected Executor executor;

    FirebaseFirestore db;
    BookDB bookDB;

    @Inject
    public BookController(BookDB bookDB, Executor executor, FirebaseFirestore db){
        this.bookDB = bookDB;
        this.executor = executor;
        this.db = db;
    }

    /**
     * Adds Book to DB asynchronously. On Success or Failure, it will have a callback to let the ui know
     * @param owner
     * @param author
     * @param ISBN
     * @param title
     */
    public void addBook(String owner, String author, String ISBN, String title, final BookCallback bookCallback) {
        if(isNotNullOrEmpty(author) && isNotNullOrEmpty(ISBN) && isNotNullOrEmpty(title)) {
            Book addingBook = new Book(owner, title, author, ISBN);
            addingBook.setStatus(status.AVAILABLE);
            addingBook.setWorkflow(workflow.AVAILABLE);
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
     * @param bookID
     * @param author
     * @param ISBN
     * @param title
     * @return
     */
    public void editBook(String bookID, String author, String ISBN, String title, final BookCallback bookCallback){
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
     * @param owner
     */
    public void getOwnedBooks(String owner, final BookCallback bookCallback){
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
     * Deletes book using bookID
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
     * @param keywords
     * @return
     */
    public void findBooks(String keywords, final BookCallback bookCallback) {
        ArrayList<Book> searchedBooks = new ArrayList<Book>();
        if (isNotNullOrEmpty(keywords)) {
            executor.execute(() -> {
               ArrayList<Book> allBooks = bookDB.getAllBooks();
               if (allBooks != null) {
                   for(Book eachBook : allBooks) {
                       String title = eachBook.getTitle();
                       if (StringUtils.containsIgnoreCase(title, keywords)){
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
     * @param owner
     * @return
     */
    public void getOwnerRequestedBooks(String owner, final BookCallback bookCallback){
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
     * @param owner
     * @return
     */
    public void getOwnerBorrowedBooks(String owner, final BookCallback bookCallback){
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
     * @param owner
     * @return
     */
    public void getOwnerAcceptedBooks(String owner, final BookCallback bookCallback){
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
     * @param owner
     * @return
     */
    public void getOwnerAvailableBooks(String owner, final BookCallback bookCallback){
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

}
