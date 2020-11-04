package com.team41.boromi.book;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.team41.boromi.BookActivity;
import com.team41.boromi.BoromiApp;
import com.team41.boromi.R;
import com.team41.boromi.controllers.AuthenticationController;
import com.team41.boromi.dagger.BoromiModule;
import com.team41.boromi.models.User;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass. Use the {@link SettingsFragment#newInstance} factory method
 * to create an instance of this fragment.
 */
public class SettingsFragment extends Fragment implements EditUserFragment.ChangesUserInformation {

  private final static String TAG = "SETTINGS_FRAGMENT";

  @Inject
  AuthenticationController authenticationController;

  BookActivity activity;

  User user;

  private TextView imageViewAvatar;
  private TextView textViewUsername;
  private TextView textViewEmail;
  private ImageView imageViewEditUserIcon;
  private LinearLayout buttonLogout;

  public SettingsFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @return A new instance of fragment SettingsFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static SettingsFragment newInstance() {
    SettingsFragment fragment = new SettingsFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((BoromiApp) getActivity().getApplicationContext())
            .appComponent
            .getAuthenticationComponent()
            .inject(this);

    this.activity = (BookActivity) getActivity();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_settings, container, false);

    // Gets the authenticated user
    user = activity.getUser();

    // Gets the ui components
    imageViewAvatar = view.findViewById(R.id.settings_user_avatar);
    textViewUsername = view.findViewById(R.id.settings_text_view_username);
    textViewEmail = view.findViewById(R.id.settings_text_view_email);
    imageViewEditUserIcon = view.findViewById(R.id.settings_button_edit_user);
    buttonLogout = view.findViewById(R.id.settings_button_logout);

    // Sets the text in the email and password field
    textViewUsername.setText(user.getUsername());
    textViewEmail.setText(user.getEmail());
    imageViewAvatar.setText(Character.toString(user.getUsername().charAt(0)).toUpperCase());

    buttonLogout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // TODO: Implement in a later ticket
        //        authenticationController.signOut();
        //        BoromiModule.user = null;
        //
        //        activity.finish();
      }
    });

    SettingsFragment settingsFragment = this;

    imageViewEditUserIcon.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        assert getFragmentManager() != null;
        new EditUserFragment().show(getChildFragmentManager(), "EDIT_USER");
      }
    });

    return view;
  }

  @Override
  public void changeUserInformation(String username, String email) {
      // Neither fields were change so do nothing
      if (username.equals(this.user.getUsername()) && email.equals(this.user.getEmail())) {
        return;
      }

      // A change was made, prepare an updated user
      User modifiedUser = new User(user.getUUID(), username, email);

      // If the email was changed, then the change needs to made in both auth and firestore
      if (!email.equals(this.user.getEmail())) {
        authenticationController.changeEmail(modifiedUser);
        BoromiModule.user = modifiedUser;
        textViewEmail.setText(email);
        textViewUsername.setText(username);
        return;
      }

      // If only the username was changed, then we only need to update firestore
      authenticationController.updateUser(modifiedUser);
      BoromiModule.user = modifiedUser;
      textViewUsername.setText(username);
  }
}