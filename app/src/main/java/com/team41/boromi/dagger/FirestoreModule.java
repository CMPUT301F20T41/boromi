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
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    // Leave this commented out to use the production db
    // Uncomment it out to use an emulated db
    // firestore.useEmulator("10.0.2.2", 8080);
    return firestore;
  }

  /**
   * provides auth from firebaseAuth
   *
   * @return {@code FirebaseAuth} instance of auth
   */
  @Singleton
  @Provides
  static FirebaseAuth provideAuth() {
    FirebaseAuth auth = FirebaseAuth.getInstance();

    // Leave this commented out to use the production auth server
    // Uncomment it out to use an emulated auth server
    // auth.useEmulator("10.0.2.2", 9099);

    return auth;
  }
}
