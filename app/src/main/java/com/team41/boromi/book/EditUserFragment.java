package com.team41.boromi.book;

import static com.team41.boromi.utility.Utility.isNotNullOrEmpty;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.team41.boromi.BookActivity;
import com.team41.boromi.BoromiApp;
import com.team41.boromi.R;
import com.team41.boromi.callbacks.AuthNoResultCallback;
import com.team41.boromi.controllers.AuthenticationController;
import com.team41.boromi.dagger.BoromiModule;
import com.team41.boromi.models.User;
import javax.inject.Inject;

/**
 * This Dialog fragment is used when a user edits their information
 */
public class EditUserFragment extends DialogFragment {

  @Inject
  AuthenticationController authenticationController;
  private TextInputEditText editTextUsername;
  private TextInputEditText editTextEmail;
  private Button buttonSaveChanges;
  /**
   * Validate input fields have been filled
   */
  TextWatcher usernameEmailWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      // Empty method, required for text watcher
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      String email = editTextEmail.getText().toString().trim();
      String username = editTextUsername.getText().toString().trim();

      // Enables the button if both text fields are not null
      buttonSaveChanges.setEnabled(
          isNotNullOrEmpty(email) && isNotNullOrEmpty(username)
      );
    }

    @Override
    public void afterTextChanged(Editable s) {
      // Empty method, required for text watcher
    }
  };
  private User user;
  private BookActivity activity;
  private Boolean successfulWrite = false;
  /**
   * Update user information
   */
  private View.OnClickListener attemptToChangeUserInformation = new View.OnClickListener() {

    @Override
    public void onClick(View v) {
      String username = editTextUsername.getText().toString();
      String email = editTextEmail.getText().toString();

      // Attempts to write to the database
      // Neither fields were change so do nothing
      if (username.equals(user.getUsername()) && email.equals(user.getEmail())) {
        Toast.makeText(activity, "No Changes Were Made to Your Information", Toast.LENGTH_LONG)
            .show();
        return;
      }

      // A change was made, prepare an updated user
      User modifiedUser = new User(user.getUUID(), username, email);

      // If the email was changed, then the change needs to made in both auth and firestore
      if (!email.equals(user.getEmail())) {
        authenticationController.changeEmail(modifiedUser, new AuthNoResultCallback() {
          @Override
          public void onSuccess() {
            activity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(activity, "Successfully Updated Your Information", Toast.LENGTH_LONG)
                    .show();
              }
            });

            BoromiModule.user = modifiedUser;
            successfulWrite = true;
            dismiss();
          }

          @Override
          public void onFailure(Exception exception) {
            activity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(activity, "Failed to Update Your Information", Toast.LENGTH_LONG)
                    .show();
              }
            });
          }
        });
      } else {
        // If only the username was changed, then we only need to update firestore
        authenticationController.updateUser(modifiedUser);

        activity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            Toast.makeText(activity, "Successfully Updated Your Information", Toast.LENGTH_LONG)
                .show();
          }
        });

        successfulWrite = true;
        BoromiModule.user = modifiedUser;
        dismiss();
      }
    }
  };

  /**
   * Initialize values
   *
   * @param savedInstanceState
   * @return
   */
  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    View view = LayoutInflater
        .from(getActivity())
        .inflate(R.layout.fragment_edit_user, null);

    activity = (BookActivity) getActivity();

    ((BoromiApp) getActivity().getApplicationContext())
        .appComponent
        .getAuthenticationComponent()
        .inject(this);

    editTextUsername = view.findViewById(R.id.edit_user_edit_text_username);
    editTextEmail = view.findViewById(R.id.edit_user_edit_text_email);
    buttonSaveChanges = view.findViewById(R.id.edit_user_button_save);

    // Sets a text watcher for the text fields
    editTextUsername.addTextChangedListener(usernameEmailWatcher);
    editTextEmail.addTextChangedListener(usernameEmailWatcher);

    user = ((BookActivity) getActivity()).getUser();
    editTextUsername.setText(user.getUsername());
    editTextEmail.setText(user.getEmail());

    buttonSaveChanges.setOnClickListener(attemptToChangeUserInformation);

    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    return builder.setView(view).create();
  }

  /**
   * update ui
   */
  @Override
  public void onDestroy() {
    super.onDestroy();

    if (successfulWrite) {
      String username = editTextUsername.getText().toString();
      String email = editTextEmail.getText().toString();
      ((SettingsFragment) getParentFragment()).changeUserInformation(username, email);
    }
  }

  public interface ChangesUserInformation {

    void changeUserInformation(String username, String email);
  }
}