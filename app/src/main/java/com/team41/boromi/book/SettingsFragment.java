package com.team41.boromi.book;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.team41.boromi.BookActivity;
import com.team41.boromi.R;
import com.team41.boromi.models.User;

/**
 * SettingsFragment manages the user information tab
 */
public class SettingsFragment extends Fragment implements EditUserFragment.ChangesUserInformation {

  private final static String TAG = "SETTINGS_FRAGMENT";

  BookActivity activity;

  User user;

  private TextView imageViewAvatar;
  private TextView textViewUsername;
  private TextView textViewEmail;
//  private ImageView imageViewEditUserIcon;

  // TODO: Add logout functionality
  // private LinearLayout buttonLogout;

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

  /**
   * Initialize any values
   *
   * @param savedInstanceState
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.activity = (BookActivity) getActivity();
  }

  /**
   * Binds any listeners or values
   *
   * @param inflater
   * @param container
   * @param savedInstanceState
   * @return
   */
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
//    imageViewEditUserIcon = view.findViewById(R.id.settings_button_edit_user);

    // TODO: Implement logging out
    // buttonLogout = view.findViewById(R.id.settings_button_logout);

    // Sets the text in the email and password field
    textViewUsername.setText(user.getUsername());
    textViewEmail.setText(user.getEmail());
    imageViewAvatar.setText(Character.toString(user.getUsername().charAt(0)).toUpperCase());

    // TODO: Implement logging out
    //    buttonLogout.setOnClickListener(new View.OnClickListener() {
    //      @Override
    //      public void onClick(View v) {
    //
    //        //        authenticationController.signOut();
    //        //        BoromiModule.user = null;
    //        //
    //        //        activity.finish();
    //      }
    //    });

    SettingsFragment settingsFragment = this;

//    imageViewEditUserIcon.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View v) {
//        assert getFragmentManager() != null;
//        new EditUserFragment().show(getChildFragmentManager(), "EDIT_USER");
//      }
//    });

    return view;
  }

  /**
   * Updates the UI with the new information
   *
   * @param username username of the user
   * @param email    email of the user
   */
  @Override
  public void changeUserInformation(String username, String email) {
    textViewUsername.setText(username);
    textViewEmail.setText(email);
  }
}