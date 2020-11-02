package com.team41.boromi.callbacks;

import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;

import java.util.List;
import java.util.Map;

public interface BookRequestCallback {
  // map of book with arraylist of bookRequests on that book
  void onComplete(Map<Book, List<BookRequest>> bookWithRequests);
}
