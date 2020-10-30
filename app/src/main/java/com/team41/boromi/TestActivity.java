package com.team41.boromi;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.AuthResult;
import com.team41.boromi.callbacks.AuthCallback;
import com.team41.boromi.controllers.AuthenticationController;
import javax.inject.Inject;

public class TestActivity extends AppCompatActivity {

  private final static String TAG = "TestActivity";

  @Inject
  AuthenticationController authController;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((BoromiApp) getApplicationContext()).appComponent.getAuthenticationComponent().inject(this);

    authController
        .makeSignUpRequest("Brock", "bchelle@ualberta.ca", "CMPUT301", new AuthCallback() {
          @Override
          public void onSuccess(AuthResult authResult) {
            Log.d(TAG, "LOGIN GOOD");
          }

          @Override
          public void onFailure(Exception e) {
            Log.d(TAG, "UNABLE TO LOGIN");
            Log.w(TAG, e.getCause());
          }
        });

  }
}