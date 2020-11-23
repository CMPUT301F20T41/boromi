package com.team41.boromi.book;

import static com.team41.boromi.constants.CommonConstants.REQUEST_IMAGE_CAPTURE;
import static com.team41.boromi.utility.Utility.isNotNullOrEmpty;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.team41.boromi.BookActivity;
import com.team41.boromi.R;
import com.team41.boromi.models.Book;
import java.io.ByteArrayInputStream;

/**
 * This Dialog Fragment is used when the user chooses to edit a book.
 */
public class EditBookFragment extends DialogFragment {

  Book editingBook;
  private Button editBook;
  private EditText author;
  private EditText title;
  private EditText isbn;
  /**
   * Used to validate input fields
   */
  TextWatcher allFieldsWatcher = new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      // Empty method, required for text watcher
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
      String authorText = author.getText().toString().trim();
      String titleText = title.getText().toString().trim();
      String isbnText = isbn.getText().toString().trim();

      // Sets the button if all the text fields have content and the ISBN is 13 characters
      editBook.setEnabled(
          isNotNullOrEmpty(authorText) && isNotNullOrEmpty(titleText) && isNotNullOrEmpty(isbnText)
              && returnIfISBNGood(isbnText));
    }

    @Override
    public void afterTextChanged(Editable s) {
      // Empty method, required for text watcher
    }
  };
  private ImageButton addImage;
  private Bitmap imageBitmap;

  public EditBookFragment(Book editingBook) {
    this.editingBook = editingBook;
  }

  /**
   * Factory method to create EditBookFragment
   *
   * @param book
   * @return
   */
  public static EditBookFragment newInstance(Book book) {
    EditBookFragment editBookFragment = new EditBookFragment(book);
    Bundle args = new Bundle();
    editBookFragment.setArguments(args);
    return editBookFragment;
  }

  /**
   * Initialize any values
   *
   * @param inflater
   * @param container
   * @param savedInstanceState
   * @return
   */
  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.edit_book_fragment, container);
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  /**
   * Bind any listeners or values
   *
   * @param view
   * @param savedInstanceState
   */
  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    editBook = (Button) view.findViewById(R.id.edit_book_add_button);
    author = (EditText) view.findViewById(R.id.edit_book_author);
    title = (EditText) view.findViewById(R.id.edit_book_title);
    isbn = (EditText) view.findViewById(R.id.edit_book_isbn);
    addImage = (ImageButton) view.findViewById(R.id.edit_book_book_image);

    // Makes the image rounded
    addImage.setClipToOutline(true);

    author.setText(editingBook.getAuthor());
    title.setText(editingBook.getTitle());
    isbn.setText(editingBook.getISBN());

    author.addTextChangedListener(allFieldsWatcher);
    title.addTextChangedListener(allFieldsWatcher);
    isbn.addTextChangedListener(allFieldsWatcher);

    addImage.setClipToOutline(true);

    if (editingBook.getImg64() != null) {
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
          Base64.decode(editingBook.getImg64(), Base64.DEFAULT));
      Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
      addImage.setScaleType(ImageView.ScaleType.FIT_XY);
      addImage.setImageBitmap(bitmap); // decode to image
    }

    editBook.setOnClickListener(view1 -> {
      EditBookFragmentListener listener = (EditBookFragmentListener) getActivity();

      assert listener != null;

      if (imageBitmap == null && editingBook.getImg64() != null) {
        imageBitmap = ((BookActivity) getActivity()).getBookController()
            .decodeBookImage(editingBook);
      }
      listener.onEditComplete(
          editingBook.getBookId(),
          author.getText().toString(),
          title.getText().toString(),
          isbn.getText().toString(),
          imageBitmap);
      dismiss();
    });

    addImage.setOnClickListener(view2 -> dispatchTakePictureIntent());
    addImage.setOnLongClickListener(view3 -> {
      addImage.setImageResource(R.drawable.add_photo_icon);
      this.imageBitmap = null;
      addImage.setScaleType(ImageView.ScaleType.CENTER);
      return true;
    });
  }

  /**
   * Called when returning from Camera Activity and stores the photo taken.
   *
   * @param requestCode
   * @param resultCode
   * @param data
   */
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == -1) {
      Bundle extras = data.getExtras();
      Bitmap imageBitmap = (Bitmap) extras.get("data");
      addImage.setImageBitmap(imageBitmap);
      addImage.setScaleType(ImageView.ScaleType.FIT_XY);
      this.imageBitmap = imageBitmap;
    }
  }

  /**
   * Starts camera event to take a new photo
   */
  private void dispatchTakePictureIntent() {
    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    try {
      startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    } catch (Exception e) {
      // display error state to the user
    }
  }

  /**
   * returns true if isbn is length 10 or 13.
   *
   * @param isbn
   * @return
   */
  public boolean returnIfISBNGood(String isbn) {
    return isbn.length() == 13 || isbn.length() == 10;
  }

  /**
   * Listener that is called when edit book button is pressed
   */
  public interface EditBookFragmentListener {

    void onEditComplete(String BookID, String author, String title, String isbn, Bitmap image);
  }
}
