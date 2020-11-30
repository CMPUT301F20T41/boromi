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
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team41.boromi.BookActivity;
import com.team41.boromi.BoromiApp;
import com.team41.boromi.R;
import com.team41.boromi.callbacks.AuthNoResultCallback;
import com.team41.boromi.controllers.AuthenticationController;
import com.team41.boromi.dagger.BoromiModule;
import com.team41.boromi.dbs.UserDB;
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
  FirebaseFirestore db;
  private ProgressBar spinner;

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
  private User modifiedUser;
  private User user;
  private BookActivity activity;
  private Boolean successfulWrite = false;
  /**
   * Update user information
   */
  private View.OnClickListener attemptToChangeUserInformation = new View.OnClickListener() {

    @Override
    public void onClick(View v) {
      spinner.setVisibility(View.VISIBLE);
      String username = editTextUsername.getText().toString().toLowerCase();
      String email = editTextEmail.getText().toString();
      db = FirebaseFirestore.getInstance();
      UserDB userDB = new UserDB(db);

      // Attempts to write to the database
      // Neither fields were change so do nothing
      if (username.equals(user.getUsername()) && email.equals(user.getEmail())) {
        spinner.setVisibility(View.GONE);
        Toast.makeText(activity, "No Changes Were Made to Your Information", Toast.LENGTH_LONG)
                .show();
        return;
      }

      modifiedUser = new User(user.getUUID(), username, email);
      // A change was made, prepare an updated user
      // If the email was changed, then the change needs to made in both auth and firestore
      new Thread(new Runnable() {
        @Override
        public void run() {
          if(!username.equals(user.getUsername()) && email.equals((user.getEmail()))) { // if only username is changed and email unchanged
            if (userDB.isUsernameUnique(username)) {
              authenticationController.updateUser(modifiedUser);
              activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  spinner.setVisibility(View.GONE);
                  Toast.makeText(activity, "Successfully Updated Your Information", Toast.LENGTH_LONG)
                          .show();
                }
              });
              successfulWrite = true;
              BoromiModule.user = modifiedUser;
              dismiss();
            } else {
              activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  spinner.setVisibility(View.GONE);
                  Toast.makeText(activity, "User name taken", Toast.LENGTH_LONG)
                          .show();
                }
              });
            }
          } else if (!email.equals(user.getEmail()) && username.equals(user.getUsername())) { // if only username is changed and email unchanged
            authenticationController.changeEmail(modifiedUser, new AuthNoResultCallback() {
              @Override
              public void onSuccess() {
                activity.runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    spinner.setVisibility(View.GONE);
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
                    spinner.setVisibility(View.GONE);
                    Toast.makeText(activity, "Failed to Update Your Information", Toast.LENGTH_LONG)
                            .show();
                  }
                });
              }
            });
          } else {  // if both are changed
            if ((userDB.isUsernameUnique(username))) {
              authenticationController.changeEmail(modifiedUser, new AuthNoResultCallback() {
                @Override
                public void onSuccess() {
                  activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                      spinner.setVisibility(View.GONE);
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
                      spinner.setVisibility(View.GONE);
                      Toast.makeText(activity, "Failed to Update Your Information", Toast.LENGTH_LONG)
                              .show();
                    }
                  });
                }
              });
            } else {
              activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  spinner.setVisibility(View.GONE);
                  Toast.makeText(activity, "User name taken", Toast.LENGTH_LONG)
                          .show();
                }
              });
            }
          }
        }
      }).start();
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
    spinner = view.findViewById(R.id.edit_user_loader);
    spinner.setVisibility(View.GONE);
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
      user = ((BookActivity) getActivity()).getUser();
      String username = editTextUsername.getText().toString().toLowerCase();
      String email = editTextEmail.getText().toString();
      user.setEmail(email);
      user.setUsername(username);
      ((SettingsFragment) getParentFragment()).changeUserInformation(username, email);
    }
  }

  public interface ChangesUserInformation {

    void changeUserInformation(String username, String email);
  }
}