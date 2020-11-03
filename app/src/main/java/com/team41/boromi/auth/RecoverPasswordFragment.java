package com.team41.boromi.auth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.team41.boromi.MainActivity;
import com.team41.boromi.R;

/**
 * A simple {@link Fragment} subclass. Use the {@link RecoverPasswordFragment#newInstance} factory
 * method to create an instance of this fragment.
 */
public class RecoverPasswordFragment extends Fragment {

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
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_recover_password, container, false);
    EditText emailInput = view.findViewById(R.id.recover_email);
    Button recoverButton = view.findViewById(R.id.recover_recover);
    recoverButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        String email = emailInput.getText().toString();
        ((MainActivity) getActivity()).getAuthController().resetPassword(email);
        Toast.makeText(getContext(), "Request Sent", Toast.LENGTH_LONG).show();
      }
    });
    return view;
  }
}