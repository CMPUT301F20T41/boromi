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

public class MainActivity extends AppCompatActivity {

  @Inject
  AuthenticationController authController;
  private FragmentManager manager = null;
  FragmentTransaction ft;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ((BoromiApp) getApplicationContext()).appComponent.getAuthenticationComponent().inject(this);

    startActivity(new Intent(this, TestActivity.class));

    if (manager == null) {
      manager = getSupportFragmentManager();
    }
    if (manager.findFragmentById(R.id.auth_fragment) == null) {
      WelcomeFragment welcomeFragment = new WelcomeFragment();
      ft = manager.beginTransaction();
      ft.add(R.id.auth_fragment, welcomeFragment).commit();
    }
  }

  public void customBack(View view) {
    super.onBackPressed();
  }

  public AuthenticationController getAuthController() {
    return authController;
  }

}
