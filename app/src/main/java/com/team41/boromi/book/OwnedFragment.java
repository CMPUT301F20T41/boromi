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
import com.team41.boromi.BookActivity;
import com.team41.boromi.R;
import com.team41.boromi.adapters.PagerAdapter;
import com.team41.boromi.callbacks.BookCallback;
import com.team41.boromi.callbacks.BookRequestCallback;
import com.team41.boromi.models.Book;
import com.team41.boromi.models.BookRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass. Use the {@link OwnedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OwnedFragment extends Fragment {

  private TabLayout tabLayout;
  private ViewPager2 viewPager2;
  private PagerAdapter pagerAdapter;
  private BookActivity bookActivity;
  private String parent = "Owned";
  private String TAG = "OwnedFrag";

  public OwnedFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @return A new instance of fragment BookFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static OwnedFragment newInstance() {
    OwnedFragment fragment = new OwnedFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bookActivity = (BookActivity) getActivity();
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


  public void getOwnerAvailable(GenericListFragment fragment) {
    bookActivity.getBookController()
        .getOwnerAvailableBooks(bookActivity.getUser().getUUID(), new BookCallback() {
          @Override
          public void onSuccess(ArrayList<Book> books) {
            bookActivity.getCollections().put("OwnerAvailable", books);
            fragment.updateData(books);
          }

          @Override
          public void onFailure(Exception e) {

          }
        });
  }

  public void getOwnerRequests(GenericListFragment fragment) {
    bookActivity.getBookRequestController().getRequestOnOwnedBooks(new BookRequestCallback() {
      @Override
      public void onComplete(Map<Book, List<BookRequest>> bookWithRequests) {
        bookActivity.setRequestsCollections(bookWithRequests);
        fragment.updateData(bookWithRequests);
      }
    });
  }

  public void getOwnerAccepted(GenericListFragment fragment) {
    bookActivity.getBookController()
        .getOwnerAcceptedBooks(bookActivity.getUser().getUUID(), new BookCallback() {
          @Override
          public void onSuccess(ArrayList<Book> books) {
            bookActivity.getCollections().put("OwnerAccepted", books);
            fragment.updateData(books);
          }

          @Override
          public void onFailure(Exception e) {

          }
        });
  }

  public void getOwnerLent(GenericListFragment fragment) {
    bookActivity.getBookController().getOwnerBorrowedBooks(bookActivity.getUser().getUUID(),
        new BookCallback() {
          @Override
          public void onSuccess(ArrayList<Book> books) {
            bookActivity.getCollections().put("OwnerLent", books);
            fragment.updateData(books);
          }

          @Override
          public void onFailure(Exception e) {

          }
        });
  }

  public void getData(String tag, GenericListFragment fragment) {
    if (tag.equals("Available")) {
      getOwnerAvailable(fragment);
    } else if (tag.equals("Requested")) {
      getOwnerRequests(fragment);
    } else if (tag.equals("Accepted")) {
      getOwnerAccepted(fragment);
    } else if (tag.equals("Lent")) {
      getOwnerLent(fragment);
    }
  }
}