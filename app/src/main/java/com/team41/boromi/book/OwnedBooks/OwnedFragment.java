package com.team41.boromi.book.OwnedBooks;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.team41.boromi.R;
import com.team41.boromi.auth.WelcomeFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OwnedFragment#newInstance} factory method to
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

    public OwnedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
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

        if (manager == null) {
            manager = getChildFragmentManager();
        }
        if (manager.findFragmentById(R.id.owned_fragment) == null) {
            OwnedAvailableFragment ownedAvailableFragment = new OwnedAvailableFragment();
            ft = manager.beginTransaction();
            ft.add(R.id.owned_fragment, ownedAvailableFragment).commit();
        }

        availableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OwnedAvailableFragment ownedAvailableFragment = new OwnedAvailableFragment();
                ft = manager.beginTransaction();
                ft.replace(R.id.owned_fragment, ownedAvailableFragment).addToBackStack("available").commit();
            }
        });
        requestsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OwnedRequestsFragment ownedRequestsFragment = new OwnedRequestsFragment();
                ft = manager.beginTransaction();
                ft.replace(R.id.owned_fragment, ownedRequestsFragment).addToBackStack("requests").commit();
            }
        });
        acceptedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OwnedAcceptedFragment ownedAcceptedFragment = new OwnedAcceptedFragment();
                ft = manager.beginTransaction();
                ft.replace(R.id.owned_fragment, ownedAcceptedFragment).addToBackStack("accepted").commit();
            }
        });
        lentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OwnedLentFragment ownedLentFragment = new OwnedLentFragment();
                ft = manager.beginTransaction();
                ft.replace(R.id.owned_fragment, ownedLentFragment).addToBackStack("lent").commit();
            }
        });
        return view;
    }
}