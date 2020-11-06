package com.team41.boromi.auth;

import static com.team41.boromi.utility.Utility.isNotNullOrEmpty;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.AuthResult;
import com.team41.boromi.BookActivity;
import com.team41.boromi.MainActivity;
import com.team41.boromi.R;
import com.team41.boromi.callbacks.AuthCallback;

/**
 * SignupFragment manages the signup page and creates an account for the user and redirects the
 * user to the book activity when their account is created.
 */
public class SignupFragment extends Fragment {

  Button createAccountButton;
  private EditText emailInput;
  private EditText userNameInput;
  private EditText passwordInput;
  /**
   * Used to enable the create account button when input fields are filled
   */
  TextWatcher emailUsernamePasswordTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      // Get the text in both fields and check that both are not null or empty
      String email = emailInput.getText().toString().trim();
      String password = passwordInput.getText().toString().trim();
      String username = userNameInput.getText().toString().trim();

      // Enables the login button if both fields have text, Disables it otherwise
      createAccountButton.setEnabled(
          isNotNullOrEmpty(email) &&
              isNotNullOrEmpty(password) &&
              isNotNullOrEmpty(username) &&
              password.length() >= 6
      );
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  };
  private MainActivity activity;
  private ProgressBar spinner;

  public SignupFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment
   * @return A new instance of fragment SignupFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static SignupFragment newInstance() {
    SignupFragment fragment = new SignupFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  /**
   * onCreate method to initialize any variables
   * @param savedInstanceState
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activity = (MainActivity) getActivity();
  }

  /**
   * onCreateView to bind any listeners or values
   * @param inflater
   * @param container
   * @param savedInstanceState
   * @return
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {

    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_signup, container, false);

    spinner = view.findViewById(R.id.signup_loading);
    spinner.setVisibility(View.GONE);
    createAccountButton = view.findViewById(R.id.signup_signup);

    emailInput = view.findViewById(R.id.signup_email);
    userNameInput = view.findViewById(R.id.signup_username);
    passwordInput = view.findViewById(R.id.signup_password);

    // Disables the button since all the text fields are empty
    createAccountButton.setEnabled(false);

    // Sets text watchers for each of the inputs
    emailInput.addTextChangedListener(emailUsernamePasswordTextWatcher);
    userNameInput.addTextChangedListener(emailUsernamePasswordTextWatcher);
    passwordInput.addTextChangedListener(emailUsernamePasswordTextWatcher);

    createAccountButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        String email = emailInput.getText().toString();
        String username = userNameInput.getText().toString();
        String password = passwordInput.getText().toString();
        spinner.setVisibility(View.VISIBLE);
        activity.getAuthController().makeSignUpRequest(username, email, password,
            new AuthCallback() {
              @Override
              public void onSuccess(AuthResult authResult) {
                Log.d("Create Account", "Creation successful");
                activity.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    spinner.setVisibility(View.GONE);
                  }
                });
                Intent intent = new Intent(getActivity(), BookActivity.class);
                startActivity(intent);
              }

              @Override
              public void onFailure(Exception exception) {
                Log.d("Create Account", "Creation failed");
                activity.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(activity, "Account Creation Failed", Toast.LENGTH_LONG).show();
                  }
                });
              }
            });
      }
    });
    return view;
  }
}
