package com.team41.boromi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.team41.boromi.auth.WelcomeFragment;
import com.team41.boromi.controllers.AuthenticationController;
import javax.inject.Inject;

/**
 * This is the main activity and starting point of our app. Here, the WelcomeFragment will be shown
 */
public class MainActivity extends AppCompatActivity {

  @Inject
  AuthenticationController authController;
  FragmentTransaction ft;
  private FragmentManager manager = null;

  /**
   * Initialize variables, inject app, show WelcomeFragment
   *
   * @param savedInstanceState
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ((BoromiApp) getApplicationContext()).appComponent.getAuthenticationComponent().inject(this);

    if (manager == null) {
      manager = getSupportFragmentManager();
    }
    if (manager.findFragmentById(R.id.auth_fragment) == null) {
      WelcomeFragment welcomeFragment = new WelcomeFragment();
      ft = manager.beginTransaction();
      ft.add(R.id.auth_fragment, welcomeFragment).commit();
    }

//    startActivity(new Intent(this, TestActivity.class));
  }

  /**
   * Custom back button in the auth pages
   *
   * @param view
   */
  public void customBack(View view) {
    super.onBackPressed();
  }

  /**
   * Gets the authController
   *
   * @return AuthenticationController
   */
  public AuthenticationController getAuthController() {
    return authController;
  }

}
