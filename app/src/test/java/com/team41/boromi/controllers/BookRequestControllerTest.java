package com.team41.boromi.controllers;

import com.team41.boromi.callbacks.BookRequestCallback;
import com.team41.boromi.constants.CommonConstants;
import com.team41.boromi.constants.CommonConstants.BookStatus;
import com.team41.boromi.dbs.BookDB;
import com.team41.boromi.dbs.BookRequestDB;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;
import com.team41.boromi.models.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static com.team41.boromi.constants.CommonConstants.DB_TIMEOUT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class BookRequestControllerTest {

  @Mock BookRequestDB bookRequestDB;
  @Mock User user;
  @Mock BookDB bookDB;
  Executor executor;
  BookRequestController dut;
  String bookId;
  String requestor;
  String owner;
  String requestId;
  BookRequest mockRequest;
  Book mockBook;

  @Before
  public void setup() throws NoSuchFieldException, IllegalAccessException {
    executor = Executors.newSingleThreadExecutor();
    dut = new BookRequestController(executor, user, bookRequestDB, bookDB);
    bookId = "BOOK_REQUEST_UTEST_BOOKID";
    requestor = "BOOK_REQUEST_UTEST_REQUESTING_UID";
    owner = "BOOK_REQUEST_UTEST_BOOK_OWNER";
    requestId = "BOOK_REQUEST_UTEST_RID";

    mockRequest = new BookRequest(requestor + "_NAME", requestor, bookId, owner);
    Field requestIdField = mockRequest.getClass().getDeclaredField("requestId");
    requestIdField.setAccessible(true);
    requestIdField.set(mockRequest, requestId);

    mockBook = new Book(owner, bookId);
  }

  @Test
  public void test_getRequestBooks() throws InterruptedException {
    List<BookRequest> sampleBookRequestsFromUser;
    sampleBookRequestsFromUser = new ArrayList<BookRequest>(){{
      add(mockRequest);
    }};

    Map<Book, List<BookRequest>> expectedBookWithRequestListMap = new HashMap<Book, List<BookRequest>>(){{put(mockBook, sampleBookRequestsFromUser);}};

    Mockito.when(user.getUUID()).thenReturn("BOOK_REQUEST_UTEST");
    Mockito.when(bookRequestDB.getBookRequests(Mockito.anyString())).thenReturn(sampleBookRequestsFromUser);
    Mockito.when(bookDB.getBooksWithRequestList(sampleBookRequestsFromUser)).thenReturn(expectedBookWithRequestListMap);

    final boolean[] finished = {false};
    dut.getRequestedBooks(new BookRequestCallback() {
      @Override
      public void onComplete(Map<Book, List<BookRequest>> bookWithRequests) {
          assertEquals(expectedBookWithRequestListMap, bookWithRequests);
          finished[0] = true;
      }
    });

    // allow the callback to be called
    Thread.sleep(DB_TIMEOUT);
    assertTrue(finished[0]); // assert the task was called back
  }

  @Test
  public void test_makeRequestOnBook() {
    dut.makeRequestOnBook(mockBook);
    assertEquals(mockBook.getStatus(), BookStatus.AVAILABLE);
  }

  @Test
  public void test_acceptBookRequest() throws InterruptedException {
    List<BookRequest> sampleAllBookRequests;
    sampleAllBookRequests = new ArrayList<BookRequest>(){{
      add(mockRequest);
    }};

    Mockito.when(bookDB.getBookById(Mockito.anyString())).thenReturn(mockBook);

    dut.acceptBookRequest(mockRequest, new BookRequestCallback() {
      @Override
      public void onComplete(Map<Book, List<BookRequest>> bookWithRequests) {

      }
    });

    Thread.sleep(DB_TIMEOUT); // allow complete

    assertEquals(CommonConstants.BookStatus.ACCEPTED, mockBook.getStatus());
    assertEquals(requestor, mockBook.getBorrower());
  }
}
