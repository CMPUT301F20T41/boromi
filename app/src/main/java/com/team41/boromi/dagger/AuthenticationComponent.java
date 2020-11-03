package com.team41.boromi.dagger;

import com.team41.boromi.MainActivity;
import com.team41.boromi.TestActivity;
import com.team41.boromi.book.SettingsFragment;

import dagger.Subcomponent;

/**
 * A subcomponent that is destroyed after activity login/signup ends
 */
@ActivityScope
@Subcomponent
public interface AuthenticationComponent {

  void inject(TestActivity testActivity);

  void inject(MainActivity mainActivity);

  void inject(SettingsFragment settingsFragment);
}
