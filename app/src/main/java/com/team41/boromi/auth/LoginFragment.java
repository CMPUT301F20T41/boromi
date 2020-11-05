package com.team41.boromi.auth;

import static com.team41.boromi.utility.Utility.isNotNullOrEmpty;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.auth.AuthResult;
import com.team41.boromi.BookActivity;
import com.team41.boromi.MainActivity;
import com.team41.boromi.R;
import com.team41.boromi.callbacks.AuthCallback;

/**
 * A simple {@link Fragment} subclass. Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

  private EditText emailInput;
  private EditText passwordInput;
  private Button loginButton;
  TextWatcher emailPasswordTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      // Get the text in both fields and check that both are not null or empty
      String email = emailInput.getText().toString().trim();
      String password = passwordInput.getText().toString().trim();

      // Enables the login button if both fields have text, Disables it otherwise
      loginButton.setEnabled(isNotNullOrEmpty(email) && isNotNullOrEmpty(password));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  };
  private MainActivity activity;
  private ProgressBar spinner;

  public LoginFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @return A new instance of fragment LoginFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static LoginFragment newInstance() {
    LoginFragment fragment = new LoginFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activity = (MainActivity) getActivity();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_login, container, false);

    spinner = view.findViewById(R.id.login_loading);
    spinner.setVisibility(View.GONE);
    Button recoverPasswordButton = view.findViewById(R.id.login_recoverPassword);
    loginButton = (Button) view.findViewById(R.id.login_login);
    emailInput = view.findViewById(R.id.login_email);
    passwordInput = view.findViewById(R.id.login_password);

    // Start the button out as being disabled because the text fields are empty
    loginButton.setEnabled(false);

    // Set on change listeners for the two text fields
    emailInput.addTextChangedListener(emailPasswordTextWatcher);
    passwordInput.addTextChangedListener(emailPasswordTextWatcher);

    recoverPasswordButton.setOnClickListener(view1 -> {
      Fragment recoverPasswordFragment = new RecoverPasswordFragment();
      FragmentManager manager = getFragmentManager();
      assert manager != null;

      FragmentTransaction ft = manager.beginTransaction();
      ft.replace(R.id.auth_fragment, recoverPasswordFragment).addToBackStack("recover").commit();
    });
    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();
        spinner.setVisibility(View.VISIBLE);
        activity.getAuthController().makeLoginRequest(email, password, new AuthCallback() {
          @Override
          public void onSuccess(AuthResult authResult) {
            Log.d("Login", "Login successful");
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
            Log.d("Login", "Login failed");
            activity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                spinner.setVisibility(View.GONE);
                Toast.makeText(activity, "Login Failed", Toast.LENGTH_LONG).show();
              }
            });
          }
        });
      }
    });
    return view;
  }
}