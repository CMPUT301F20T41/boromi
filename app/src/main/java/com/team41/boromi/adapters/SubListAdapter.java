package com.team41.boromi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.maps.model.LatLng;
import com.team41.boromi.BookActivity;
import com.team41.boromi.BookViewModel;
import com.team41.boromi.R;
import com.team41.boromi.book.MapFragment;
import com.team41.boromi.callbacks.BookRequestCallback;
import com.team41.boromi.controllers.BookRequestController;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is recyclerview adapter that is used on the owner requests tab to show the user
 * request for each book
 */
public class SubListAdapter extends RecyclerView.Adapter<SubListAdapter.ViewHolder> {

  private final SubListAdapter _this = this;
  private ArrayList<BookRequest> usersRequested;
  private Book book;
  private BookRequestController bookRequestController;
  private GenericListAdapter parentAdapter;
  private BookActivity bookActivity;
  private int selectedBookIndex;
  private BookViewModel bookViewModel;

  public SubListAdapter(ArrayList<BookRequest> usersRequested, Book book,
      BookActivity bookActivity, GenericListAdapter parentAdapter) {
    this.book = book;
    this.bookActivity = bookActivity;
    this.bookRequestController = bookActivity.getBookRequestController();
    this.parentAdapter = parentAdapter;
    bookViewModel = parentAdapter.getGenericListFragment().getBookViewModel();
    if (usersRequested == null) {
      this.usersRequested = new ArrayList<>();
    } else {
      this.usersRequested = usersRequested;
    }
  }

  /**
   * Overrides onCreateViewHolder to set listeners and get layout elements and inflate the layout
   */
  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.req_list_entry, parent, false);

    ImageButton acceptButton = view.findViewById(R.id.accept);
    ImageButton declineButton = view.findViewById(R.id.cancel);

    SubListAdapter.ViewHolder holder = new ViewHolder(view);

    acceptButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        selectedBookIndex = holder.getLayoutPosition();
        bookViewModel.navExchangeLocation(book, usersRequested.get(selectedBookIndex));
      }
    });

    declineButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        int idx = holder.getLayoutPosition();
        bookRequestController.declineBookRequest((usersRequested.get(idx)));
        usersRequested.remove(usersRequested.get(idx));
        notifyDataSetChanged();
        if (usersRequested.isEmpty()) {
          bookViewModel.getOwnerRequests();
        }
      }
    });

    return holder;
  }

  /**
   * Update the model with the correct values
   */
  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.user.setText(usersRequested.get(position).getRequestorName());
  }

  /**
   * Returns number of elements (requests)
   *
   * @return number of items in the list
   */
  @Override
  public int getItemCount() {
    return usersRequested.size();
  }

  /**
   * Returns the book that is being requested on
   *
   * @return returns the book that is being requested on
   */
  public Book getBook() {
    return book;
  }

  /**
   * Sets the users that requested the book
   */
  public void setUsersRequested(ArrayList<BookRequest> usersRequested) {
    this.usersRequested = usersRequested;
  }

  /**
   * ViewHolder class to hold the model
   */
  public static class ViewHolder extends RecyclerView.ViewHolder {

    TextView user;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      user = itemView.findViewById(R.id.req_list_entry_user);
    }
  }
}
