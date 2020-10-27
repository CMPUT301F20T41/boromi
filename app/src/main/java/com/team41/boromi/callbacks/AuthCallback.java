package com.team41.boromi.callbacks;

import com.google.firebase.auth.AuthResult;

/**
 * An interface defining callback methods for auth
 */
public interface AuthCallback {

  void onSuccess(AuthResult authResult);

  void onFailure(Exception exception);
}
