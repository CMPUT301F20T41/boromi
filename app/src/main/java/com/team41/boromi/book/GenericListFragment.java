package com.team41.boromi.book;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.team41.boromi.BookActivity;
import com.team41.boromi.R;
import com.team41.boromi.adapters.GenericListAdapter;
import com.team41.boromi.callbacks.BookCallback;
import com.team41.boromi.constants.CommonConstants.BookStatus;
import com.team41.boromi.constants.CommonConstants.ExchangeStage;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
  RecyclerView recyclerView;
  GenericListAdapter listAdapter;
  private ArrayList<Book> bookDataList = new ArrayList<>();
  private int layoutID;
  private String tempMsg;
  private String parent;
  private Map<Book, List<BookRequest>> bookWithRequests;

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
   * @param savedInstanceState
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
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
   * @return String tag of the parent fragment
   */
  public String getParent() {
    return parent;
  }

  /**
   * Bind any listeners and initialize any values. This will also set up the GenericListAdapter
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
    if (parent.equals("Owned")) {
      ((OwnedFragment) getParentFragment()).getData(tag, this);
    } else if (parent.equals("Borrowed")) {
      ((BorrowedFragment) getParentFragment()).getData(tag, this);
    }
    return view;
  }

  /**
   * This function will be called from the parent fragment to update the data of this
   * GenericListFragment by updating the adapter list
   * @param books books to update
   */
  public void updateData(ArrayList<Book> books) {
    this.bookDataList.clear();
    this.bookDataList.addAll(books);
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        listAdapter.notifyDataSetChanged();
      }
    });
  }

  /**
   * Polymorphism to accept a map. Request tabs use a map of Book and BookRequest.
   * @param bookWithRequests Map of Book and List of BookRequests
   */
  public void updateData(Map<Book, List<BookRequest>> bookWithRequests) {
    this.bookDataList.clear();
    this.bookDataList.addAll(bookWithRequests.keySet());
    this.bookWithRequests = bookWithRequests;
    listAdapter.setBookWithRequests(bookWithRequests);
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        listAdapter.notifyDataSetChanged();
        listAdapter.notifySubAdapters();
      }
    });
  }

  /**
   * Used in the Accepted tabs to process the exchanging of the book. It will also refresh the
   * lent, accepted, borrowed, requested subtabs
   * @param book Book to be exchanged
   */
  public void bookExchangeRequest(Book book) {
    BookActivity bookActivity = (BookActivity) getActivity();
    bookActivity.getBookController().updateBookExchange(bookActivity.getUser().getUUID(), book,
        new BookCallback() {
          @Override
          public void onSuccess(ArrayList<Book> books) {
            if (books.get(0).getStatus() == BookStatus.BORROWED) {
              bookActivity.updateFragment("OwnedFragment", "Lent");
              bookActivity.updateFragment("OwnedFragment", "Accepted");
              bookActivity.updateFragment("BorrowedFragment", "Borrowed");
              bookActivity.updateFragment("BorrowedFragment", "Requested");

            }
          }

          @Override
          public void onFailure(Exception e) {

          }
        });
  }
}