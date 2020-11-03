package com.team41.boromi;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.team41.boromi.book.GenericListFragment;

public class FragmentFactory {

  public static Fragment createFragment(Class<? extends Fragment> c, Bundle bundle)
      throws InstantiationException, IllegalAccessException {
    switch (c.getSimpleName()) {
      case ("GenericListFragment"):
        return GenericListFragment.newInstance(bundle);
      default:
        return c.newInstance();
      // TODO change to dagger

    }
  }
}
