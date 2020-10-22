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

  public void onClickLogin(View view) {
    Fragment loginFragment = new LoginFragment();
    ft = manager.beginTransaction();
    ft.replace(R.id.auth_fragment, loginFragment).addToBackStack("login").commit();
  }

  public void onClickSignup(View view) {
    Fragment signupFragment = new SignupFragment();
    ft = manager.beginTransaction();
    ft.replace(R.id.auth_fragment, signupFragment).addToBackStack("signup").commit();
  }

  public void onForgotPassword(View view) {
    Fragment recoverPasswordFragment = new RecoverPassword();
    ft = manager.beginTransaction();
    ft.replace(R.id.auth_fragment, recoverPasswordFragment).addToBackStack("recover").commit();
  }
}
