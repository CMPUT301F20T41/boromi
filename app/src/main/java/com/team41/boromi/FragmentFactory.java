package com.team41.boromi;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.team41.boromi.book.GenericListFragment;
import com.team41.boromi.book.MapFragment;

/**
 * This function creates Fragments based on their class name. For GenericListFragments, it will pass
 * in a bundle, for other fragments such as OwnedFragment, it will pass in nothing
 */
public class FragmentFactory {

  /**
   * Creates fragment
   * @param c Class of fragment to be created
   * @param bundle Bundle to be passed into the fragment
   * @return
   * @throws InstantiationException
   * @throws IllegalAccessException
   */
  public static Fragment createFragment(Class<? extends Fragment> c, Bundle bundle)
      throws InstantiationException, IllegalAccessException {
    switch (c.getSimpleName()) {
      case ("GenericListFragment"):
        return GenericListFragment.newInstance(bundle);
      case ("MapFragment"):
        return MapFragment.newInstance(bundle);
      default:
        return c.newInstance();

    }
  }
}
