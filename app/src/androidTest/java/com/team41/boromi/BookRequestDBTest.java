package com.team41.boromi;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;
import com.team41.boromi.dbs.BookRequestDB;
import com.team41.boromi.models.BookRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static com.team41.boromi.constants.CommonConstants.DB_TIMEOUT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test for BookReturnsDB
 */
@RunWith(AndroidJUnit4.class)
public class BookRequestDBTest {

  BookRequestDB dut;
  List<BookRequest> addedBookRequests = new ArrayList<>();
  String bookId;
  String requestor;
  String owner;
  String requestId;
  BookRequest mockRequest;

  @Before
  public void setup() throws NoSuchFieldException, IllegalAccessException {
    dut = new BookRequestDB(FirebaseFirestore.getInstance());
    bookId = "BOOK_REQUEST_DB_UTEST_BOOKID";
    requestor = "BOOK_REQUEST_DB_UTEST_REQUESTING_UID";
    owner = "BOOK_REQUEST_DB_UTEST_BOOK_OWNER";
    requestId = "BOOK_REQUEST_DB_UTEST_RID";

    mockRequest = new BookRequest(requestor + "_NAME", requestor, bookId, owner);
    Field requestIdField = mockRequest.getClass().getDeclaredField("requestId");
    requestIdField.setAccessible(true);
    requestIdField.set(mockRequest, requestId);
  }

  /**
   * Tests if the requestor can get gotten and that push bookRequest works
   */
  @Test
  public void test_pushAndGetBookRequestsForRequestor() throws InterruptedException {
    dut.pushBookRequest(mockRequest);
    // wait for book to be created/complete
    Thread.sleep(DB_TIMEOUT);
    List<BookRequest> returnedRequested = dut.getBookRequests(requestor);

    assertEquals(1, returnedRequested.size());
    assertEquals(mockRequest.getRequestId(), returnedRequested.get(0).getRequestId());
  }


  @Test
  public void test_deleteRequestsForBook() throws NoSuchFieldException, IllegalAccessException, InterruptedException {
    dut.pushBookRequest(mockRequest);
    // add another request on the book
    Field requestIdField = mockRequest.getClass().getDeclaredField("requestId");
    requestIdField.setAccessible(true);
    requestIdField.set(mockRequest, "DIFFERENT_REQUEST_ID");
    dut.pushBookRequest(mockRequest);

    // wait for finish since push is async
    Thread.sleep(DB_TIMEOUT);

    // same requestor
    // verify 2 instances in db
    List<BookRequest> returnedRequested = dut.getBookRequests(requestor);
    assertEquals(2, returnedRequested.size());
    List<String> validRequestIds = new ArrayList<String>(){{add(requestId); add("DIFFERENT_REQUEST_ID");}};
    for(BookRequest br : returnedRequested)
      assertTrue(validRequestIds.contains(br.getRequestId()));
    // delete all requests for a book given a bood id
    dut.deleteRequestsForBook(bookId);
    Thread.sleep(DB_TIMEOUT); // give time to delete since async
    returnedRequested = dut.getBookRequests(requestor);

    assertEquals(0, returnedRequested.size());
  }


  @Test
  public void test_getRequestsForOwner() throws InterruptedException {
    dut.pushBookRequest(mockRequest);
    // wait for a thread
    Thread.sleep(DB_TIMEOUT);
    List<BookRequest> bookRequestList =  dut.getBookRequestsForOwner(owner);
    assertEquals(1, bookRequestList.size());
    assertEquals(mockRequest.getRequestId(), bookRequestList.get(0).getRequestId());

    bookRequestList = dut.getBookRequestsForOwner("NO_BOOKS_FOR_THIS_OWNER");
    assertEquals(0, bookRequestList.size());
  }


  @After
  public void tearDown() {
    dut.deleteBookRequest(requestId);
  }
}
