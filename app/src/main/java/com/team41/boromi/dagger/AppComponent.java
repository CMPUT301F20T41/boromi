package com.team41.boromi.dagger;

import com.team41.boromi.BookActivity;
import com.team41.boromi.TestRequestBookActivity;
import dagger.Component;
import javax.inject.Singleton;

/**
 * Hold a graph of all objects that will exist in memory for the lifecycle of app
 */
@Singleton
@Component(modules = {BoromiModule.class, FirestoreModule.class})
public interface AppComponent {

  AuthenticationComponent getAuthenticationComponent();

  void inject(BookActivity bookActivity);

  void inject(TestRequestBookActivity testActivityRequestBook);

}
