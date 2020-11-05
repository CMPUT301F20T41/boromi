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
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass. Use the {@link GenericListFragment#newInstance} factory
 * method to create an instance of this fragment.
 */
public class GenericListFragment extends Fragment {

  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String LAYOUT_PARAM1 = "LayoutID";
  private static final String DATA_PARAM2 = "Data";
  private static final String MSG_PARAM3 = "Msg";
  private static final String PARENT_PARAM4 = "Parent";
  private static final String TAG_PARAM5 = "TAG";
  private static final String TAG = "GenericListFrag";
  private final GenericListFragment _this = this;
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

  public String getParent() {
    return parent;
  }

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
}