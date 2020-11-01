package com.team41.boromi.dagger;

import com.team41.boromi.TestActivity;

import dagger.Subcomponent;

/**
 * A subcomponent that is destroyed after activity login/signup ends
 */
@ActivityScope
@Subcomponent
public interface AuthenticationComponent {

  void inject(TestActivity testActivity);
}
