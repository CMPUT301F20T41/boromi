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
import com.team41.boromi.models.Book;
import java.util.ArrayList;

/**
 * BorrowedFragment manages the borrower tab. It creates 3 GenericListFragments to populate the sub
 * tabs.
 */
public class BorrowedFragment extends Fragment {

  private TabLayout tabLayout;
  private ViewPager2 viewPager2;
  private PagerAdapter pagerAdapter;
  private BookActivity bookActivity;
  private BookViewModel bookViewModel;
  private String parent = "Borrowed";

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

  /**
   * onCreated used to create the GenericListFragments that will correspond to each subtab.
   *
   * @param savedInstanceState
   */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bookActivity = (BookActivity) getActivity();
    bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);
    // add fragments to tabs
    ArrayList<Book> bookDataList = new ArrayList<>();
    pagerAdapter = new PagerAdapter(getChildFragmentManager(), getLifecycle());

    Bundle bundle;
    bundle = bookActivity.setupBundle(R.layout.borrowing, new ArrayList<>(),
        "These are all the books that you have borrowed currently", parent, "Borrowed");
    pagerAdapter.addFragment(
        new Pair<Class<? extends Fragment>, Bundle>(GenericListFragment.class, bundle));

    bundle = bookActivity.setupBundle(R.layout.reqbm, new ArrayList<>(),
        "These are all the books that you have requested to borrow", parent, "Requested");
    pagerAdapter.addFragment(
        new Pair<Class<? extends Fragment>, Bundle>(GenericListFragment.class, bundle));

    bundle = bookActivity.setupBundle(R.layout.accepted_request, new ArrayList<>(),
        "These are all the books that you have been accepted to borrow", parent, "Accepted");
    pagerAdapter.addFragment(
        new Pair<Class<? extends Fragment>, Bundle>(GenericListFragment.class, bundle));
  }

  /**
   * onCreateView to set up any listeners and values
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
    View view = inflater.inflate(R.layout.fragment_borrowed, container, false);
    tabLayout = (TabLayout) view.findViewById(R.id.tabs_sub_borrowed);
    viewPager2 = view.findViewById(R.id.view_pager_borrowed);

    // TABS
    TabItem borrowedTab = (TabItem) view.findViewById(R.id.tabs_sub_borrowed_borrowed);
    TabItem requestedTab = (TabItem) view.findViewById(R.id.tabs_sub_borrowed_requested);
    TabItem acceptedTab = (TabItem) view.findViewById(R.id.tabs_sub_borrowed_accepted);

    // Configure viewpager2 options and initialize page adapter
    viewPager2.setUserInputEnabled(false);
    viewPager2.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
    viewPager2.setOffscreenPageLimit(tabLayout.getTabCount());
    viewPager2.setAdapter(pagerAdapter);

    /**
     * Changes the shown fragment depending on which tab is selected
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