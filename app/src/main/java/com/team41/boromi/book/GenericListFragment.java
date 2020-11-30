package com.team41.boromi.book;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.team41.boromi.BookActivity;
import com.team41.boromi.BookViewModel;
import com.team41.boromi.R;
import com.team41.boromi.adapters.GenericListAdapter;
import com.team41.boromi.callbacks.BookCallback;
import com.team41.boromi.callbacks.ReturnCallback;
import com.team41.boromi.constants.CommonConstants;
import com.team41.boromi.constants.CommonConstants.BookStatus;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.team41.boromi.constants.CommonConstants.REQUEST_IMAGE_CAPTURE;

/**
 * GenericListFragment is used for each of the subtabs and the search. It will inflate the model
 * that it has been provided with
 */
public class GenericListFragment extends Fragment {

  // Bundle tags
  private static final String LAYOUT_PARAM1 = "LayoutID";
  private static final String DATA_PARAM2 = "Data";
  private static final String MSG_PARAM3 = "Msg";
  private static final String PARENT_PARAM4 = "Parent";
  private static final String TAG_PARAM5 = "TAG";
  private static final String TAG = "GenericListFrag";

  public String tag;
  public boolean cancelled_scan;
  RecyclerView recyclerView;
  GenericListAdapter listAdapter;
  private ArrayList<Book> bookDataList = new ArrayList<>();
  private int layoutID;
  private String tempMsg;
  private String parent;
  private Map<Book, List<BookRequest>> bookWithRequests;
  private BookViewModel bookViewModel;
  private Book bookToExchange;

  public GenericListFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @return A new instance of fragment OwnedAcceptedFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static GenericListFragment newInstance(Bundle bundle) {
    GenericListFragment fragment = new GenericListFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  /**
   * Initialize any values/unpack bundle
   *
   * @param savedInstanceState
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);
    if (getArguments() != null) {
      layoutID = getArguments().getInt(LAYOUT_PARAM1);
      bookDataList = (ArrayList<Book>) getArguments().getSerializable(DATA_PARAM2);
      tempMsg = getArguments().getString(MSG_PARAM3);
      parent = getArguments().getString(PARENT_PARAM4);
      tag = getArguments().getString(TAG_PARAM5);
    }
  }

  /**
   * Gets the parent tag. Example if this instance of GenericListFragment is the sub tab of Owner
   * books, then the parent would be "Owner"
   *
   * @return String tag of the parent fragment
   */
  public String getParent() {
    return parent;
  }

  /**
   * Bind any listeners and initialize any values. This will also set up the GenericListAdapter
   *
   * @param inflater
   * @param container
   * @param savedInstanceState
   * @return
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_generic_list, container, false);
    TextView tempMsgView = view.findViewById(R.id.tempMessage);
    recyclerView = view.findViewById(R.id.generic_list);
    listAdapter = new GenericListAdapter(bookDataList, bookWithRequests, layoutID,
        (BookActivity) getActivity(), this);
    recyclerView.setAdapter(listAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    tempMsgView.setText(tempMsg);

    final Observer<ArrayList<Book>> bookDataObserver = new Observer<ArrayList<Book>>() {
      @Override
      public void onChanged(ArrayList<Book> books) {
        if (books == null) {
          books = new ArrayList<>();
        }
        bookDataList.clear();
        bookDataList.addAll(books);
        listAdapter.notifyDataSetChanged();
      }
    };
    final Observer<Map<Book, List<BookRequest>>> bookRequestDataObserver = new Observer<Map<Book, List<BookRequest>>>() {
      @Override
      public void onChanged(Map<Book, List<BookRequest>> bookListMap) {
        bookDataList.clear();
        bookDataList.addAll(bookListMap.keySet());
        bookWithRequests = bookListMap;
        listAdapter.setBookWithRequests(bookWithRequests);
        listAdapter.notifyDataSetChanged();
        listAdapter.notifySubAdapters();
      }
    };
    if (tag.equals("Requested")) {
      bookViewModel.getRequested(this).observe(getViewLifecycleOwner(), bookRequestDataObserver);
    } else {
      bookViewModel.getData(this).observe(getViewLifecycleOwner(), bookDataObserver);
    }
    return view;
  }

  /**
   * Returns instance of BookViewModel
   * @return BookViewModel
   */
  public BookViewModel getBookViewModel() {
    return bookViewModel;
  }

  /**
   * Used in the Accepted tabs to process the exchanging of the book. It will also refresh the lent,
   * accepted, borrowed, requested subtabs
   *
   * @param book Book to be exchanged
   */
  public void bookExchangeRequest(Book book) {
    BookActivity bookActivity = (BookActivity) getActivity();
    bookActivity.getBookController().updateBookExchange(bookActivity.getUser().getUUID(), book,
        new BookCallback() {
          @Override
          public void onSuccess(ArrayList<Book> books) {
            if (books.get(0).getStatus() == BookStatus.BORROWED) {
              bookViewModel.getOwnerLent();
              bookViewModel.getOwnerAccepted();
              bookViewModel.getBorrowedBorrowed();
              bookViewModel.getBorrowedRequested();
            }
          }

          @Override
          public void onFailure(Exception e) {

          }
        });
  }

  /**
   * Accepts return request
   * @param book Book to be returned
   */
  public void bookReturnRequest(Book book) {
    BookActivity bookActivity = (BookActivity) getActivity();
    bookActivity.getBookReturnController().acceptReturnRequest(book.getBookId(), new ReturnCallback() {
      @Override
      public void onSuccess(Book book) {
        if (book.getStatus()== BookStatus.AVAILABLE) {
          bookViewModel.getOwnerAvailable();
          bookViewModel.getOwnerLent();
          bookViewModel.getOwnerAccepted();
          bookViewModel.getBorrowedBorrowed();
          bookViewModel.getBorrowedRequested();
        }
      }
      @Override
      public void onFailure() {
      }
    });
  }

  /**
   * Verifies barcode on book
   * @param book Book object to be verified
   */
  public void verifyBarcode(Book book) {
    bookToExchange = book;
    dispatchTakeBarcodeIntent();
  }

  /**
   * Starts barcode scanning
   */
  private void dispatchTakeBarcodeIntent() {
    IntentIntegrator.forSupportFragment(this).initiateScan();
  }

  /**
   * Return from barcode scanning
   * @param requestCode
   * @param resultCode
   * @param data
   */
  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
      IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
      if(result != null) {
        if(result.getContents() == null) {
          Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
          bookViewModel.getBorrowedBorrowed();
        } else {
          Toast.makeText(getActivity(), "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
          completeBookExchange(result.getContents());
        }
      } else {
        super.onActivityResult(requestCode, resultCode, data);
      }
  }

  /**
   * Completes book exchange by comparing the isbn and status
   * @param scannedISBN scanned isbn
   */
  public void completeBookExchange(String scannedISBN) {
    BookActivity bookActivity = (BookActivity) getActivity();
    if(scannedISBN.compareTo(bookToExchange.getISBN()) != 0) {
      Toast.makeText(this.getContext(), "Verification Failed, this isn't the proper book!", Toast.LENGTH_LONG).show();
      return;
    }
    if(bookToExchange.getStatus() == BookStatus.ACCEPTED) {
      bookExchangeRequest(bookToExchange);
    }
    else if (bookToExchange.getStatus() == BookStatus.BORROWED && bookToExchange.getWorkflow() == CommonConstants.BookWorkflowStage.BORROWED){
      bookActivity.getBookReturnController().addReturnRequest(bookToExchange, new ReturnCallback() {
        @Override
        public void onSuccess(Book books) {
          bookViewModel.getReturnButton().setBackgroundResource(R.drawable.cancel_circle);
          bookViewModel.getBorrowedBorrowed();
        }
        @Override
        public void onFailure() {
        }
      });
    }
    else if(bookToExchange.getStatus() == BookStatus.BORROWED && bookToExchange.getWorkflow() == CommonConstants.BookWorkflowStage.PENDINGRETURN){
      bookReturnRequest(bookToExchange);
    }
  }
}