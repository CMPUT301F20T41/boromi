package com.team41.boromi.book;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener;
import com.google.android.material.tabs.TabLayout.Tab;
import com.team41.boromi.BookActivity;
import com.team41.boromi.BookViewModel;
import com.team41.boromi.R;
import com.team41.boromi.adapters.PagerAdapter;
import java.util.ArrayList;

/**
 * This Fragment manages the user Owned books tab. It will create 4 GenericListFragments for each of
 * its 4 tabs.
 */
public class OwnedFragment extends Fragment {

  private TabLayout tabLayout;
  private ViewPager2 viewPager2;
  private PagerAdapter pagerAdapter;
  private BookActivity bookActivity;
  private String parent = "Owned";
  private String TAG = "OwnedFrag";
  private BookViewModel bookViewModel;

  public OwnedFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @return A new instance of fragment BookFragment.
   */
  public static OwnedFragment newInstance() {
    OwnedFragment fragment = new OwnedFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  /**
   * Create the GenericListFragments that will populate each of the 4 tabs.
   *
   * @param savedInstanceState
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bookActivity = (BookActivity) getActivity();
    bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);
    pagerAdapter = new PagerAdapter(getChildFragmentManager(), getLifecycle());
    // add fragments to tabs
    Bundle bundle;
    bundle = bookActivity.setupBundle(R.layout.available, new ArrayList<>(),
        "These are all the books that you own that are available for other people to borrow",
        parent, "Available");
    pagerAdapter.addFragment(
        new Pair<Class<? extends Fragment>, Bundle>(GenericListFragment.class, bundle));

    bundle = bookActivity.setupBundle(R.layout.reqom, new ArrayList<>(),
        "These are all the books you own that other people have requested to borrow", parent,
        "Requested");
    pagerAdapter.addFragment(
        new Pair<Class<? extends Fragment>, Bundle>(GenericListFragment.class, bundle));

    bundle = bookActivity.setupBundle(R.layout.accepted, new ArrayList<>(),
        "These are all the book requests that you have accepted", parent, "Accepted");
    pagerAdapter.addFragment(
        new Pair<Class<? extends Fragment>, Bundle>(GenericListFragment.class, bundle));

    bundle = bookActivity.setupBundle(R.layout.lent, new ArrayList<>(),
        "These are all your books that are being borrowed by other people", parent, "Lent");
    pagerAdapter.addFragment(
        new Pair<Class<? extends Fragment>, Bundle>(GenericListFragment.class, bundle));
  }

  /**
   * Bind any listeners and values, also set up viewpager to change between subtabs
   *
   * @param inflater
   * @param container
   * @param savedInstanceState
   * @return
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_owned, container, false);
    tabLayout = (TabLayout) view.findViewById(R.id.tabs_sub_owned);
    viewPager2 = view.findViewById(R.id.view_pager_owned);
    TabItem availableTab = (TabItem) view.findViewById(R.id.tabs_sub_owned_available);
    TabItem requestsTab = (TabItem) view.findViewById(R.id.tabs_sub_owned_requests);
    TabItem acceptedTab = (TabItem) view.findViewById(R.id.tabs_sub_owned_accepted);
    TabItem lentTab = (TabItem) view.findViewById(R.id.tabs_sub_owned_lent);

    // Configure viewpager2 options and initialize page adapter
    viewPager2.setUserInputEnabled(false);
    viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
    viewPager2.setOffscreenPageLimit(tabLayout.getTabCount());
    viewPager2.setAdapter(pagerAdapter);
    /**
     * Changes the fragment depending on which subtab is clicked
     */
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