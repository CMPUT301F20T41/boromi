package com.team41.boromi.callbacks;

/**
 * Interface for callbacks for auth
 */
public interface AuthNoResultCallback {

  void onSuccess();

  void onFailure(Exception exception);
}
