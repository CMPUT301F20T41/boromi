package com.team41.boromi.auth;

import static com.team41.boromi.utility.Utility.isNotNullOrEmpty;

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
import com.team41.boromi.MainActivity;
import com.team41.boromi.R;
import com.team41.boromi.callbacks.AuthNoResultCallback;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass. Use the {@link RecoverPasswordFragment#newInstance} factory
 * method to create an instance of this fragment.
 */
public class RecoverPasswordFragment extends Fragment {

  private EditText emailInput;
  private Button recoverButton;
  TextWatcher emailTextWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      // Get the text in both fields and check that both are not null or empty
      String email = emailInput.getText().toString().trim();

      // Enables the login button if both fields have text, Disables it otherwise
      recoverButton.setEnabled(isNotNullOrEmpty(email));
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
  };
  private ProgressBar spinner;
  private MainActivity activity;

  public RecoverPasswordFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @return A new instance of fragment RecoverPassword.
   */
  // TODO: Rename and change types and number of parameters
  public static RecoverPasswordFragment newInstance() {
    RecoverPasswordFragment fragment = new RecoverPasswordFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activity = (MainActivity) Objects.requireNonNull(getActivity());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_recover_password, container, false);

    // Gets the ui components by id
    emailInput = view.findViewById(R.id.recover_email);
    recoverButton = view.findViewById(R.id.recover_recover);

    // Gets the loading spinner and hides it
    spinner = view.findViewById(R.id.recover_loader);
    spinner.setVisibility(View.GONE);

    // Disables the button to start, empty text field
    recoverButton.setEnabled(false);

    // Sets a text watcher for the email input
    emailInput.addTextChangedListener(emailTextWatcher);

    recoverButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        String email = emailInput.getText().toString();

        spinner.setVisibility(View.VISIBLE);
        activity
            .getAuthController()
            .resetPassword(email, new AuthNoResultCallback() {
              @Override
              public void onSuccess() {
                Log.d("Login", "Login successful");
                activity.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(activity, "Request Sent", Toast.LENGTH_LONG).show();
                  }
                });
              }

              @Override
              public void onFailure(Exception exception) {
                Log.d("Login", "Request Sent");
                activity.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(activity, "Failed to Send", Toast.LENGTH_LONG).show();
                  }
                });
              }
            });
      }
    });
    return view;
  }
}