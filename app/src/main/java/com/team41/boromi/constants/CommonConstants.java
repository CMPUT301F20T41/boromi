package com.team41.boromi.constants;

/**
 * Common constants that the app uses
 */
public class CommonConstants {

  public static final long DB_TIMEOUT = 5000; // 5s
  public static final long GENERIC_TIMEOUT = 5000; // 5s
  public static final int REQUEST_IMAGE_CAPTURE = 1;

  /**
   * One of Available, Requested, Accepted, Borrowed
   */
  public enum BookStatus {
    AVAILABLE,
    REQUESTED,
    ACCEPTED,
    BORROWED
  }

  /**
   * One of Available, Requested, Accepted, PendingBorrow, Borrowed, PendingReturn
   */
  public enum BookWorkflowStage {
    AVAILABLE,
    REQUESTED,
    ACCEPTED,
    PENDINGBORROW,
    BORROWED,
    PENDINGRETURN
  }

  /**
   * Keep track of which user scanned during book exchange
   */
  public enum ExchangeStage {
    OWNER,
    BORROWER
  }

}
