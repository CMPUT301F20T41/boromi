package com.team41.boromi.book;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.team41.boromi.R;
import com.team41.boromi.models.User;


public class DisplayOtherUserFragment extends DialogFragment {
    User displayingUser;

    private TextView emailDisplay;
    private TextView usernameDisplay;
    private TextView avatarDisplay;

    public DisplayOtherUserFragment(User displayingUser) {
        this.displayingUser = displayingUser;
    }


    public static DisplayOtherUserFragment newInstance(User displayUser) {
        DisplayOtherUserFragment displayOtherUserFragment = new DisplayOtherUserFragment(displayUser);
        Bundle args = new Bundle();
        displayOtherUserFragment.setArguments(args);
        return displayOtherUserFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.fragment_other_users, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailDisplay = (TextView) view.findViewById(R.id.other_user_email);
        usernameDisplay = (TextView) view.findViewById(R.id.other_user_username);
        avatarDisplay = (TextView) view.findViewById(R.id.other_user_avatar);
        avatarDisplay.setText(displayingUser.getUsername().substring(0,1).toUpperCase());
        emailDisplay.setText(displayingUser.getEmail());
        usernameDisplay.setText(displayingUser.getUsername());
    }
}

