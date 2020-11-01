package com.team41.boromi.dagger;

import com.team41.boromi.models.User;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A dagger module to describe modules associated with the app
 */
@Module
public class BoromiModule {

  public static User user;

  /**
   * Describes a way to initialize thread pool
   *
   * @return
   */
  @Provides
  @Singleton
  Executor provideExecutor() {
    int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    return new ThreadPoolExecutor(
        NUM_THREADS + 2,
        NUM_THREADS + 6,  // prevent tasks from being rejected
        1,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<Runnable>()
    );
  }

  @Provides
  @Singleton
  User provideUser() {
    if (user == null) {
      throw new RuntimeException("User was null when asked to inject.");
    }
    return user;
  }
}
