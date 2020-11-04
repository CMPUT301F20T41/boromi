package com.team41.boromi.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.team41.boromi.R;
import com.team41.boromi.controllers.BookController;
import com.team41.boromi.controllers.BookRequestController;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GenericListAdapter extends RecyclerView.Adapter<GenericListAdapter.ViewHolder> {

  BookController bookController;
  BookRequestController bookRequestController;
  private ArrayList<Book> books;
  private int resource;
  private ViewGroup parent;
  private Map<Book, List<BookRequest>> bookWithRequests;
  private ArrayList<SubListAdapter> subListAdapters;

  public GenericListAdapter(ArrayList<Book> books, int id, BookController bookController) {
    this.books = books;
    resource = id;
    this.bookController = bookController;
  }

  public GenericListAdapter(ArrayList<Book> books, Map<Book, List<BookRequest>> bookWithRequests,
      int id, BookController bookController, BookRequestController bookRequestController) {
    this.books = books;
    this.bookWithRequests = bookWithRequests;
    subListAdapters = new ArrayList<>();
    resource = id;
    this.bookController = bookController;
    this.bookRequestController = bookRequestController;
  }


  @NonNull
  @Override
  public GenericListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
    ViewHolder holder = new ViewHolder(view, resource);
    this.parent = parent;
    return holder;
  }

  @Override
  public void onBindViewHolder(@NonNull GenericListAdapter.ViewHolder holder, int position) {
    Book book = books.get(position);
    if (holder.author != null) {
      holder.author.setText(book.getAuthor());
    }
    if (holder.user != null) {
      // TODO more logic required depending on the page
      holder.user.setText(book.getBorrower());
    }
    if (holder.title != null) {
      holder.title.setText(book.getTitle());
    }
    if (holder.isbn != null) {
      holder.isbn.setText(book.getISBN());
    }
    if (holder.imageButton != null) {
      Bitmap b = bookController.decodeBookImage(book);
      if (b != null) {
        holder.imageButton.setImageBitmap(b);
      }
    }
    if (holder.reqom != null) {
      RecyclerView recyclerView = holder.view.findViewById(R.id.reqom_request_list);
      ArrayList<BookRequest> requesters;
      if (bookWithRequests == null) {
        requesters = new ArrayList<>();
      } else {
        requesters = (ArrayList<BookRequest>) bookWithRequests.get(book);
      }
      SubListAdapter subListAdapter = new SubListAdapter(requesters, book, bookRequestController);
      recyclerView.setLayoutManager(new LinearLayoutManager(parent.getContext()));
      recyclerView.setAdapter(subListAdapter);
      subListAdapters.add(subListAdapter);
    }
  }

  @Override
  public int getItemCount() {
    return books.size();
  }

  public void setBookWithRequests(
      Map<Book, List<BookRequest>> bookWithRequests) {
    this.bookWithRequests = bookWithRequests;
  }

  public ArrayList<SubListAdapter> getSubListAdapters() {
    return subListAdapters;
  }

  public void notifySubAdapters() {
    for (SubListAdapter subListAdapter : subListAdapters) {
      subListAdapter.setUsersRequested(
          (ArrayList<BookRequest>) bookWithRequests.get(subListAdapter.getBook()));
//      bookWithRequests.forEach((key, value) -> {});
      subListAdapter.notifyDataSetChanged();
    }
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView author;
    TextView isbn;
    TextView user;
    RecyclerView reqom;
    View view;
    ImageButton imageButton;

    public ViewHolder(@NonNull View itemView, int layout) {
      super(itemView);
      view = itemView;
      switch (layout) {
        case (R.layout.available):
          title = itemView.findViewById(R.id.available_title);
          author = itemView.findViewById(R.id.available_author);
          isbn = itemView.findViewById(R.id.available_isbn);
          imageButton = itemView.findViewById(R.id.available_book_image);
          reqom = null;
          break;
        case (R.layout.accepted):
          title = itemView.findViewById(R.id.accepted_title);
          author = itemView.findViewById(R.id.accepted_author);
          isbn = itemView.findViewById(R.id.accepted_isbn);
          user = itemView.findViewById(R.id.accepted_user);
          imageButton = itemView.findViewById(R.id.accepted_book_image);

          reqom = null;
          break;
        case (R.layout.borrowing):
          title = itemView.findViewById(R.id.borrowing_title);
          author = itemView.findViewById(R.id.borrowing_author);
          isbn = itemView.findViewById(R.id.borrowing_isbn);
          user = itemView.findViewById(R.id.borrowing_user);
          imageButton = itemView.findViewById(R.id.borrowing_book_image);
          reqom = null;
          break;
        case (R.layout.lent):
          title = itemView.findViewById(R.id.lent_title);
          author = itemView.findViewById(R.id.lent_author);
          isbn = itemView.findViewById(R.id.lent_isbn);
          user = itemView.findViewById(R.id.lent_user);
          imageButton = itemView.findViewById(R.id.lent_book_image);
          reqom = null;
          break;
        case (R.layout.reqbm):
          title = itemView.findViewById(R.id.reqbm_title);
          author = itemView.findViewById(R.id.reqbm_author);
          isbn = itemView.findViewById(R.id.reqbm_isbn);
          user = itemView.findViewById(R.id.reqbm_user);
          imageButton = itemView.findViewById(R.id.reqbm_book_image);
          reqom = null;
          break;
        case (R.layout.reqom):
          title = itemView.findViewById(R.id.reqom_title);
          author = itemView.findViewById(R.id.reqom_author);
          isbn = itemView.findViewById(R.id.reqom_isbn);
          reqom = itemView.findViewById(R.id.reqom_request_list);
          imageButton = itemView.findViewById(R.id.reqom_book_image);
          break;
        case (R.layout.searched):
          title = itemView.findViewById(R.id.searched_title);
          author = itemView.findViewById(R.id.searched_author);
          isbn = itemView.findViewById(R.id.searched_isbn);
          user = itemView.findViewById(R.id.searched_user);
          imageButton = itemView.findViewById(R.id.searched_bookImage);
          reqom = null;
      }

    }
  }
}
