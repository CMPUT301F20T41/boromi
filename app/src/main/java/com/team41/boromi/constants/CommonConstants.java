package com.team41.boromi.constants;

/**
 * Common constants that the app uses
 */
public class CommonConstants {

  public static final long DB_TIMEOUT = 5000; // 5s
  public static final long GENERIC_TIMEOUT = 5000; // 5s
  public static final int REQUEST_IMAGE_CAPTURE = 1;

  public enum BookStatus {
    AVAILABLE,
    REQUESTED,
    ACCEPTED,
    BORROWED
  }

  public enum BookWorkflowStage {
    AVAILABLE,
    REQUESTED,
    ACCEPTED,
    PENDINGBORROW,
    BORROWED,
    PENDINGRETURN
  }

  public enum ExchangeStage {
    OWNER,
    BORROWER
  }

}
