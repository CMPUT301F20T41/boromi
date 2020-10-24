package com.team41.boromi.book;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.team41.boromi.R;

/**
 * A simple {@link Fragment} subclass. Use the {@link OwnedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OwnedFragment extends Fragment {

  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "param1";
  private static final String ARG_PARAM2 = "param2";

  // TODO: Rename and change types of parameters
  private String mParam1;
  private String mParam2;

  private FragmentManager manager = null;
  FragmentTransaction ft;
  GenericListFragment activeFragment;

  public OwnedFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @param param1 Parameter 1.
   * @param param2 Parameter 2.
   * @return A new instance of fragment BookFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static OwnedFragment newInstance(String param1, String param2) {
    OwnedFragment fragment = new OwnedFragment();
    Bundle args = new Bundle();
    args.putString(ARG_PARAM1, param1);
    args.putString(ARG_PARAM2, param2);
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mParam1 = getArguments().getString(ARG_PARAM1);
      mParam2 = getArguments().getString(ARG_PARAM2);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_owned, container, false);
    Button availableButton = (Button) view.findViewById(R.id.book_available_tab);
    Button requestsButton = (Button) view.findViewById(R.id.book_requests_tab);
    Button acceptedButton = (Button) view.findViewById(R.id.book_accepted_tab);
    Button lentButton = (Button) view.findViewById(R.id.book_lent_tab);

    // set default fragment
    if (manager == null) {
      manager = getChildFragmentManager();
    }
    if (manager.findFragmentById(R.id.owned_fragment) == null) {
      GenericListFragment ownedAvailableFragment = GenericListFragment
          .newInstance("This is owned available books");
      ft = manager.beginTransaction();
      ft.add(R.id.owned_fragment, ownedAvailableFragment, "available").commit();
      activeFragment = ownedAvailableFragment;
    }

    availableButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        createFragment("available", "This is owned available books");
      }
    });
    requestsButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        createFragment("requests", "This is owned requests books");
      }
    });
    acceptedButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        createFragment("accepted", "This is owned accepted books");
      }
    });
    lentButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        createFragment("lent", "This is owned lent books");
      }
    });
    return view;
  }

  private void createFragment(String tag, String message) {
    // Check if fragment already exists
    GenericListFragment genericListFragment = (GenericListFragment) manager.findFragmentByTag(tag);
    if (genericListFragment == activeFragment) { // return if fragment is currently active
      return;
    }
    ft = manager.beginTransaction();
    if (genericListFragment
        != null) {  // if fragment exists, show it and hide the previous fragment
      ft.show(genericListFragment).hide(activeFragment).addToBackStack(null).commit();
      activeFragment = genericListFragment;
    } else { // create new fragment
      genericListFragment = GenericListFragment.newInstance(message);
      ft.add(R.id.owned_fragment, genericListFragment, tag).hide(activeFragment)
          .addToBackStack(null).commit();
    }
    activeFragment = genericListFragment;   // set fragment as active
  }
}