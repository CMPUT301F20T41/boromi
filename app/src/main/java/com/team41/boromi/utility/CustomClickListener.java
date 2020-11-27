package com.team41.boromi.utility;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import com.team41.boromi.BookActivity;
import com.team41.boromi.R;
import com.team41.boromi.book.EditBookFragment;
import com.team41.boromi.book.GenericListFragment;
import com.team41.boromi.callbacks.BookCallback;
import com.team41.boromi.constants.CommonConstants;
import com.team41.boromi.controllers.BookController;
import com.team41.boromi.models.Book;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * This class is a listener for the more (...) button that appears on the cards.
 */
public class CustomClickListener implements View.OnClickListener,
    PopupMenu.OnMenuItemClickListener {

  Book book;
  BookController bookController;
  BookActivity bookActivity;
  GenericListFragment genericListFragment;

  public CustomClickListener(Book book, BookActivity bookActivity,
      GenericListFragment genericListFragment) {
    this.book = book;
    this.bookActivity = bookActivity;
    this.bookController = bookActivity.getBookController();
    this.genericListFragment = genericListFragment;
  }

  /**
   * Expand the more button and show the dropdown menu
   *
   * @param view
   */
  @Override
  public void onClick(View view) {
    PopupMenu popup = new PopupMenu(view.getContext(), view);
    try {
      MenuInflater inflater = popup.getMenuInflater();
      popup.setOnMenuItemClickListener(this::onMenuItemClick);
      inflater.inflate(R.menu.book_edit_delete_menu, popup.getMenu());
      if (genericListFragment.getParent().equals("Owned")) {
        MenuItem deleteBtn = popup.getMenu().findItem(R.id.delete_book);
        deleteBtn.setVisible(true);
        if (genericListFragment.tag.equals("Available")) {
          MenuItem editBtn = popup.getMenu().findItem(R.id.edit_book);
          editBtn.setVisible(true);
        } else if (genericListFragment.tag.equals("Accepted")) {
          MenuItem exchange = popup.getMenu().findItem(R.id.exchange_book);
          exchange.setVisible(true);
          exchange.setTitle("Give");
        } else if (genericListFragment.tag.equals("Lent") && book.getWorkflow() == CommonConstants.BookWorkflowStage.PENDINGRETURN) {
          MenuItem receive = popup.getMenu().findItem(R.id.regain_book);
          receive.setVisible(true);
          receive.setTitle("Regain");
        }
      } else if (genericListFragment.getParent().equals("Borrowed")) {
        if (genericListFragment.tag.equals("Accepted")) {
          MenuItem exchange = popup.getMenu().findItem(R.id.exchange_book);
          exchange.setVisible(true);
          exchange.setTitle("Receive");
        }
      } else {
        popup.getMenu().findItem(R.id.delete_book).setVisible(false);
        popup.getMenu().findItem(R.id.exchange_book).setVisible(false);
        popup.getMenu().findItem(R.id.edit_book).setVisible(false);
        popup.getMenu().findItem(R.id.regain_book).setVisible(false);
      }
      Method method = popup.getMenu().getClass()
          .getDeclaredMethod("setOptionalIconsVisible", boolean.class);
      method.setAccessible(true);
      popup.setOnMenuItemClickListener(this::onMenuItemClick);
      method.invoke(popup.getMenu(), true);

      popup.setGravity(Gravity.END);

      popup.show();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * onClick functionality for individual menu items
   *
   * @param menuItem
   * @return
   */
  @Override
  public boolean onMenuItemClick(MenuItem menuItem) {
    switch (menuItem.getItemId()) {
      case R.id.edit_book:
        Log.d("edit!", book.getTitle());
        FragmentManager fragmentManager = (genericListFragment.getActivity())
            .getSupportFragmentManager();
        EditBookFragment showEditBookFragment = EditBookFragment.newInstance(book);
        showEditBookFragment.show(fragmentManager, "editBook");

        return true;
      case R.id.delete_book:
        Log.d("delete!", book.getTitle());
        bookController.deleteBook(book.getBookId(), new BookCallback() {
          @Override
          public void onSuccess(ArrayList<Book> books) {
            genericListFragment.getActivity().runOnUiThread(() -> {
              if (genericListFragment.getParent().equals("Owned")) {
                // TODO temp solution just update both
                genericListFragment.getBookViewModel().getOwnerAvailable();
                genericListFragment.getBookViewModel().getOwnerAccepted();
//                ((BookActivity) genericListFragment.getActivity())
//                    .updateFragment("OwnedFragment", genericListFragment.tag);
              } else if (genericListFragment.getParent().equals("Borrowed")) {
//                ((BookActivity) genericListFragment.getActivity())
//                    .updateFragment("BorrowedFragment", genericListFragment.tag);
              }
            });
          }

          @Override
          public void onFailure(Exception e) {
          }
        });
        return true;
      case R.id.regain_book:
        genericListFragment.verifyBarcode(book);
        //genericListFragment.bookReturnRequest(book);

        return true;
      case R.id.exchange_book:
        genericListFragment.verifyBarcode(book);
//          genericListFragment.bookExchangeRequest(book);
        return true;

    }
    return false;
  }

}
