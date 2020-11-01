package com.team41.boromi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.AuthResult;
import com.team41.boromi.callbacks.AuthCallback;
import com.team41.boromi.controllers.AuthenticationController;
import com.team41.boromi.controllers.BookRequestController;

import javax.inject.Inject;

public class TestActivity extends AppCompatActivity {

  private final static String TAG = "TestActivity";

  @Inject
  AuthenticationController authController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((BoromiApp) getApplicationContext()).appComponent.getAuthenticationComponent().inject(this);

    authController.makeLoginRequest("mingyaang@gmail.com", "cmput301", new AuthCallback() {
      @Override
      public void onSuccess(AuthResult authResult) {
        Log.d(TAG, "LOGIN GOOD");
        startRequestActivity();
      }

      @Override
      public void onFailure(Exception e) {
        Log.d(TAG, "UNABLE TO LOGIN");
        Log.w(TAG, e.getCause());
      }
    });
  }

  // START OF BOOK REQUESTING SAMPLE LOGIC
  private void startRequestActivity() {
    startActivity(new Intent(this, TestRequestBookActivity.class));
  }


}