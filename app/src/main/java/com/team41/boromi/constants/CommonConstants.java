package com.team41.boromi.constants;

public class CommonConstants {
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
