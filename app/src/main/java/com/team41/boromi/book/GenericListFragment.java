package com.team41.boromi.book;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.team41.boromi.R;

/**
 * A simple {@link Fragment} subclass. Use the {@link GenericListFragment#newInstance} factory
 * method to create an instance of this fragment.
 */
public class GenericListFragment extends Fragment {

  // TODO: Rename parameter arguments, choose names that match
  // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
  private static final String ARG_PARAM1 = "msg";

  // TODO: Rename and change types of parameters
  private String tempMsg;

  public GenericListFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @return A new instance of fragment OwnedAcceptedFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static GenericListFragment newInstance(Bundle bundle) {
    GenericListFragment fragment = new GenericListFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      tempMsg = getArguments().getString(ARG_PARAM1);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_generic_list, container, false);
    TextView tempMsgView = view.findViewById(R.id.tempMessage);
    tempMsgView.setText(tempMsg);
    return view;
  }
}