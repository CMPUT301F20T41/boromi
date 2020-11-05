package com.team41.boromi.book;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import com.team41.boromi.BookActivity;
import com.team41.boromi.R;
import com.team41.boromi.adapters.GenericListAdapter;
import com.team41.boromi.callbacks.BookCallback;
import com.team41.boromi.models.Book;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass. Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

  RecyclerView recyclerView;
  GenericListAdapter listAdapter;
  ArrayList<Book> searchResults;
  private BookActivity bookActivity;
  private GenericListFragment genericListFragment;

  public SearchFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @return A new instance of fragment SearchFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static SearchFragment newInstance() {
    SearchFragment fragment = new SearchFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bookActivity = (BookActivity) getActivity();
    searchResults = new ArrayList<>();
    listAdapter = new GenericListAdapter(searchResults, R.layout.searched,
        (BookActivity) getActivity());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    final Button searched_request = null;
    View view = inflater.inflate(R.layout.fragment_search, container, false);
    recyclerView = view.findViewById(R.id.search_recyclerView);
    final TextInputEditText search = view.findViewById(R.id.search_view);
    ImageButton search_butt = view.findViewById(R.id.search_butt);
    TextView Results = view.findViewById(R.id.results);

    recyclerView.setAdapter(listAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    search_butt.setOnClickListener(v -> {
      String keywords = search.getText().toString();
      bookActivity.getBookController().findBooks(keywords, new BookCallback() {
        @Override
        public void onSuccess(ArrayList<Book> books) {
          ArrayList<Book> filtered = (ArrayList<Book>) books.stream().filter(
              book -> !book.getOwner().equals(((BookActivity) getActivity()).getUser().getUUID()))
              .collect(Collectors.toList());
          bookActivity.getCollections().put("Searched", filtered);

          searchResults.clear();
          searchResults.addAll(filtered);
          getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              listAdapter.notifyDataSetChanged();
              Results.setText("Results");
            }
          });
        }

        @Override
        public void onFailure(Exception e) {
          searchResults.clear();
          getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
              Results.setText("No results found, please try a different title");
              listAdapter.notifyDataSetChanged();
            }
          });
        }
      });
    });
    return view;
  }
}