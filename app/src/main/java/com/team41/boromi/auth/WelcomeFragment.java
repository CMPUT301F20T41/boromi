package com.team41.boromi.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.team41.boromi.R;

/**
 * A simple {@link Fragment} subclass. Use the {@link WelcomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WelcomeFragment extends Fragment {

  public WelcomeFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @return A new instance of fragment WelcomeFragment.
   */
  public static WelcomeFragment newInstance() {
    WelcomeFragment fragment = new WelcomeFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_welcome, container, false);
    Button signup = (Button) view.findViewById(R.id.welcome_signup);
    Button login = view.findViewById(R.id.welcome_login);
    login.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Fragment loginFragment = new LoginFragment();
        FragmentManager manager = getFragmentManager();
        assert manager != null;
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.auth_fragment, loginFragment).addToBackStack("login").commit();
      }
    });
    signup.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Fragment signupFragment = new SignupFragment();
        FragmentManager manager = getFragmentManager();
        assert manager != null;
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.auth_fragment, signupFragment).addToBackStack("signup").commit();
      }
    });
    return view;
  }

}