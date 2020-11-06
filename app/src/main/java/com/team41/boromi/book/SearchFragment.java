package com.team41.boromi.book;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.team41.boromi.BookActivity;
import com.team41.boromi.R;
import com.team41.boromi.adapters.GenericListAdapter;
import com.team41.boromi.callbacks.BookCallback;
import com.team41.boromi.models.Book;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * SearchFragment is used to manage the search tab. It will create one GenericListFragment to house
 * the data obtained from the search
 */
public class SearchFragment extends Fragment {

  EditText search;
  TextView Results;
  RecyclerView recyclerView;
  ProgressBar spinner;

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

  /**
   * Create the GenericListAdapter
   * @param savedInstanceState
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bookActivity = (BookActivity) getActivity();
    searchResults = new ArrayList<>();
    listAdapter = new GenericListAdapter(searchResults, R.layout.searched,
        (BookActivity) getActivity());
  }

  /**
   * Bind any listeners, values
   * @param inflater
   * @param container
   * @param savedInstanceState
   * @return
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    final Button searched_request = null;
    View view = inflater.inflate(R.layout.fragment_search, container, false);
    recyclerView = view.findViewById(R.id.search_recyclerView);
    search = view.findViewById(R.id.search_view);
    ImageButton search_butt = view.findViewById(R.id.search_butt);
    Results = view.findViewById(R.id.results);
    spinner = view.findViewById(R.id.search_loading);

    spinner.setVisibility(View.GONE);

    search.setOnKeyListener((v, keyCode, event) -> {
      if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode ==  KeyEvent.KEYCODE_ENTER)) {
        searchForBooks();
        return true;
      }
      return false;
    });

    recyclerView.setAdapter(listAdapter);
    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    search_butt.setOnClickListener(v -> searchForBooks());
    return view;
  }

  public void searchForBooks() {
    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

    String keywords = search.getText().toString();

    spinner.setVisibility(View.VISIBLE);
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
            spinner.setVisibility(View.GONE);
            if(searchResults.isEmpty() == false) {
              Results.setText("Results");
            }
            else{
              Results.setText("No results found, please try a different title");
            }
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
            spinner.setVisibility(View.GONE);
            listAdapter.notifyDataSetChanged();
          }
        });
      }
    });
  }
}