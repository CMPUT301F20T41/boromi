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
    if (str != null && !str.trim().isEmpty()) {
      return true;
    }
    return false;
  }
}
