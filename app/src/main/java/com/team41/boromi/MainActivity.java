package com.team41.boromi;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
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

    this.createNotificationChannel();

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

  private void createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      CharSequence name = getString(R.string.channel_name);
      String description = getString(R.string.channel_description);
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel("postNotificationChannel", name, importance);
      channel.setDescription(description);
      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
  }
}
