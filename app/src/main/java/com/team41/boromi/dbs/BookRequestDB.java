package com.team41.boromi.dbs;

import static com.team41.boromi.constants.CommonConstants.DB_TIMEOUT;

import android.util.Log;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.team41.boromi.models.BookRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Firebase calls for bookRequest collection
 */
@Singleton
public class BookRequestDB {

  private final static String TAG = "BookRequestDB";
  private final String DB_COLLECTION = "bookrequests";
  private final FirebaseFirestore db;
  private final CollectionReference bookRequestsRef;

  private final Gson gson = new Gson();

  @Inject
  public BookRequestDB(FirebaseFirestore db) {
    this.db = db;
    bookRequestsRef = db.collection(DB_COLLECTION);
  }

  /**
   * Grabs a list of bookRequests from an uid
   *
   * @param uid id of the user
   * @return list of bookRequests
   */
  public List<BookRequest> getBookRequests(String uid) {
    ArrayList<BookRequest> bookRequests = new ArrayList<>();

    QuerySnapshot res;

    // Gets all books with the owner field equal to thee uuid
    try {
      res = Tasks.await(
          bookRequestsRef.whereEqualTo("requestor", uid).get(),
          DB_TIMEOUT,
          TimeUnit.MILLISECONDS
      );
    } catch (Exception e) { // failed
      Log.w(TAG, e.getCause());
      return null;
    }

    for (DocumentSnapshot document : res.getDocuments()) {
      bookRequests.add(document.toObject(BookRequest.class));
    }

    return bookRequests;
  }

  /**
   * async push a book request to the db, if it already exists then update it
   *
   * @param bookRequest bookRequest to be pushed
   */
  public void pushBookRequest(BookRequest bookRequest) {
    bookRequestsRef.document(bookRequest.getRequestId()).set(bookRequest, SetOptions.merge());
  }

  /**
   * Gets owned books that are requested
   *
   * @param owner
   * @return
   */
  public List<BookRequest> getBookRequestsForOwner(String owner) {
    List<BookRequest> bookRequests = new ArrayList<>();

    QuerySnapshot res;

    try {
      res = Tasks.await(
          bookRequestsRef.whereEqualTo("owner", owner).get(),
          DB_TIMEOUT,
          TimeUnit.MILLISECONDS
      );
    } catch (Exception e) { // failed
      Log.w(TAG, e.getCause());
      return null;
    }

    for (DocumentSnapshot document : res.getDocuments()) {
      bookRequests.add(document.toObject(BookRequest.class));
    }

    return bookRequests;
  }


  /**
   * Removes requests from library that have bookId sync grabs bookid, but async delete them
   *
   * @param bid id of the book
   */
  public void deleteRequestsForBook(String bid) {
    QuerySnapshot res;

    try {
      res = Tasks.await(
          bookRequestsRef.whereEqualTo("bookId", bid).get(),
          DB_TIMEOUT,
          TimeUnit.MILLISECONDS
      );

      for (DocumentSnapshot document : res.getDocuments()) {
        bookRequestsRef.document(document.getId()).delete();
      }

    } catch (Exception e) { // failed
      Log.w(TAG, e.getCause());
    }
  }

  // async delete bookrequest

  /**
   * Deletes a book request
   *
   * @param rid
   */
  public void deleteBookRequest(String rid) {
    bookRequestsRef.document(rid).delete();
  }

}
