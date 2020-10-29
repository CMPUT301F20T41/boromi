package com.team41.boromi.dbs;

import static com.team41.boromi.constants.CommonConstants.DB_TIMEOUT;

import android.util.Log;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.team41.boromi.models.User;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserDB {

  private final static String TAG = "UserDB";
  private final String DB_COLLECTION = "users";
  private final FirebaseFirestore db;
  private final CollectionReference usersRef;

  private final Gson gson = new Gson();

  @Inject
  public UserDB(FirebaseFirestore db) {
    this.db = db;
    usersRef = db.collection(DB_COLLECTION);
  }

  /**
   * sync request for the user
   *
   * @param uuid
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
   * async pushes a user to a db merges data only in case of conflicts
   */
  public void pushUser(User user) {
    Gson gson = new Gson();
    gson.toJson(user);
    usersRef.document(user.getUUID()).set(user, SetOptions.merge());
  }

}
