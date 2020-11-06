package com.team41.boromi.utility;

/**
 * Utility class
 */
public abstract class Utility {

  /**
   * static method to check null/empty
   *
   * @param str
   * @return
   */
  public static boolean isNotNullOrEmpty(String str) {
    if (!str.trim().isEmpty() && str != null) {
      return true;
    }
    return false;
  }
}
