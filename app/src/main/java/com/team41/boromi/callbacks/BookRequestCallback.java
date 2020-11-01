package com.team41.boromi.callbacks;

import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;

import java.util.List;
import java.util.Map;

public interface BookRequestCallback {
  void onComplete(List<BookRequest> bookRequests, Map<String, Book> bookMap);
}
