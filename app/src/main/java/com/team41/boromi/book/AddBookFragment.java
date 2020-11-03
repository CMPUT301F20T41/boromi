package com.team41.boromi.book;

import static com.team41.boromi.constants.CommonConstants.REQUEST_IMAGE_CAPTURE;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.team41.boromi.R;

public class AddBookFragment extends DialogFragment {

  private Button addBook;
  private EditText author;
  private EditText title;
  private EditText isbn;
  private ImageButton addImage;
  private Bitmap imageBitmap;

  public AddBookFragment() {
  }

  public static AddBookFragment newInstance() {
    AddBookFragment addBookFragment = new AddBookFragment();
    Bundle args = new Bundle();
    addBookFragment.setArguments(args);
    return addBookFragment;
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.add_book_fragment, container);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    addBook = (Button) view.findViewById(R.id.add_book_add_button);
    author = (EditText) view.findViewById(R.id.add_book_author);
    title = (EditText) view.findViewById(R.id.add_book_title);
    isbn = (EditText) view.findViewById(R.id.add_book_isbn);
    addImage = (ImageButton) view.findViewById(R.id.add_book_image);

    addBook.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        AddBookFragmentListener listener = (AddBookFragmentListener) getActivity();
        listener.onComplete(author.getText().toString(), title.getText().toString(),
            isbn.getText().toString(), imageBitmap);
        dismiss();
      }
    });
    addImage.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        dispatchTakePictureIntent();
      }
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == -1) {
      Bundle extras = data.getExtras();
      Bitmap imageBitmap = (Bitmap) extras.get("data");
      addImage.setImageBitmap(imageBitmap);
      this.imageBitmap = imageBitmap;
    }
  }

  private void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    try {
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    } catch (ActivityNotFoundException e) {
      // display error state to the user
    }
  }

  public interface AddBookFragmentListener {

    void onComplete(String author, String title, String isbn, Bitmap image);
  }
}
