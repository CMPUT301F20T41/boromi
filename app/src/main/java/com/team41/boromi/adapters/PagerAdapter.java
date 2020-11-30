package com.team41.boromi.adapters;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.team41.boromi.FragmentFactory;
import java.util.ArrayList;

/**
 * This function manages the fragments used in the tabs. This class is used together with
 * Viewpager2
 */
public class PagerAdapter extends FragmentStateAdapter {

  private final ArrayList<Pair<Class<? extends Fragment>, Bundle>> fragmentClasses;
  public FragmentManager fragmentManager;

  /**
   * Initialize with a fragment manager and lifecycle object
   *
   * @param fragmentManager fragment manager
   * @param lifecycle       lifecycle
   */
  public PagerAdapter(@NonNull FragmentManager fragmentManager,
      @NonNull Lifecycle lifecycle) {
    super(fragmentManager, lifecycle);
    fragmentClasses = new ArrayList<>();
    this.fragmentManager = fragmentManager;
  }

  /**
   * This function is used to create the fragments by using a FragmentFactory
   */
  @NonNull
  @Override
  public Fragment createFragment(int position) {
    Pair<Class<? extends Fragment>, Bundle> p = fragmentClasses.get(position);
    try {
      return FragmentFactory.createFragment(p.first, p.second);
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
      Log.w("PagerAdapter", e);
      throw new RuntimeException("Failed to create fragment " + e.toString());
    }
  }

  /**
   * Returns the number of fragments
   *
   * @return number of fragments
   */
  @Override
  public int getItemCount() {
    return fragmentClasses.size();
  }

  /**
   * Adds a fragment
   *
   * @param f Fragment.Class with a Bundle to pass in any objects
   */
  public void addFragment(Pair<Class<? extends Fragment>, Bundle> f) {
    fragmentClasses.add(f);
  }
}
