package com.team41.boromi.book;

import android.Manifest.permission;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.team41.boromi.BookActivity;
import com.team41.boromi.BookViewModel;
import com.team41.boromi.R;
import com.team41.boromi.adapters.MapInfoWindowAdapter;
import com.team41.boromi.adapters.SubListAdapter;
import com.team41.boromi.models.Book;
import java.util.ArrayList;

/**
 * Map fragment shows google maps and the exchange locations
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

  private static final String MODE_KEY = "Mode";
  private GoogleMap googleMap;
  private UiSettings mapUiSettings;
  private FloatingActionButton confirmButton;
  private TextView tooltip;
  private RadioGroup radioGroup;

  private Marker currentMarker;

  private int mode; // 0 viewing, 1 add
  private BookViewModel bookViewModel;
  private ArrayList<Book> bookLocations;

  public MapFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @return A new instance of fragment MapFragment.
   */
  public static MapFragment newInstance(Bundle bundle) {
    MapFragment fragment = new MapFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bookViewModel = new ViewModelProvider(requireActivity()).get(BookViewModel.class);
    bookLocations = new ArrayList<>();
    if (getArguments() != null) {
      mode = getArguments().getInt(MODE_KEY);
    }
  }

  /**
   * Sets up google maps and corresponding on click listeners
   * @param inflater
   * @param container
   * @param savedInstanceState
   * @return
   */
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_map, container, false);
    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    tooltip = view.findViewById(R.id.map_tooltip);
    confirmButton = view.findViewById(R.id.map_confirm_pin);
    radioGroup = view.findViewById(R.id.map_radio_group);
    radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (i == R.id.map_filter_all)
          updateMarkers(0);
        else if (i == R.id.map_filter_owner)
          updateMarkers(1);
        else if (i == R.id.map_filter_borrower)
          updateMarkers(2);
      }
    });
    confirmButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        bookViewModel.setExchangeLocation(currentMarker.getPosition());
        TabLayout.Tab tab = ((BookActivity) getActivity()).getTab(0);
        tab.select();
      }
    });
    if (mode == 0) {
      confirmButton.hide();
      tooltip.setVisibility(View.INVISIBLE);
    }
    return view;
  }

  /**
   * Updates the marker based on filter options
   * @param options 0 for all markers, 1 for books you own, 2 for books you are borrowing
   */
  private void updateMarkers(int options) {
    googleMap.clear();
    for (Book book : bookLocations) {
      if (book.getLocationLat() == null || book.getLocationLon() == null) {
        continue;
      }
      LatLng location = new LatLng(book.getLocationLat(), book.getLocationLon());
      String snipit = book.getTitle() + ",";
      if (book.getOwner().equals(bookViewModel.getUser().getUUID()) && (options==0||options==1)) {
        snipit += "1," + book.getBorrowerName();
        googleMap.addMarker(new MarkerOptions().position(location).snippet(snipit));
      } else if (book.getBorrower().equals(bookViewModel.getUser().getUUID()) && (options==0||options==2)) {
        snipit += "0," + book.getOwnerName();
        googleMap.addMarker(new MarkerOptions().position(location).snippet(snipit))
            .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
      }
    }
    if (currentMarker != null) {
      currentMarker = googleMap
          .addMarker(new MarkerOptions().position(currentMarker.getPosition()).draggable(true).icon(
              BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
    }
  }

  /**
   * User navigated away from map, reset to view mode
   */
  @Override
  public void onPause() {
    setMode(0);
    bookViewModel.setExchangeBook(null);
    bookViewModel.setExchangeBookRequest(null);
    if (currentMarker != null) {
      currentMarker.remove();
      currentMarker = null;
    }
    super.onPause();
  }

  /**
   * When google maps is ready, set onclick listeners and observables
   * @param googleMap
   */
  @Override
  public void onMapReady(final GoogleMap googleMap) {
    this.googleMap = googleMap;
    mapUiSettings = googleMap.getUiSettings();
    mapUiSettings.setZoomControlsEnabled(true);
    mapUiSettings.setMapToolbarEnabled(false);
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.5231923, -113.5), 11.0f));
    googleMap.setInfoWindowAdapter(new MapInfoWindowAdapter(this));
    this.googleMap.setOnMapClickListener(new OnMapClickListener() {
      @Override
      public void onMapClick(LatLng latLng) {
        if (mode == 0) { // Viewing mode
          return;
        }
        if (currentMarker != null) {
          currentMarker.remove();
          currentMarker = null;
        }
        currentMarker = googleMap
            .addMarker(new MarkerOptions().position(latLng).draggable(true).icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
      }
    });
    final Observer<ArrayList<Book>> locationObserver = new Observer<ArrayList<Book>>() {
      @Override
      public void onChanged(ArrayList<Book> books) {

        bookLocations.clear();
        bookLocations.addAll(books);
        updateMarkers(0);
        radioGroup.check(R.id.map_filter_all);
      }
    };
    bookViewModel.getLocations().observe(getViewLifecycleOwner(), locationObserver);
  }

  /**
   * Sets viewing mode.
   * @param mode 0 for viewing mode, 1 for editing mode
   */
  public void setMode(int mode) {
    this.mode = mode;
    if (mode == 0) {
      confirmButton.hide();
      tooltip.setVisibility(View.INVISIBLE);
    } else {
      confirmButton.show();
      tooltip.setVisibility(View.VISIBLE);
    }
  }
}