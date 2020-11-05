package com.team41.boromi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;
import com.team41.boromi.adapters.PagerAdapter;
import com.team41.boromi.book.AddBookFragment;
import com.team41.boromi.book.BorrowedFragment;
import com.team41.boromi.book.EditBookFragment;
import com.team41.boromi.book.GenericListFragment;
import com.team41.boromi.book.MapFragment;
import com.team41.boromi.book.OwnedFragment;
import com.team41.boromi.book.SearchFragment;
import com.team41.boromi.book.SettingsFragment;
import com.team41.boromi.callbacks.BookCallback;
import com.team41.boromi.controllers.AuthenticationController;
import com.team41.boromi.controllers.BookController;
import com.team41.boromi.controllers.BookRequestController;
import com.team41.boromi.controllers.BookReturnController;
import com.team41.boromi.dagger.BoromiModule;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;
import com.team41.boromi.models.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import com.team41.boromi.models.Book;
import java.util.ArrayList;

public class BookActivity extends AppCompatActivity implements AddBookFragment.AddBookFragmentListener, EditBookFragment.EditBookFragmentListener {

  private static final String LAYOUT_PARAM1 = "LayoutID";
  private static final String DATA_PARAM2 = "Data";
  private static final String MSG_PARAM3 = "Msg";
  private static final String PARENT_PARAM4 = "Parent";
  private static final String TAG_PARAM5 = "TAG";

  private final String TAG = "BOOK ACTIVITY";
  public Bitmap addedImage;
  @Inject
  BookReturnController bookReturnController;
  @Inject
  BookController bookController;
  @Inject
  BookRequestController bookRequestController;
  @Inject
  User user;
  private ViewPager2 viewPager2;
  private PagerAdapter pagerAdapter;
  private TabLayout tabLayout;
  private Map<String, ArrayList<Book>> collections = new HashMap<>();
  private Map<Book, List<BookRequest>> requestsCollections = new HashMap<>();
  private MenuItem addButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_book);
    ((BoromiApp) getApplicationContext()).appComponent.inject(this);
    pagerAdapter = new PagerAdapter(getSupportFragmentManager(), getLifecycle());
    Toolbar toolbar = (Toolbar) findViewById(R.id.book_toolbar);
    setSupportActionBar(toolbar);

    // Prevents the keyboard from moving the entire screen up
    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    tabLayout = findViewById(R.id.tabs_main);
    viewPager2 = findViewById(R.id.view_pager_main);
    TabItem tabBorrow = findViewById(R.id.tab_borrowed_books);
    TabItem tabOwned = findViewById(R.id.tab_owned_books);
    TabItem tabSearch = findViewById(R.id.tab_search);
    TabItem tabMap = findViewById(R.id.tab_location);
    TabItem tabSettings = findViewById(R.id.tab_settings);

    // Add fragments for each tab
    pagerAdapter
            .addFragment(new Pair<Class<? extends Fragment>, Bundle>(OwnedFragment.class, null));
    pagerAdapter
            .addFragment(new Pair<Class<? extends Fragment>, Bundle>(BorrowedFragment.class, null));
    pagerAdapter
            .addFragment(new Pair<Class<? extends Fragment>, Bundle>(SearchFragment.class, null));
    pagerAdapter.addFragment(new Pair<Class<? extends Fragment>, Bundle>(MapFragment.class, null));
    pagerAdapter
            .addFragment(new Pair<Class<? extends Fragment>, Bundle>(SettingsFragment.class, null));

    // configure viewpager2 and initialize page adapter
    viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
    viewPager2.setOffscreenPageLimit(tabLayout.getTabCount());
    viewPager2.setUserInputEnabled(false);
    viewPager2.setAdapter(pagerAdapter);
    tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
      @Override
      public void onTabSelected(Tab tab) {
        viewPager2.setCurrentItem(tab.getPosition());
      }

      @Override
      public void onTabUnselected(Tab tab) {

      }

      @Override
      public void onTabReselected(Tab tab) {

      }
    });

  }

  public Bitmap getAddedImage() {
    return addedImage;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.toolbar_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.toolbar_add:
        // TODO add toolbar add logic
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddBookFragment addBookFragment = AddBookFragment.newInstance();
        addBookFragment.show(fragmentManager, "addBook");
        return true;
      case R.id.toolbar_scan:
        // TODO add toolbar scan logic
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }


  public Bundle setupBundle(
          int layout,
          ArrayList<Book> data,
          String messsge,
          String parent,
          String tag
  ) {
    Bundle bundle = new Bundle();
    bundle.putInt(LAYOUT_PARAM1, layout);
    bundle.putSerializable(DATA_PARAM2, data);
    bundle.putString(MSG_PARAM3, messsge);
    bundle.putString(PARENT_PARAM4, parent);
    bundle.putString(TAG_PARAM5, tag);
    return bundle;
  }

  public ArrayList<Book> getOwnerAvailable() {
    System.out.println(collections.get("OwnerAvailable"));
    return collections.get("OwnerAvailable");
  }

  public ArrayList<Book> getOwnerRequests() {
    System.out.println(collections.get("OwnerRequests"));

    return collections.get("OwnerRequests");
  }

  public ArrayList<Book> getOwnerAccepted() {
    System.out.println(collections.get("OwnerAccepted"));

    return collections.get("OwnerAccepted");
  }

  public ArrayList<Book> getOwnerLent() {
    System.out.println(collections.get("OwnerLent"));

    return collections.get("OwnerLent");
  }

  public BookReturnController getBookReturnController() {
    return bookReturnController;
  }

  public BookController getBookController() {
    return bookController;
  }

  public BookRequestController getBookRequestController() {
    return bookRequestController;
  }

  public User getUser() {
    return user;
  }

  public Map<String, ArrayList<Book>> getCollections() {
    return collections;
  }

  public Map<Book, List<BookRequest>> getRequestsCollections() {
    return requestsCollections;
  }

  public void setRequestsCollections(
          Map<Book, List<BookRequest>> requestsCollections) {
    this.requestsCollections = requestsCollections;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onComplete(String author, String title, String isbn, Bitmap image) {

    BookCallback bookCallback = new BookCallback() {
      @Override
      public void onSuccess(ArrayList<Book> books) {
        for (Fragment f : getSupportFragmentManager().getFragments()) {
          if (f.getClass().getSimpleName().equals("OwnedFragment")) {
            for (Fragment fc : f.getChildFragmentManager().getFragments()) {
              GenericListFragment gf = (GenericListFragment) fc;
              if (gf.tag.equals("Available")) {
                ((OwnedFragment) f).getData(gf.tag, gf);
              }
            }
          }
        }
      }

      @Override
      public void onFailure(Exception e) {

      }
    };

    bookController.addBook(author, isbn, title, image, bookCallback);
  }

  @Override
  public void onEditComplete(String BookID, String author, String title, String isbn, Bitmap image){
    bookController.editBook(BookID, author, isbn, title, image, new BookCallback() {
      @Override
      public void onSuccess(ArrayList<Book> books) {
        for (Fragment f : getSupportFragmentManager().getFragments()) {
          if (f.getClass().getSimpleName().equals("OwnedFragment")) {
            for (Fragment fc : f.getChildFragmentManager().getFragments()) {
              GenericListFragment gf = (GenericListFragment) fc;
              if (gf.tag.equals("Available")) {
                ((OwnedFragment) f).getData(gf.tag, gf);
              }
            }
          }
        }
      }

      @Override
      public void onFailure(Exception e) {
      }
    });
  }



  public void updateFragment(String mainTab, String subTab) {
    Optional<Fragment> f = getSupportFragmentManager().getFragments().stream().filter(fragment -> fragment.getClass().getSimpleName().equals(mainTab)).findFirst();
    if (f.isPresent()) {
      if (mainTab.equals("OwnedFragment")){
        OwnedFragment ownedFragment = OwnedFragment.class.cast(f.get());
        Optional<Fragment> subFragment = ownedFragment.getChildFragmentManager().getFragments().stream().filter(fragment -> ((GenericListFragment) fragment).tag.equals(subTab)).findFirst();
        if (subFragment.isPresent()) {
          ownedFragment.getData(subTab,(GenericListFragment) subFragment.get());
        }
      } else if (mainTab.equals("BorrowedFragment")) {
        BorrowedFragment borrowedFragment = BorrowedFragment.class.cast(f.get());
        Optional<Fragment> subFragment = borrowedFragment.getChildFragmentManager().getFragments().stream().filter(fragment -> ((GenericListFragment) fragment).tag.equals(subTab)).findFirst();
        if (subFragment.isPresent()) {
          borrowedFragment.getData(subTab,(GenericListFragment) subFragment.get());
        }
      } else {
        return;
      }
    }
  }

}
