package com.team41.boromi.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.team41.boromi.R;
import com.team41.boromi.callbacks.BookRequestCallback;
import com.team41.boromi.controllers.BookRequestController;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SubListAdapter extends RecyclerView.Adapter<SubListAdapter.ViewHolder> {

  private final SubListAdapter _this = this;
  private ArrayList<BookRequest> usersRequested;
  private Book book;
  private BookRequestController bookRequestController;
  private GenericListAdapter parentAdapter;

  public SubListAdapter(ArrayList<BookRequest> usersRequested, Book book,
      BookRequestController bookRequestController, GenericListAdapter parentAdapter) {
    this.book = book;
    this.bookRequestController = bookRequestController;
    this.parentAdapter = parentAdapter;

    if (usersRequested == null) {
      this.usersRequested = new ArrayList<>();
    } else {
      this.usersRequested = usersRequested;
    }
  }

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
        int idx = holder.getLayoutPosition();
        bookRequestController.acceptBookRequest(usersRequested.get(idx), new BookRequestCallback() {
          @Override
          public void onComplete(Map<Book, List<BookRequest>> bookWithRequests) {
            usersRequested = new ArrayList<>();
            parentAdapter.deleteBookRequest(book, _this);
          }
        });
      }
    });

    declineButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        int idx = holder.getLayoutPosition();
        bookRequestController.declineBookRequest((usersRequested.get(idx)));
        usersRequested.remove(usersRequested.get(idx));
        notifyDataSetChanged();
      }
    });

    return holder;
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.user.setText(usersRequested.get(position).getRequestorName());
  }

  @Override
  public int getItemCount() {
    return usersRequested.size();
  }

  public Book getBook() {
    return book;
  }

  public ArrayList<BookRequest> getUsersRequested() {
    return this.usersRequested;
  }

  public void setUsersRequested(ArrayList<BookRequest> usersRequested) {
    this.usersRequested = usersRequested;
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    TextView user;

    public ViewHolder(@NonNull View itemView) {
      super(itemView);
      user = itemView.findViewById(R.id.req_list_entry_user);
    }
  }
}
