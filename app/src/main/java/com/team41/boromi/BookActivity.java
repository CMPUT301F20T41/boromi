package com.team41.boromi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.team41.boromi.adapters.PagerAdapter;
import com.team41.boromi.book.AddBookFragment;
import com.team41.boromi.book.BorrowedFragment;
import com.team41.boromi.book.DisplayBookFragment;
import com.team41.boromi.book.DisplayOtherUserFragment;
import com.team41.boromi.book.EditBookFragment;
import com.team41.boromi.book.MapFragment;
import com.team41.boromi.book.OwnedFragment;
import com.team41.boromi.book.SearchFragment;
import com.team41.boromi.book.SettingsFragment;
import com.team41.boromi.callbacks.BookCallback;
import com.team41.boromi.controllers.BookController;
import com.team41.boromi.controllers.BookRequestController;
import com.team41.boromi.controllers.BookReturnController;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.GoogleBook;
import com.team41.boromi.models.User;
import java.util.ArrayList;
import javax.inject.Inject;

/**
 * BookActivity is the main activity that will house all of the fragments. It creates the fragments
 * for the main tabs (OwnedFragment, BorrowedFragment, SearchFragment, MapFragment,
 * SettingsFragment) It uses PagerAdapter, ViewPager2, TabsLayout to switch betwen the tabs and
 * fragments. Each SubFragment may create more fragments. For example, OwnedFragment,
 * BorrowedFragment, SearchFragment will be creating GenericListFragments to house lists
 */
public class BookActivity extends AppCompatActivity implements
    AddBookFragment.AddBookFragmentListener, EditBookFragment.EditBookFragmentListener {

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
  private MenuItem addButton;
  private BookViewModel bookViewModel;
  private ProgressBar spinner;
  String bookAPIURL = "https://www.googleapis.com/books/v1/volumes?q=+isbn=";
  String key = "&key=AIzaSyB2TMW4SaN9uQyABkPkJTfE77YWaLYMWGo"; // should never do this but lazy
  RequestQueue rq;
  Gson gson = new Gson();

  /**
   * Initialize any values, create the fragments, link pagerAdapter, ViewPager2, and tabLayout
   *
   * @param savedInstanceState
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_book);
    ((BoromiApp) getApplicationContext()).appComponent.inject(this);
    bookViewModel = new ViewModelProvider(this, new ViewModelFactory(this))
        .get(BookViewModel.class);
    bookViewModel.queryAllData();
    bookViewModel.temp = "hello";
    pagerAdapter = new PagerAdapter(getSupportFragmentManager(), getLifecycle());
    Toolbar toolbar = (Toolbar) findViewById(R.id.book_toolbar);
    setSupportActionBar(toolbar);
    spinner = findViewById(R.id.progress_loading);
    spinner.setVisibility(View.GONE);
    rq = Volley.newRequestQueue(this);

    // Subscribes to the pushNotifications topic
    FirebaseMessaging.getInstance().subscribeToTopic(user.getUUID());

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
    Bundle bundle = new Bundle();
    bundle.putInt("Mode", 0);
    pagerAdapter
        .addFragment(new Pair<Class<? extends Fragment>, Bundle>(MapFragment.class, bundle));
    pagerAdapter
        .addFragment(new Pair<Class<? extends Fragment>, Bundle>(SettingsFragment.class, null));

    // configure viewpager2 and initialize page adapter
    viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
    viewPager2.setOffscreenPageLimit(tabLayout.getTabCount());
    viewPager2.setUserInputEnabled(false);
    viewPager2.setAdapter(pagerAdapter);
    final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh);
    /**
     * Switch fragments depending on the selected tab
     */
    tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
      @Override
      public void onTabSelected(Tab tab) {
        if (tab.getPosition() == 3) {
          swipeRefreshLayout.setEnabled(false);
        } else {
          swipeRefreshLayout.setEnabled(true);
        }
        viewPager2.setCurrentItem(tab.getPosition());
      }

      @Override
      public void onTabUnselected(Tab tab) {

      }

      @Override
      public void onTabReselected(Tab tab) {

      }
    });

    /**
     * Pull down refresh updates OwnedFragment tab and BorrowedFragment tab.
     */
    swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh() {
        bookViewModel.queryAllData();
        swipeRefreshLayout.setRefreshing(false);
      }
    });
  }

  /**
   * Custom toolbar to house add/scan buttons
   *
   * @param menu
   * @return
   */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.toolbar_menu, menu);
    return true;
  }

  /**
   * Logic when toolbar buttons are clicked
   *
   * @param item selected menu item
   * @return
   */
  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.toolbar_add:
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddBookFragment addBookFragment = AddBookFragment.newInstance();
        addBookFragment.show(fragmentManager, "addBook");
        return true;
      case R.id.toolbar_scan:
        // TODO add toolbar scan logic
        scanForBook();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public BookViewModel getBookViewModel() {
    return bookViewModel;
  }

  public void switchTabs(int index) {
    tabLayout.getTabAt(index).select();
  }

  public TabLayout.Tab getTab(int index) {
    return tabLayout.getTabAt(index);
  }

  public Fragment getMainFragment(String pos) {
    return getSupportFragmentManager().findFragmentByTag(pos);
  }

  /**
   * Sets up bundle for GenericListFragment
   *
   * @param layout  model to inject
   * @param data    book data to inject
   * @param messsge description of the tab
   * @param parent  parent fragment tag
   * @param tag     genericlistfragment tag
   * @return Bundle
   */
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

  /**
   * Gets the bookReturnController
   *
   * @return BookReturnController
   */
  public BookReturnController getBookReturnController() {
    return bookReturnController;
  }

  /**
   * Gets the BookController
   *
   * @return BookController
   */
  public BookController getBookController() {
    return bookController;
  }

  /**
   * Gets the BookRequestController
   *
   * @return BookRequestController
   */
  public BookRequestController getBookRequestController() {
    return bookRequestController;
  }

  /**
   * Gets logged in user
   *
   * @return User
   */
  public User getUser() {
    return user;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if(requestCode == 49374) {
      IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
      if(result != null) {
        if (result.getContents() == null) {
          Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
        } else {
          Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
          queryBookData(result.getContents());
        }
      }
    } else {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }


  /**
   * Logic when a book is added. Call the controller method then update the fragment
   *
   * @param author author of the book
   * @param title  title of the book
   * @param isbn   isbn of the book
   * @param image  image of the book
   */
  @Override
  public void onComplete(String author, String title, String isbn, Bitmap image) {
    bookController.addBook(author, isbn, title, image, new BookCallback() {
      @Override
      public void onSuccess(ArrayList<Book> books) {
        bookViewModel.getOwnerAvailable();
      }

      @Override
      public void onFailure(Exception e) {

      }
    });
  }

  /**
   * Callback for editing a book. When the book dialog closes
   *
   * @param BookID id of the edited book
   * @param author new author
   * @param title  new title
   * @param isbn   new isbn
   * @param image  new image
   */
  @Override
  public void onEditComplete(String BookID, String author, String title, String isbn,
      Bitmap image) {
    bookController.editBook(BookID, author, isbn, title, image, new BookCallback() {
      @Override
      public void onSuccess(ArrayList<Book> books) {
        bookViewModel.getOwnerAvailable();
      }

      @Override
      public void onFailure(Exception e) {
      }
    });
  }

  @Override
  public void onBackPressed() {

  }

  private void scanForBook() {
    IntentIntegrator intentIntegrator = new IntentIntegrator(this);
    intentIntegrator.setRequestCode(49374);
    intentIntegrator.initiateScan();
  }

  private void queryBookData(String isbn) {
    spinner.setVisibility(View.VISIBLE);
    StringRequest stringRequest = new StringRequest(Request.Method.GET, formatQuery(isbn),
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
            // Display the first 500 characters of the response string.
            displayBookData(response, isbn);
          }
        }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        spinner.setVisibility(View.GONE);
        makeToast("NETWORK ERROR OCCURRED ON REQUEST FOR BOOK");
      }
    });

// Add the request to the RequestQueue.
    rq.add(stringRequest);
  }

  private String formatQuery(String isbn) {
    String formattedQuery = bookAPIURL;
    formattedQuery += isbn;
    formattedQuery += key;
    return formattedQuery;
  }

  private void makeToast(String msg) {
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
  }

  private void displayBookData(String book, String isbn) {
    System.out.println("DISPLAYING BOOKS");

    GoogleBook gb = gson.fromJson(book, GoogleBook.class);
    Book bookToDisplay = new Book();
    bookToDisplay.setTitle(gb.getTitle(0));
    bookToDisplay.setAuthor(gb.getFirstAuthor(0));
    bookToDisplay.setISBN(isbn);
    spinner.setVisibility(View.GONE);
    DisplayBookFragment displayBookFragment = DisplayBookFragment.newInstance(bookToDisplay);
    displayBookFragment.show(getSupportFragmentManager(), "displayUsers");
  }



}
