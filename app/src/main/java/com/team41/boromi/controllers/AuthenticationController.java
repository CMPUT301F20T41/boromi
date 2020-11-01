package com.team41.boromi.controllers;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
   * @param email
   * @param password
   * @param authCallback
   */
  public void makeLoginRequest(String email, String password, final AuthCallback authCallback) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(executor, new OnCompleteListener<AuthResult>() {
          public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
              // Sign in success, update UI with the signed-in user's information
              Log.d(TAG, "signInWithEmail:success");
              BoromiModule.user = userDB.getUserByUUID(auth.getCurrentUser().getUid());
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
   * @param email
   * @param password
   * @return a user object if successful
   */
  public void makeSignUpRequest(final String username, String email, String password,
                                final AuthCallback authCallback) {
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
   * @param username
   */
  private void createUser(String username) {
    FirebaseUser fUser = auth.getCurrentUser();
    User user = new User(fUser.getUid(), fUser.getEmail(), username);
    userDB.pushUser(user);
    BoromiModule.user = user;   // set user in signup process
  }


}
