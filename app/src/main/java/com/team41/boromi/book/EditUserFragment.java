package com.team41.boromi.book;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.team41.boromi.BookActivity;
import com.team41.boromi.R;
import com.team41.boromi.models.User;

public class EditUserFragment extends DialogFragment {
	private TextInputEditText editTextUsername;
	private TextInputEditText editTextEmail;
	private Button buttonSaveChanges;

	private ChangesUserInformation listener;

	public interface ChangesUserInformation {
		void changeUserInformation(String username, String email);
	}

	@NonNull
	@Override
	public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
		View view = LayoutInflater
				.from(getActivity())
				.inflate(R.layout.fragment_edit_user, null);

		editTextUsername = view.findViewById(R.id.edit_user_edit_text_username);
		editTextEmail = view.findViewById(R.id.edit_user_edit_text_email);
		buttonSaveChanges = view.findViewById(R.id.edit_user_button_save);

		User user = ((BookActivity) getActivity()).getUser();
		editTextUsername.setText(user.getUsername());
		editTextEmail.setText(user.getEmail());

		buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = editTextUsername.getText().toString();
				String email = editTextEmail.getText().toString();
				((SettingsFragment) getParentFragment()).changeUserInformation(username, email);
				dismiss();
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		return builder.setView(view).create();
	}
}