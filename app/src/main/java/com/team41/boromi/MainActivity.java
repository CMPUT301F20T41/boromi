package com.team41.boromi;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.team41.boromi.auth.LoginFragment;
import com.team41.boromi.auth.RecoverPassword;
import com.team41.boromi.auth.SignupFragment;
import com.team41.boromi.auth.WelcomeFragment;

public class MainActivity extends AppCompatActivity {


  private FragmentManager manager = null;
  FragmentTransaction ft;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (manager == null) {
      manager = getSupportFragmentManager();
    }
    if (manager.findFragmentById(R.id.auth_fragment) == null) {
      WelcomeFragment welcomeFragment = new WelcomeFragment();
      ft = manager.beginTransaction();
      ft.add(R.id.auth_fragment, welcomeFragment).commit();
    }
  }
}
