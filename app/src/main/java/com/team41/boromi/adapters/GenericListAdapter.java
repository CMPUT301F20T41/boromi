package com.team41.boromi.adapters;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.team41.boromi.BookActivity;
import com.team41.boromi.R;
import com.team41.boromi.book.DisplayOtherUserFragment;
import com.team41.boromi.book.GenericListFragment;
import com.team41.boromi.book.ImageExpandFragment;
import com.team41.boromi.callbacks.ReturnCallback;
import com.team41.boromi.constants.CommonConstants;
import com.team41.boromi.controllers.BookController;
import com.team41.boromi.controllers.BookRequestController;
import com.team41.boromi.dbs.UserDB;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;
import com.team41.boromi.models.User;
import com.team41.boromi.utility.CustomClickListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * This class is an adapter class for a recycler view. This class is meant to be used together with
 * GenericListFragment to store the individual model cards for each tab
 */
public class GenericListAdapter extends RecyclerView.Adapter<GenericListAdapter.ViewHolder> {

  private static final String TAG = "Adapter Tag";
  BookController bookController;
  BookRequestController bookRequestController;
  UserDB userDB;
  private ArrayList<Book> books;
  private int resource;
  private ViewGroup parent;
  private Map<Book, List<BookRequest>> bookWithRequests;
  private ArrayList<SubListAdapter> subListAdapters;
  private GenericListFragment genericListFragment;
  private BookActivity bookActivity;
  FirebaseFirestore db;

  /**
   * GenericListAdapter constructor without genericListFragment
   * @param books list of books
   * @param id layout resource id
   * @param bookActivity instance of BookActivity
   */
  public GenericListAdapter(ArrayList<Book> books, int id, BookActivity bookActivity) {
    this.bookActivity = bookActivity;
    this.books = books;
    this.resource = id;
    this.bookController = bookActivity.getBookController();
    this.bookRequestController = bookActivity.getBookRequestController();
  }

  /**
   * GenericListAdapter constructor with genericListFragment
   * @param books list of book
   * @param bookWithRequests list of book requests
   * @param id layout resource id
   * @param bookActivity instance of BookActivity
   * @param fragment instance of GenericListFragment
   */
  public GenericListAdapter(ArrayList<Book> books, Map<Book, List<BookRequest>> bookWithRequests,
      int id, BookActivity bookActivity, GenericListFragment fragment) {
    this.bookActivity = bookActivity;
    this.books = books;
    this.bookWithRequests = bookWithRequests;
    this.subListAdapters = new ArrayList<>();
    this.resource = id;
    this.bookController = bookActivity.getBookController();
    this.bookRequestController = bookActivity.getBookRequestController();
    this.genericListFragment = fragment;
  }


  /**
   * This function overrides onCreateViewHolder and is mainly used to initialize onClickListeners
   */
  @NonNull
  @Override
  public GenericListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
    ViewHolder holder = new ViewHolder(view, resource);
    this.parent = parent;
    if (holder.request_button != null) {
      holder.request_button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          int pos = holder.getAdapterPosition();
          if (pos != RecyclerView.NO_POSITION) {
            Book requested = books.get(pos);
            bookRequestController.makeRequestOnBook(requested);
            books.remove(requested);
            if (resource == R.layout.searched) {
              bookActivity.getBookViewModel().getBorrowedRequested();
            } else {
              genericListFragment.getBookViewModel().getBorrowedRequested();
            }
            notifyDataSetChanged();
          }
        }
      });
    }
    if (holder.withdrawButton != null) {
      holder.withdrawButton.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View view) {
          int pos = holder.getAdapterPosition();
          if (pos != RecyclerView.NO_POSITION) {
            Book requested = books.get(pos);
            bookRequestController.declineBookRequest(bookWithRequests.get(requested).get(0));
            books.remove(requested);
            bookWithRequests.remove(requested);
            notifyDataSetChanged();
          }
        }
      });
    }
    if (holder.user != null) {
      holder.user.setOnClickListener(view1 -> {
        String usernameClicked = books.get(holder.getAdapterPosition()).getOwner();
        db = FirebaseFirestore.getInstance();
        UserDB userDB = new UserDB(db);
        new Thread(() -> {
          AppCompatActivity activity = (AppCompatActivity) view1.getContext();
          User userToDisplay = userDB.getUserByUUID(usernameClicked);
          DisplayOtherUserFragment displayOtherUserFragment = DisplayOtherUserFragment.newInstance(userToDisplay);
          displayOtherUserFragment.show(activity.getSupportFragmentManager(), "displayUsers");
        }).start();
      });
    }
    return holder;
  }

  /**
   * This function is used to update viewmodel with the correct information
   */
  @Override
  public void onBindViewHolder(@NonNull GenericListAdapter.ViewHolder holder, int position) {
    Book book = books.get(position);
    if (holder.author != null) {
      holder.author.setText(book.getAuthor());
    }
    if (holder.user != null) {
      if (resource == R.layout.searched || resource == R.layout.accepted_request) {
        holder.user.setText(book.getOwnerName());
      } else {
        holder.user.setText(book.getBorrowerName());
      }
    }
    if (holder.title != null) {
      holder.title.setText(book.getTitle());
    }
    if (holder.isbn != null) {
      String isbnText = "ISBN: " + book.getISBN();
      holder.isbn.setText(isbnText);
    }
    if (holder.imageButton != null) {
      Bitmap b = bookController.decodeBookImage(book);

      if (b != null) {
        holder.imageButton.setScaleType(ImageView.ScaleType.FIT_XY);
        holder.imageButton.setImageBitmap(b);
      } else {
        holder.imageButton.setScaleType(ImageView.ScaleType.CENTER);
        holder.imageButton.setImageResource(R.drawable.book_icon);
      }
      holder.imageButton.setOnClickListener( new OnClickListener(){
        @Override
        public void onClick(View v) {
          if (book.getImg64() != null) {
            FragmentManager fragmentManager = (genericListFragment.getActivity())
                    .getSupportFragmentManager();
            ImageExpandFragment showImageExpandFragment = ImageExpandFragment.newInstance(book);
            showImageExpandFragment.show(fragmentManager, "image_expanded");
          }
        }
      });
    }
    if (holder.reqom != null) {
      RecyclerView recyclerView = holder.view.findViewById(R.id.reqom_request_list);
      ArrayList<BookRequest> requesters;
      if (bookWithRequests == null) {
        requesters = new ArrayList<>();
      } else {
        requesters = (ArrayList<BookRequest>) bookWithRequests.get(book);
      }
      SubListAdapter subListAdapter = new SubListAdapter(requesters, book, bookActivity,
          this);
      recyclerView.setLayoutManager(new LinearLayoutManager(parent.getContext()));
      recyclerView.setAdapter(subListAdapter);
      subListAdapters.add(subListAdapter);
    }
    if (holder.rightButton != null) {
      if (book.getWorkflow() == CommonConstants.BookWorkflowStage.PENDINGRETURN || book.getWorkflow() == CommonConstants.BookWorkflowStage.PENDINGBORROW){
        holder.rightButton.setImageResource(R.drawable.more_horizontal_notified);
      }
      holder.rightButton
          .setOnClickListener(new CustomClickListener(book, bookActivity, genericListFragment));
    }
    if (holder.returnButton != null){
      if (book.getWorkflow() == CommonConstants.BookWorkflowStage.PENDINGRETURN){
        holder.returnButton.setBackgroundResource(R.drawable.cancel_circle);
      }
      else if (book.getWorkflow() == CommonConstants.BookWorkflowStage.BORROWED){
        holder.returnButton.setBackgroundResource(R.drawable.return_circle); 
      }
      holder.returnButton.setOnClickListener(new OnClickListener(){
        @Override
        public void onClick(View v) {
          if (book.getWorkflow() == CommonConstants.BookWorkflowStage.BORROWED) {
            genericListFragment.verifyBarcode(book);
          }
          else if (book.getWorkflow() == CommonConstants.BookWorkflowStage.PENDINGRETURN) {
              bookActivity.getBookReturnController().cancelReturnRequest(book.getBookId(), new ReturnCallback() {
                @Override
                public void onSuccess(Book books) {
                  holder.returnButton.setBackgroundResource(R.drawable.return_circle);
                }

                @Override
                public void onFailure() {
                }
              });
          }
        }
      });
    }
    if (holder.status != null) {
      if (book.getStatus() == CommonConstants.BookStatus.AVAILABLE) {
        holder.status.setText("AVAILABLE");
      } else if (book.getStatus() == CommonConstants.BookStatus.REQUESTED) {
        holder.status.setText("REQUESTED");
      }
    }
  }

  /**
   * Returns the number of elements in the list
   */
  @Override
  public int getItemCount() {
    return books.size();
  }

  /**
   * Sets the books that have requests. Used for request tabs
   *
   * @param bookWithRequests Map of key: Book, value: List<BookRequest>
   */
  public void setBookWithRequests(
      Map<Book, List<BookRequest>> bookWithRequests) {
    this.bookWithRequests = bookWithRequests;
  }

  /**
   * This function notifies subAdapters used in the Owner book requests tab that the data has been
   * changed
   */
  public void notifySubAdapters() {
    for (SubListAdapter subListAdapter : subListAdapters) {
      subListAdapter.setUsersRequested(
          (ArrayList<BookRequest>) bookWithRequests.get(subListAdapter.getBook()));
      subListAdapter.notifyDataSetChanged();
    }
  }

  /**
   * This function deletes a book request when a request has been accepted
   *
   * @param book           Book to be deleted
   * @param subListAdapter Adapter for sub lists
   */
  public void deleteBookRequest(Book book, SubListAdapter subListAdapter) {
    this.books.remove(book);
    this.bookWithRequests.remove(book);
    this.subListAdapters.remove(subListAdapter);
    ((BookActivity) genericListFragment.getActivity()).runOnUiThread(new Runnable() {
      @Override
      public void run() {
        notifyDataSetChanged();
        genericListFragment.getBookViewModel().getOwnerAccepted();
      }
    });
  }

  public GenericListFragment getGenericListFragment() {
    return genericListFragment;
  }


  /**
   * This class holds the models used in the recyclerView
   */
  public static class ViewHolder extends RecyclerView.ViewHolder {


    TextView title;
    TextView author;
    TextView isbn;
    TextView user;
    RecyclerView reqom;
    View view;
    ImageButton imageButton;
    ImageButton rightButton;
    public static ImageButton returnButton;
    Button request_button;
    TextView withdrawButton;
    TextView status;

    /**
     * This Constructor finds the layout elements depending on which layout is being used
     */
    public ViewHolder(@NonNull View itemView, int layout) {
      super(itemView);
      view = itemView;

      switch (layout) {
        case (R.layout.available):
          title = itemView.findViewById(R.id.available_title);
          author = itemView.findViewById(R.id.available_author);
          isbn = itemView.findViewById(R.id.available_isbn);
          imageButton = itemView.findViewById(R.id.available_book_image);
          rightButton = itemView.findViewById(R.id.right_button);
          returnButton = null;
          request_button = null;
          status = null;
          reqom = null;
          user = null;
          break;
        case (R.layout.accepted):
          title = itemView.findViewById(R.id.accepted_title);
          author = itemView.findViewById(R.id.accepted_author);
          isbn = itemView.findViewById(R.id.accepted_isbn);
          user = itemView.findViewById(R.id.accepted_user);
          imageButton = itemView.findViewById(R.id.accepted_book_image);
          rightButton = itemView.findViewById(R.id.right_button);
          returnButton = null;
          request_button = null;
          status = null;
          reqom = null;
          break;
        case (R.layout.accepted_request):
          title = itemView.findViewById(R.id.accepted_request_title);
          author = itemView.findViewById(R.id.accepted_request_author);
          isbn = itemView.findViewById(R.id.accepted_request_isbn);
          user = itemView.findViewById(R.id.accepted_request_user);
          imageButton = itemView.findViewById(R.id.accepted_request_book_image);
          rightButton = itemView.findViewById(R.id.right_button);
          returnButton = null;
          request_button = null;
          status = null;
          reqom = null;
          break;
        case (R.layout.borrowing):
          title = itemView.findViewById(R.id.borrowing_title);
          author = itemView.findViewById(R.id.borrowing_author);
          isbn = itemView.findViewById(R.id.borrowing_isbn);
          user = itemView.findViewById(R.id.borrowing_user);
          imageButton = itemView.findViewById(R.id.borrowing_book_image);
          rightButton = null;
          request_button = null;
          returnButton = itemView.findViewById(R.id.return_button);
          status = null;
          reqom = null;
          break;
        case (R.layout.lent):
          title = itemView.findViewById(R.id.lent_title);
          author = itemView.findViewById(R.id.lent_author);
          isbn = itemView.findViewById(R.id.lent_isbn);
          user = itemView.findViewById(R.id.lent_user);
          imageButton = itemView.findViewById(R.id.lent_book_image);
          rightButton = itemView.findViewById(R.id.right_button);
          returnButton = null;
          request_button = null;
          status = null;
          reqom = null;
          break;
        case (R.layout.reqbm):
          title = itemView.findViewById(R.id.reqbm_title);
          author = itemView.findViewById(R.id.reqbm_author);
          isbn = itemView.findViewById(R.id.reqbm_isbn);
          user = itemView.findViewById(R.id.reqbm_user);
          imageButton = itemView.findViewById(R.id.reqbm_book_image);
          rightButton = null;
          returnButton = null;
//          withdrawButton = itemView.findViewById(R.id.reqbm_withdraw);
          request_button = null;
          status = itemView.findViewById(R.id.reqbm_status);
          reqom = null;
          break;
        case (R.layout.reqom):
          title = itemView.findViewById(R.id.reqom_title);
          author = itemView.findViewById(R.id.reqom_author);
          isbn = itemView.findViewById(R.id.reqom_isbn);
          reqom = itemView.findViewById(R.id.reqom_request_list);
          imageButton = itemView.findViewById(R.id.reqom_book_image);
          rightButton = null;
          returnButton = null;
          request_button = null;
          status = null;
          break;
        case (R.layout.searched):
          title = itemView.findViewById(R.id.searched_title);
          author = itemView.findViewById(R.id.searched_author);
          isbn = itemView.findViewById(R.id.searched_isbn);
          user = itemView.findViewById(R.id.searched_user);
          imageButton = itemView.findViewById(R.id.searched_bookImage);
          rightButton = null;
          returnButton = null;
          request_button = itemView.findViewById(R.id.searched_request);
          status = itemView.findViewById(R.id.searched_status);
          reqom = null;
          break;
      }

      // Makes the circular
      imageButton.setClipToOutline(true);
    }

  }
}
