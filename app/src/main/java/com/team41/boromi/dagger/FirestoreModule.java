package com.team41.boromi.dagger;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * A Dagger module that provides the FirebaseFirestore
 */
@Module
public abstract class FirestoreModule {

  /**
   * Only provide one app scope level instance of FirebaseFirestore
   *
   * @return {@code FirebaseFirestore} instance of db
   */
  @Singleton
  @Provides
  static FirebaseFirestore provideFirestore() {
    return FirebaseFirestore.getInstance();
  }

  /**
   * provides auth from firebaseAuth
   *
   * @return {@code FirebaseAuth} instance of auth
   */
  @Singleton
  @Provides
  static FirebaseAuth provideAuth() {
    return FirebaseAuth.getInstance();
  }

}
