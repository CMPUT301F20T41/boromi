package com.team41.boromi;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.team41.boromi.book.GenericListFragment;
import com.team41.boromi.book.MapFragment;
import com.team41.boromi.callbacks.BookCallback;
import com.team41.boromi.callbacks.BookRequestCallback;
import com.team41.boromi.controllers.BookController;
import com.team41.boromi.controllers.BookRequestController;
import com.team41.boromi.controllers.BookReturnController;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;
import com.team41.boromi.models.BookReturn;
import com.team41.boromi.models.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BookViewModel extends ViewModel {

  public String temp;
  private BookActivity bookActivity;
  private BookReturnController bookReturnController;
  private BookController bookController;
  private BookRequestController bookRequestController;
  private User user;
  private Map<String, MutableLiveData<ArrayList<Book>>> collections = new HashMap<>();
  private MutableLiveData<Map<Book, List<BookRequest>>> ownedRequestedCollection = new MutableLiveData<>();
  private MutableLiveData<Map<Book, List<BookRequest>>> borrowedRequestedCollection = new MutableLiveData<>();
  private Book exchangeBook;
  private BookRequest exchangeBookRequest;
  private BookReturn exchangeBookReturn;

  public BookViewModel(BookActivity bookActivity) {
    this.bookActivity = bookActivity;
    bookRequestController = bookActivity.getBookRequestController();
    bookReturnController = bookActivity.getBookReturnController();
    bookController = bookActivity.getBookController();
    user = bookActivity.getUser();
    initCollection();
  }

  private void initCollection() {
    collections.put("OwnedAvailable", new MutableLiveData<>());
    collections.put("OwnedAccepted", new MutableLiveData<>());
    collections.put("OwnedLent", new MutableLiveData<>());
    collections.put("BorrowedBorrowed", new MutableLiveData<>());
    collections.put("BorrowedAccepted", new MutableLiveData<>());
    collections.put("SearchResults", new MutableLiveData<>());
    collections.put("MapLocations", new MutableLiveData<>());

  }

  public void queryAllData() {
    getOwnerAvailable();
    getOwnerAccepted();
    getOwnerRequests();
    getOwnerLent();
    getBorrowedAccepted();
    getBorrowedBorrowed();
    getBorrowedRequested();
    getBookLocations();
  }

  public LiveData<ArrayList<Book>> getData(GenericListFragment fragment) {
    if (fragment.getParent().equals("Owned")) {
      switch (fragment.tag) {
        case "Available":
          return collections.get("OwnedAvailable");
        case "Accepted":
          return collections.get("OwnedAccepted");
        case "Lent":
          return collections.get("OwnedLent");
      }
    } else if (fragment.getParent().equals("Borrowed")) {
      switch (fragment.tag) {
        case "Borrowed":
          return collections.get("BorrowedBorrowed");
        case "Accepted":
          return collections.get("BorrowedAccepted");
      }
    }
    return null;
  }

  public LiveData<Map<Book, List<BookRequest>>> getRequested(GenericListFragment fragment) {
    MutableLiveData<Map<Book, List<BookRequest>>> data = new MutableLiveData<>();
    if (fragment.getParent().equals("Owned") && fragment.tag.equals("Requested")) {
      return ownedRequestedCollection;
    } else if (fragment.getParent().equals("Borrowed") && fragment.tag.equals("Requested")) {
      return borrowedRequestedCollection;
    }
    return data;
  }

  public LiveData<ArrayList<Book>> getSearchResults() {
    return collections.get("SearchResults");
  }

  public LiveData<ArrayList<Book>> getLocations() {
    return collections.get("MapLocations");
  }

  public User getUser() {
    return user;
  }

  //*********************************************
  // OWNER TAB
  //*********************************************

  /**
   * Backend call to get the books that the user owns that are available
   */
  public void getOwnerAvailable() {
    bookController.getOwnerAvailableBooks(user.getUUID(), new BookCallback() {
      @Override
      public void onSuccess(ArrayList<Book> books) {
        bookActivity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            collections.get("OwnedAvailable").setValue(books);
          }
        });
      }

      @Override
      public void onFailure(Exception e) {

      }
    });
  }

  /**
   * Backend call to get the books that the user owns that have been requested.
   */
  public void getOwnerRequests() {
    bookRequestController.getRequestOnOwnedBooks(new BookRequestCallback() {
      @Override
      public void onComplete(Map<Book, List<BookRequest>> bookWithRequests) {
        bookActivity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            ownedRequestedCollection.setValue(bookWithRequests);
          }
        });
      }
    });
  }

  /**
   * Backend call to get the books that the owner has accepted to be borrowed
   */
  public void getOwnerAccepted() {
    bookController.getOwnerAcceptedBooks(user.getUUID(), new BookCallback() {
      @Override
      public void onSuccess(ArrayList<Book> books) {
        bookActivity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            collections.get("OwnedAccepted").setValue(books);
          }
        });
      }

      @Override
      public void onFailure(Exception e) {

      }
    });
  }

  /**
   * Backend call to get the books that the owner owns that are being lent
   */
  public void getOwnerLent() {
    bookController.getOwnerBorrowedBooks(user.getUUID(),
        new BookCallback() {
          @Override
          public void onSuccess(ArrayList<Book> books) {
            bookActivity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                collections.get("OwnedLent").setValue(books);
              }
            });
          }

          @Override
          public void onFailure(Exception e) {

          }
        });
  }

  //*********************************************
  // BORROWER TAB
  //*********************************************

  /**
   * Backend call to obtain the books that the user is borrowing
   */
  public void getBorrowedBorrowed() {
    bookController.getOwnerBorrowingBooks(user.getUUID(),
        new BookCallback() {
          @Override
          public void onSuccess(ArrayList<Book> books) {
            bookActivity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                collections.get("BorrowedBorrowed").setValue(books);
              }
            });
          }

          @Override
          public void onFailure(Exception e) {

          }
        });
  }

  /**
   * Backend call to get the books the user has requested
   */
  public void getBorrowedRequested() {
    bookRequestController.getRequestedBooks(new BookRequestCallback() {
      @Override
      public void onComplete(Map<Book, List<BookRequest>> bookWithRequests) {
        bookActivity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            borrowedRequestedCollection.setValue(bookWithRequests);
          }
        });
      }
    });
  }

  /**
   * Backend call to get the books that the user has been accepted to borrow
   */
  public void getBorrowedAccepted() {
    bookController.getBooksOthersAccepted(new BookCallback() {
      @Override
      public void onSuccess(ArrayList<Book> books) {
        bookActivity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            collections.get("BorrowedAccepted").setValue(books);
          }
        });
      }

      @Override
      public void onFailure(Exception e) {

      }
    });
  }

  //*********************************************
  // Searching TAB
  //*********************************************
  public void searchBooks(String keywords) {
    bookActivity.getBookController().findBooks(keywords, new BookCallback() {
      @Override
      public void onSuccess(ArrayList<Book> books) {
        ArrayList<Book> filtered = (ArrayList<Book>) books.stream().filter(
            book -> !book.getOwner().equals(user.getUUID())).collect(Collectors.toList());
        bookActivity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            collections.get("SearchResults").setValue(filtered);
          }
        });
      }

      @Override
      public void onFailure(Exception e) {
        bookActivity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            collections.get("SearchResults").setValue(null);
          }
        });
      }
    });
  }

  //*********************************************
  // Map TAB
  //*********************************************
  public void getBookLocations() {
    bookController.getOwnerBorrowerLocations(new BookCallback() {
      @Override
      public void onSuccess(ArrayList<Book> books) {
        bookActivity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            collections.get("MapLocations").setValue(books);
          }
        });
      }

      @Override
      public void onFailure(Exception e) {

      }
    });
  }

  public void navExchangeLocation(Book book, BookRequest bookRequest) {
    exchangeBook = book;
    exchangeBookRequest = bookRequest;
    TabLayout.Tab mapTab = bookActivity.getTab(3);
    MapFragment mapFragment = (MapFragment) bookActivity.getMainFragment("f3");
    mapFragment.setMode(1);
    mapTab.select();
  }

  public void setExchangeLocation(LatLng location) {
    exchangeBookRequest.setLocation(location);
    bookRequestController.acceptBookRequest(exchangeBookRequest, new BookRequestCallback() {
      @Override
      public void onComplete(Map<Book, List<BookRequest>> bookWithRequests) {
        bookActivity.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            Map<Book, List<BookRequest>> tempM = ownedRequestedCollection.getValue();
            tempM.remove(exchangeBook);
            ownedRequestedCollection.setValue(tempM);
            getBookLocations();
            getOwnerAccepted();
            getOwnerAvailable();
            exchangeBook = null;
            exchangeBookRequest = null;
          }
        });
      }
    });
  }

  public Book getExchangeBook() {
    return exchangeBook;
  }

  public void setExchangeBook(Book exchangeBook) {
    this.exchangeBook = exchangeBook;
  }

  public BookRequest getExchangeBookRequest() {
    return exchangeBookRequest;
  }

  public void setExchangeBookRequest(BookRequest exchangeBookRequest) {
    this.exchangeBookRequest = exchangeBookRequest;
  }
}