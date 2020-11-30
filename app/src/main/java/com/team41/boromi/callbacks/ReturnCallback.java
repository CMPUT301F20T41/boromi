package com.team41.boromi.callbacks;

import com.team41.boromi.models.Book;

import java.util.ArrayList;

/**
 * Interface for callbacks when returning books
 */
public interface ReturnCallback {

  void onSuccess(Book book);

  void onFailure();

}
