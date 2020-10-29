package com.team41.boromi.constants;

public class CommonConstants {

  public static final long DB_TIMEOUT = 5000; // 5s
  public static final long GENERIC_TIMEOUT = 5000; // 5s

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


}
