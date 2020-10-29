package com.team41.boromi.dbs;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.User;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.team41.boromi.constants.CommonConstants.DB_TIMEOUT;

@Singleton
public class BookDB {

	private final static String TAG = "BookDB";
	private final String DB_COLLECTION = "books";
	private final FirebaseFirestore db;
	private final CollectionReference booksRef;

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

		// Gets all books with the owner field equal to thee uuid
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
		gson.toJson(book);

		try {
			Tasks.await(
					booksRef.document(book.getBookId().toString()).set(book),
					DB_TIMEOUT,
					TimeUnit.MILLISECONDS);
			return book;
		} catch (Exception e) {
			Log.w(TAG, e.getCause());
			return null;
		}
	}

	// TODO:
	// Add a method to query for all the books a user has
	// requested, borrowed, or been accepted to be borrowed
}
