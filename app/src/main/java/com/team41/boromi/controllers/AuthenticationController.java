package com.team41.boromi.controllers;

import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.team41.boromi.callbacks.AuthCallback;
import com.team41.boromi.dagger.ActivityScope;
import com.team41.boromi.dagger.BoromiModule;
import com.team41.boromi.dbs.UserDB;
import com.team41.boromi.models.User;
import java.util.concurrent.Executor;
import javax.inject.Inject;

/**
 * A class that handles authentication and sign in methods
 */
@ActivityScope
public class AuthenticationController {

  private final static String TAG = "AuthController";
  final Executor executor;
  UserDB userDB;
  FirebaseAuth auth;

  @Inject
  public AuthenticationController(UserDB userDB, FirebaseAuth auth, Executor executor) {
    this.userDB = userDB;
    this.auth = auth;
    this.executor = executor;
  }

  /**
   * Makes a request for user login. Mandatory callback to return changes to main thread.
   *
   * @param email        The email passed from the login fragment
   * @param password     The password passed from the login fragment
   * @param authCallback A callback function to execute on success or failure
   */
  public void makeLoginRequest(String email, String password, final AuthCallback authCallback) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(executor, new OnCompleteListener<AuthResult>() {
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              // Sign in success, update UI with the signed-in user's information
              Log.d(TAG, "signInWithEmail:success");
              FirebaseUser fUser = auth.getCurrentUser();
              assert fUser != null;
              BoromiModule.user = userDB.getUserByUUID(fUser.getUid());
              authCallback.onSuccess(task.getResult());
            } else {
              // If sign in fails, display a message to the user.
              Log.w(TAG, "signInWithEmail:failure", task.getException());
              authCallback.onFailure(task.getException());
            }
          }
        });
  }

  /**
   * Makes a request to sign a user up. Fails on certain conditions such as the user already has an
   * account and request sign up. More details can be found https://firebase.google.com/docs/reference/js/firebase.auth.Auth#createuserwithemailandpassword
   * This needs to be run in executor because getting the user is synchronous.
   *
   * @param username The username entered  by the user
   * @param email    The email entered by the user
   * @param password The password entered by the user
   */
  public void makeSignUpRequest(
          final String username,
          String email,
          String password,
          final AuthCallback authCallback
  ) {
    // Ensures that the username is unique before signing up
    if (userDB.getUserByUsername(username) != null) {

      // Takes two string params but I have no clue what they need to be
      authCallback.onFailure(new FirebaseAuthUserCollisionException(
          "The username '" + username + "' is taken",
          "The username '" + username + "' is taken"
      ));

      return;
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener(executor, new OnCompleteListener<AuthResult>() {
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              // Sign in success, update UI with the signed-in user's information
              Log.d(TAG, "createUserWithEmail:success");
              createUser(username);
              authCallback.onSuccess(task.getResult());
            } else {
              // If sign in fails, display a message to the user.
              Log.w(TAG, "createUserWithEmail:failure", task.getException());
              authCallback.onFailure(task.getException());
            }
          }
        });
  }

  /**
   * Pushes information to login
   *
   * @param username The username to create an account for
   */
  private void createUser(String username) {
    FirebaseUser fUser = auth.getCurrentUser();
    assert fUser != null;

    User user = new User(fUser.getUid(), username, fUser.getEmail());
    userDB.pushUser(user);
    BoromiModule.user = user;   // set user in signup process
  }

  public void resetPassword(String email) {
    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener(executor, new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()) {
              Log.d(TAG, "Email sent.");
            }
          }
        });
  }

  /**
   * Changes the users email address in firebase auth
   * If successful, persists the change to firestore
   * If unsuccessful, neither operation will happen
   * @param user The modified user information
   */
  public void changeEmail(User user) {
    FirebaseUser fUser = auth.getCurrentUser();
    assert fUser != null;
    fUser.updateEmail(user.getEmail())
            .addOnCompleteListener(executor, task -> {
              if (task.isSuccessful()) {
                userDB.pushUser(user);
              } else {
                Log.e(TAG, "Failed to update users email");
              }
            });
  }

  public void updateUser(User user) {
    userDB.pushUser(user);
  }

  public void signOut() {
    auth.signOut();
  }

}
