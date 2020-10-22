package com.team41.boromi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.team41.boromi.book.OwnedBooks.OwnedFragment;
import com.team41.boromi.book.BorrowedFragment;
import com.team41.boromi.book.MapFragment;
import com.team41.boromi.book.SearchFragment;
import com.team41.boromi.book.SettingsFragment;

public class BookActivity extends AppCompatActivity {
    Button bookButton;
    Button borrowedButton;
    Button searchButton;
    Button mapButton;
    Button settingsButton;

    private FragmentManager manager = null;
    FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);
        bookButton = findViewById(R.id.owned_books);
        borrowedButton = findViewById(R.id.borrowed_books);
        searchButton = findViewById(R.id.search);
        mapButton = findViewById(R.id.location);
        settingsButton = findViewById(R.id.settings);

        if (manager == null) {
            manager = getSupportFragmentManager();
        }
        if (manager.findFragmentById(R.id.book_fragment) == null) {
            OwnedFragment ownedFragment = new OwnedFragment();
            ft = manager.beginTransaction();
            ft.add(R.id.book_fragment, ownedFragment).commit();
        }

        bookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OwnedFragment ownedFragment = new OwnedFragment();
                ft = manager.beginTransaction();
                ft.replace(R.id.book_fragment, ownedFragment).addToBackStack("book").commit();
            }
        });
        borrowedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BorrowedFragment borrowedFragment = new BorrowedFragment();
                ft = manager.beginTransaction();
                ft.replace(R.id.book_fragment, borrowedFragment).addToBackStack("borrowed").commit();
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchFragment searchFragment = new SearchFragment();
                ft = manager.beginTransaction();
                ft.replace(R.id.book_fragment, searchFragment).addToBackStack("search").commit();
            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapFragment mapFragment = new MapFragment();
                ft = manager.beginTransaction();
                ft.replace(R.id.book_fragment, mapFragment).addToBackStack("map").commit();
            }
        });
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsFragment settingsFragment = new SettingsFragment();
                ft = manager.beginTransaction();
                ft.replace(R.id.book_fragment, settingsFragment).addToBackStack("settings").commit();
            }
        });

    }
}
