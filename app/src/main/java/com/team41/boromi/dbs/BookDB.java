package com.team41.boromi.dbs;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.team41.boromi.constants.CommonConstants;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.team41.boromi.constants.CommonConstants.DB_TIMEOUT;
import com.team41.boromi.constants.CommonConstants.BookStatus;

@Singleton
public class BookDB {

	private final static String TAG = "BookDB";
	private final String DB_COLLECTION = "books";
	private final FirebaseFirestore db;
	private final CollectionReference booksRef;
	protected BookStatus status;

	private final Gson gson = new Gson();

	@Inject
	public BookDB(FirebaseFirestore db) {
		this.db = db;
		booksRef = db.collection(DB_COLLECTION);
	}

	/**
	 * Attempts to get all books owned by the user
	 * @param username The username of the authenticated username
	 * @return A list of all the books owned by the user
	 */
	public ArrayList<Book> getUsersOwnedBooks(String username) {
		final ArrayList<Book> ownedBooks = new ArrayList<>();

		QuerySnapshot res;

		// Gets all books with the owner field equal to the uuid
		try {
			res = Tasks.await(
					booksRef.whereEqualTo("owner", username).get(),
					DB_TIMEOUT,
					TimeUnit.MILLISECONDS
			);
		} catch (Exception e) { // failed
			Log.w(TAG, e.getCause());
			return null;
		}

		for (DocumentSnapshot document : res.getDocuments())
			ownedBooks.add(document.toObject(Book.class));

		return ownedBooks;
	}

	/**
	 * Attempts to delete a book
	 * @param bookId The uuid of the book in firestore.
	 * @return True if the delete was successful, false otherwise
	 */
	public boolean deleteBook(String bookId) {
		try {
			Tasks.await(
					booksRef.document(bookId).delete(),
					DB_TIMEOUT,
					TimeUnit.MILLISECONDS
			);
			return true;
		} catch (Exception e) {
			Log.w(TAG, e.getCause());
			return false;
		}
	}

	/**
	 * Attempts to push a book to the database
	 * @param book The book object the push
	 * @return Null if the push fails, otherwise returns the book object
	 */
	public Book pushBook(Book book) {

		try {
			booksRef.document(book.getBookId()).set(book);
			return book;
		} catch (Exception e) {
			Log.w(TAG, e.getCause());
			return null;
		}
	}

	/**
	 * Searches book by querying keywords
	 * @param keywords
	 * @return
	 */
	public ArrayList<Book> findBooks(String keywords) {
		final ArrayList<Book> foundBooks = new ArrayList<>();

		QuerySnapshot res;

		try {
			res = Tasks.await(
					booksRef.startAt(keywords).endAt(keywords+"\uf8ff").get(),
					DB_TIMEOUT,
					TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			Log.w(TAG, e.getCause());
			return null;
		}

		for (DocumentSnapshot document : res.getDocuments())
			foundBooks.add(document.toObject(Book.class));

		return foundBooks;
	}

	/**
	 * Get Book using BookID
	 * @param bookID
	 * @return book
	 */
	public Book getBook(String bookID) {
		DocumentSnapshot res;

		try {
			res = Tasks.await(
					booksRef.document(bookID).get(),
					DB_TIMEOUT,
					TimeUnit.MILLISECONDS);
		} catch (Exception e) {
			Log.w(TAG, e.getCause());
			return null;
		}

		Book getBook = res.toObject(Book.class);

		return getBook;
	}


	/**
	 * gets all books from the DB
	 * @return
	 */
	public ArrayList<Book> getAllBooks(){
		ArrayList<Book> bookList = new ArrayList<>();

		QuerySnapshot res;

		try {
			res = Tasks.await(
					booksRef.get(),
					DB_TIMEOUT,
					TimeUnit.MILLISECONDS
			);

		} catch (Exception e){
			Log.w(TAG, e.getCause());
			return null;
		}

		for (DocumentSnapshot document : res.getDocuments())
			bookList.add(document.toObject(Book.class));

		return bookList;
	}

	public ArrayList<Book> getOwnerRequestedBooks(String owner) {
		final ArrayList<Book> requestedBooks = new ArrayList<>();

		QuerySnapshot res;

		// Gets all books with the owner field equal to the uuid
		try {
			res = Tasks.await(
					booksRef.whereEqualTo("owner", owner).whereEqualTo("status", status.REQUESTED).get(),
					DB_TIMEOUT,
					TimeUnit.MILLISECONDS
			);
		} catch (Exception e) { // failed
			Log.w(TAG, e.getCause());
			return null;
		}

		for (DocumentSnapshot document : res.getDocuments())
			requestedBooks.add(document.toObject(Book.class));

		return requestedBooks;
	}

	public ArrayList<Book> getOwnerBorrowedBooks(String owner) {
		final ArrayList<Book> borrowedBooks = new ArrayList<>();

		QuerySnapshot res;

		// Gets all books with the owner field equal to the uuid
		try {
			res = Tasks.await(
					booksRef.whereEqualTo("owner", owner).whereEqualTo("status", status.BORROWED).get(),
					DB_TIMEOUT,
					TimeUnit.MILLISECONDS
			);
		} catch (Exception e) { // failed
			Log.w(TAG, e.getCause());
			return null;
		}

		for (DocumentSnapshot document : res.getDocuments())
			borrowedBooks.add(document.toObject(Book.class));

		return borrowedBooks;
	}

	public ArrayList<Book> getOwnerAcceptedBooks(String owner) {
		final ArrayList<Book> acceptedBooks = new ArrayList<>();

		QuerySnapshot res;

		// Gets all books with the owner field equal to the uuid
		try {
			res = Tasks.await(
					booksRef.whereEqualTo("owner", owner).whereEqualTo("status", status.ACCEPTED).get(),
					DB_TIMEOUT,
					TimeUnit.MILLISECONDS
			);
		} catch (Exception e) { // failed
			Log.w(TAG, e.getCause());
			return null;
		}

		for (DocumentSnapshot document : res.getDocuments())
			acceptedBooks.add(document.toObject(Book.class));

		return acceptedBooks;
	}

	public ArrayList<Book> getOwnerAvailableBooks(String owner) {
		final ArrayList<Book> availableBooks = new ArrayList<>();

		QuerySnapshot res;

		// Gets all books with the owner field equal to the uuid
		try {
			res = Tasks.await(
					booksRef.whereEqualTo("owner", owner).whereEqualTo("status", status.AVAILABLE).get(),
					DB_TIMEOUT,
					TimeUnit.MILLISECONDS
			);
		} catch (Exception e) { // failed
			Log.w(TAG, e.getCause());
			return null;
		}

		for (DocumentSnapshot document : res.getDocuments())
			availableBooks.add(document.toObject(Book.class));

		return availableBooks;
	}

	public Book getBookById(String bid) {
		DocumentSnapshot res;

		try {
		  res = Tasks.await(booksRef.document(bid).get(), DB_TIMEOUT, TimeUnit.MILLISECONDS);
		} catch (Exception e) { // failed
		  Log.w(TAG, e.getCause());
		  return null;
		}

		if (res.exists()) {    // if user exists
		  return res.toObject(Book.class);
		} else {              // if user fails
		  return null;
		}
	}

	/**
	* gets books by a request list
	*
	* @param BookRequestList
	* @return
	*/

	public Map<String, Book> getBooksByBookRequestList(List<BookRequest> BookRequestList) {
	Map<String, Book> bookMap = new HashMap<>();
	for (BookRequest br : BookRequestList) {
		if (bookMap.containsKey(br.getBookId()))
			continue;
			Book b = getBookById(br.getBookId());
		if (b == null) {
			Log.w(TAG, "book: " + b.getBookId() + " doesn't exists but it was requested");
			continue;
		}
		bookMap.put(b.getBookId(), b);
	}

	return bookMap;
	}

	// TODO:
	// Add a method to query for all the books a user has
	// requested, borrowed, or been accepted to be borrowed
}
