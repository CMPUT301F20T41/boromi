package com.team41.boromi.book;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.team41.boromi.R;
import com.team41.boromi.models.Book;
import java.io.ByteArrayInputStream;

public class ImageExpandFragment extends DialogFragment {
    ImageView Image;
    TextView Title;
    String img64;
    String BookTitle;

    public ImageExpandFragment(Book book){
        this.img64 = book.getImg64();
        this.BookTitle = book.getTitle();
    }

    public static ImageExpandFragment newInstance(Book book) {
        ImageExpandFragment imageExpandFragment = new ImageExpandFragment(book);
        Bundle args = new Bundle();
        imageExpandFragment.setArguments(args);
        return imageExpandFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.expanded_book_image, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Image = view.findViewById(R.id.expanded_image);
        Title = view.findViewById(R.id.expanded_image_title);

        Title.setText(BookTitle);
        if (img64 != null){
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    Base64.decode(img64, Base64.DEFAULT));
            Bitmap bitmap = BitmapFactory.decodeStream(byteArrayInputStream);
            Image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Image.setImageBitmap(bitmap);
        }
    }
}
