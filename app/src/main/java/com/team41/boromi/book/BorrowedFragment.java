package com.team41.boromi.book;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;
import com.team41.boromi.R;
import com.team41.boromi.adapters.PagerAdapter;

/**
 * A simple {@link Fragment} subclass. Use the {@link BorrowedFragment#newInstance} factory method
 * to create an instance of this fragment.
 */
public class BorrowedFragment extends Fragment {

  private TabLayout tabLayout;
  private ViewPager2 viewPager2;
  private PagerAdapter pagerAdapter;

  public BorrowedFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @return A new instance of fragment BorrowedFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static BorrowedFragment newInstance() {
    BorrowedFragment fragment = new BorrowedFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_borrowed, container, false);
    tabLayout = (TabLayout) view.findViewById(R.id.tabs_sub_borrowed);
    viewPager2 = view.findViewById(R.id.view_pager_borrowed);
    TabItem borrowedTab = (TabItem) view.findViewById(R.id.tabs_sub_borrowed_borrowed);
    TabItem requestedTab = (TabItem) view.findViewById(R.id.tabs_sub_borrowed_requested);
    TabItem acceptedTab = (TabItem) view.findViewById(R.id.tabs_sub_borrowed_accepted);

    // add fragments to tabs
    pagerAdapter = new PagerAdapter(getChildFragmentManager(), getLifecycle());
    Bundle bundle = new Bundle();
    bundle.putString("msg", "Borrowed");
    pagerAdapter.addFragment(
        new Pair<Class<? extends Fragment>, Bundle>(GenericListFragment.class, bundle));
    bundle = new Bundle();
    bundle.putString("msg", "Requested");
    pagerAdapter.addFragment(
        new Pair<Class<? extends Fragment>, Bundle>(GenericListFragment.class, bundle));
    bundle = new Bundle();
    bundle.putString("msg", "Accepted");
    pagerAdapter.addFragment(
        new Pair<Class<? extends Fragment>, Bundle>(GenericListFragment.class, bundle));

    // Configure viewpager2 options and initialize page adapter
    viewPager2.setUserInputEnabled(false);
    viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
    viewPager2.setOffscreenPageLimit(tabLayout.getTabCount());
    viewPager2.setAdapter(pagerAdapter);

    tabLayout.addOnTabSelectedListener(new OnTabSelectedListener() {
      @Override
      public void onTabSelected(Tab tab) {
        viewPager2.setCurrentItem(tab.getPosition());
      }

      @Override
      public void onTabUnselected(Tab tab) {

      }

      @Override
      public void onTabReselected(Tab tab) {

      }
    });
    return view;
  }
}