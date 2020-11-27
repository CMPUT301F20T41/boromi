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
import com.team41.boromi.models.Book;

/**
 * Fragment to display other users' profiles
 */
public class DisplayBookFragment extends DialogFragment {
  Book b;

  private TextView titleTextView;
  private TextView authorTextView;
  private TextView isbnTextView;

  public DisplayBookFragment(Book b) {
    this.b = b;
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @return A new instance of fragment SettingsFragment.
   */
  public static DisplayBookFragment newInstance(Book b) {
    DisplayBookFragment displayBookFragment = new DisplayBookFragment(b);
    Bundle args = new Bundle();
    displayBookFragment.setArguments(args);
    return displayBookFragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
    getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    return inflater.inflate(R.layout.available, null);
  }

  /**
   * Setting up details for the card views
   * @param view
   * @param savedInstanceState
   */
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    titleTextView = (TextView) view.findViewById(R.id.available_title);
    authorTextView = (TextView) view.findViewById(R.id.available_author);
    isbnTextView = (TextView) view.findViewById(R.id.available_isbn);

    if (b.getTitle() == null)
      titleTextView.setText("NO AVAILABLE TITLE");
    else
      titleTextView.setText(b.getTitle());

    if(b.getAuthor() == null)
      authorTextView.setText("NO AVAILABLE AUTHOR");
    else
      authorTextView.setText(b.getAuthor());

    isbnTextView.setText(b.getISBN());
  }
}

