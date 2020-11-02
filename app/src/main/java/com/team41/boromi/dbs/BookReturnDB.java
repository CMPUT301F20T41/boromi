package com.team41.boromi.dbs;

import static com.team41.boromi.constants.CommonConstants.DB_TIMEOUT;

import android.util.Log;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team41.boromi.models.BookReturn;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.inject.Inject;

public class BookReturnDB {

  private final static String TAG = "ReturnDB";
  private final String DB_COLLECTION = "bookReturns";
  private final FirebaseFirestore db;
  private final CollectionReference returnsRef;

  @Inject
  public BookReturnDB(FirebaseFirestore db) {
    this.db = db;
    returnsRef = db.collection(DB_COLLECTION);
  }

  /**
   * adds entry to bookrequest db
   *
   * @param bookReturn
   * @return
   */
  public boolean addReturnRequest(BookReturn bookReturn) {
    try {
      Tasks
          .await(returnsRef.document(bookReturn.getBookId().toString()).set(bookReturn), DB_TIMEOUT,
              TimeUnit.MILLISECONDS);
      return true;
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      Log.w(TAG, e.getCause());
      return false;
    }
  }

  /**
   * removes entry to bookrequest db
   *
   * @param bookID
   * @return
   */
  public boolean deleteReturnRequest(String bookID) {
    try {
      Tasks.await(returnsRef.document(bookID).delete(), DB_TIMEOUT,
          TimeUnit.MILLISECONDS);
      return true;
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      Log.w(TAG, e.getCause());
      return false;
    }
  }

  /**
   * returns BookReturn from db if found. else null
   *
   * @param bookID
   * @return
   */
  public BookReturn getReturnRequest(String bookID) {
    DocumentSnapshot res;
    try {
      res = Tasks.await(returnsRef.document(bookID).get(), DB_TIMEOUT,
          TimeUnit.MILLISECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      Log.w(TAG, e.getCause());
      return null;
    }
    if (res.exists()) {
      return res.toObject(BookReturn.class);
    } else {
      return null;
    }
  }
}
