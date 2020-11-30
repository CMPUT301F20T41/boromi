package com.team41.boromi;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Creates a ViewModel
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

  private BookActivity bookActivity;

  public ViewModelFactory(BookActivity activity) {
    this.bookActivity = activity;
  }

  @NonNull
  @Override
  public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
    return (T) new BookViewModel(bookActivity);
  }


}
