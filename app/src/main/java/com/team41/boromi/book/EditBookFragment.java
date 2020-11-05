package com.team41.boromi.book;

import static com.team41.boromi.constants.CommonConstants.REQUEST_IMAGE_CAPTURE;
import static com.team41.boromi.utility.Utility.isNotNullOrEmpty;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import com.team41.boromi.models.Book;

import java.io.ByteArrayInputStream;


public class EditBookFragment extends DialogFragment {
    private Button editBook;
    private EditText author;
    private EditText title;
    private EditText isbn;
    private ImageButton addImage;
    private Bitmap imageBitmap;
    Book editingBook;

    public EditBookFragment(Book editingBook) {
        this.editingBook = editingBook;
    }

    public static EditBookFragment newInstance(Book book) {
        EditBookFragment editBookFragment = new EditBookFragment(book);
        Bundle args = new Bundle();
        editBookFragment.setArguments(args);
        return editBookFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.edit_book_fragment, container);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        editBook = (Button) view.findViewById(R.id.edit_book_add_button);
        author = (EditText) view.findViewById(R.id.edit_book_author);
        title = (EditText) view.findViewById(R.id.edit_book_title);
        isbn = (EditText) view.findViewById(R.id.edit_book_isbn);
        addImage = (ImageButton) view.findViewById(R.id.edit_book_book_image);

        author.setText(editingBook.getAuthor());
        title.setText(editingBook.getTitle());
        isbn.setText(editingBook.getISBN());

        if (editingBook.getImg64() != null ){
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    Base64.decode(editingBook.getImg64(), Base64.DEFAULT));
            Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
            addImage.setImageBitmap(bitmap); // decode to image
        }

        editBook.setOnClickListener(view1 -> {
            String author_text = author.getText().toString();
            String title_text = title.getText().toString();
            String isbn_text = isbn.getText().toString();
            EditBookFragmentListener listener = (EditBookFragmentListener) getActivity();
            if (isNotNullOrEmpty(author_text) && isNotNullOrEmpty(title_text) && isNotNullOrEmpty(isbn_text)) {
                listener.onEditComplete(editingBook.getBookId(), author_text, title_text, isbn_text, imageBitmap);
                dismiss();
            }
        });

        addImage.setOnClickListener(view2 -> dispatchTakePictureIntent());
        addImage.setOnLongClickListener(view3 -> {
            addImage.setImageResource(R.drawable.add_photo_icon);
            this.imageBitmap = null;
            return true;
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
        } catch (Exception e) {
            // display error state to the user
        }
    }

    public interface EditBookFragmentListener {
        void onEditComplete(String BookID, String author, String title, String isbn, Bitmap image);
    }
}
