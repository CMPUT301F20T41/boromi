package com.team41.boromi.dbs;

import static com.team41.boromi.constants.CommonConstants.DB_TIMEOUT;

import android.util.Log;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.team41.boromi.models.User;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Firebase calls for user collection
 */
@Singleton
public class UserDB {

  private final static String TAG = "UserDB";
  private final String DB_COLLECTION = "users";
  private final CollectionReference usersRef;

  @Inject
  public UserDB(FirebaseFirestore db) {
    usersRef = db.collection(DB_COLLECTION);
  }

  /**
   * sync request for the user
   *
   * @param uuid id of the user
   * @return throws runtimeException if failed, otherwise gives user
   */
  public User getUserByUUID(String uuid) {
    DocumentSnapshot res;
    try {
      res = Tasks.await(usersRef.document(uuid).get(), DB_TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) { // failed
      Log.w(TAG, e.getCause());
      return null;
    }

    if (res.exists()) {    // if user exists
      return res.toObject(User.class);
    } else {              // if user fails
      return null;
    }
  }

  /**
   * Attempts to find a user by their username
   *
   * @param username The username to look for
   * @return null if no user was found, otherwise returns the found user
   */
  public User getUserByUsername(String username) {
    QuerySnapshot res;

    // Queries firestore for the username
    try {
      res = Tasks.await(
          usersRef.whereEqualTo("username", username).get(),
          DB_TIMEOUT,
          TimeUnit.MILLISECONDS
      );
    } catch (Exception e) { // Request failed
      Log.w(TAG, e.getCause());
      return null;
    }

    if (res.isEmpty()) { // No user found
      return null;
    } else if (res.size() > 1) { // This shouldn't happen but is a good check
      Log.e(TAG, "Data Inconsistency: 2 or more users have the same username");
      return res.getDocuments().get(0).toObject(User.class);
    } else { // Exactly 1 user found
      return res.getDocuments().get(0).toObject(User.class);
    }

  }


  /**
   * async pushes a user to a db merges data only in case of conflicts
   *
   * @param user User to add
   * @return User if successful, null otherwise
   */
  public User pushUser(User user) {
    try {
      Tasks.await(
          usersRef.document(user.getUUID()).set(user, SetOptions.merge()),
          DB_TIMEOUT,
          TimeUnit.MILLISECONDS);
      return user;
    } catch (Exception e) {
      Log.w(TAG, e.getCause());
      return null;
    }
  }
}
