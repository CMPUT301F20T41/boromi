package com.team41.boromi.book;

import static com.team41.boromi.constants.CommonConstants.REQUEST_IMAGE_CAPTURE;
import static com.team41.boromi.utility.Utility.isNotNullOrEmpty;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
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

  private Button buttonAddBook;
  private EditText editTextAuthor;
  private EditText editTextTitle;
  private EditText editTextIsbn;
  TextWatcher allFieldsWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      // Empty method, required for text watcher
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      String author = editTextAuthor.getText().toString().trim();
      String title = editTextTitle.getText().toString().trim();
      String isbn = editTextIsbn.getText().toString().trim();

      // Sets the button if all the text fields have content and the ISBN is 13 characters
      buttonAddBook.setEnabled(
          isNotNullOrEmpty(author) &&
              isNotNullOrEmpty(title) &&
              isNotNullOrEmpty(isbn)
              && isbn.length() == 13
      );
    }

    @Override
    public void afterTextChanged(Editable s) {
      // Empty method, required for text watcher
    }
  };
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
  public void onResume() {
    super.onResume();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    buttonAddBook = (Button) view.findViewById(R.id.add_book_add_button);
    editTextAuthor = (EditText) view.findViewById(R.id.add_book_author);
    editTextTitle = (EditText) view.findViewById(R.id.add_book_title);
    editTextIsbn = (EditText) view.findViewById(R.id.add_book_isbn);
    addImage = (ImageButton) view.findViewById(R.id.add_book_image);

    // Disables the button to start since the fields are all empty
    buttonAddBook.setEnabled(false);

    // Adds listeners for all of the text fields
    editTextAuthor.addTextChangedListener(allFieldsWatcher);
    editTextTitle.addTextChangedListener(allFieldsWatcher);
    editTextIsbn.addTextChangedListener(allFieldsWatcher);

    buttonAddBook.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        AddBookFragmentListener listener = (AddBookFragmentListener) getActivity();

        assert listener != null;
        listener.onComplete(
            editTextAuthor.getText().toString(),
            editTextTitle.getText().toString(),
            editTextIsbn.getText().toString(),
            imageBitmap
        );
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
