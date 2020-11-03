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

public class PagerAdapter extends FragmentStateAdapter {

  private final ArrayList<Pair<Class<? extends Fragment>, Bundle>> fragmentClasses;
  public FragmentManager fragmentManager;

  public PagerAdapter(@NonNull FragmentManager fragmentManager,
      @NonNull Lifecycle lifecycle) {
    super(fragmentManager, lifecycle);
    fragmentClasses = new ArrayList<>();
    this.fragmentManager = fragmentManager;
  }

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

  @Override
  public int getItemCount() {
    return fragmentClasses.size();
  }

  public void addFragment(Pair<Class<? extends Fragment>, Bundle> f) {
    fragmentClasses.add(f);
  }
}
