package com.team41.boromi.book;

import android.Manifest.permission;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.team41.boromi.BookActivity;
import com.team41.boromi.R;
import com.team41.boromi.adapters.SubListAdapter;
import java.security.Permission;
import java.util.ArrayList;

/**
 * Not Implemented Yet
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {

  private static final String MODE_KEY = "Mode";

  private GoogleMap googleMap;
  private UiSettings mapUiSettings;
  private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;
  private static final int LOCATION_LAYER_PERMISSION_REQUEST_CODE = 2;

  private FloatingActionButton confirmButton;

  private ArrayList<Marker> markers;
  private Marker currentMarker;

  private int mode; // 0 viewing, 1 add
  private SubListAdapter adapter;

  public MapFragment() {
    // Required empty public constructor
  }

  /**
   * Use this factory method to create a new instance of this fragment using the provided
   * parameters.
   *
   * @return A new instance of fragment MapFragment.
   */
  // TODO: Rename and change types and number of parameters
  public static MapFragment newInstance(Bundle bundle) {
    MapFragment fragment = new MapFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (getArguments() != null) {
      mode = getArguments().getInt(MODE_KEY);
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_map, container, false);
    SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
        .findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);
    confirmButton = view.findViewById(R.id.map_confirm_pin);
    confirmButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        // TODO LOGIC TO SAVE DROPPED PIN
        SelectedLocation location = (SelectedLocation) adapter;
        adapter.onLocationSelected(currentMarker.getPosition());

        TabLayout.Tab tab = ((BookActivity) getActivity()).getTab(0);
        tab.select();
      }
    });
    if (mode == 0) {
      confirmButton.hide();
    }
    return view;
  }

  @Override
  public void onPause() {
    setMode(0);
    super.onPause();
  }

  @Override
  public void onMapReady(final GoogleMap googleMap) {
    this.googleMap = googleMap;
    mapUiSettings = googleMap.getUiSettings();
    mapUiSettings.setZoomControlsEnabled(true);
    mapUiSettings.setMapToolbarEnabled(false);
    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.5231923, -113.5), 11.0f));
    this.googleMap.setOnMapClickListener(new OnMapClickListener() {
      @Override
      public void onMapClick(LatLng latLng) {
        if (mode == 0) { // Viewing mode
          return;
        }
        if (currentMarker != null) {
          currentMarker.remove();
        }
        currentMarker = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true).icon(
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
      }
    });

    mapUiSettings.setMyLocationButtonEnabled(true);
    if (ActivityCompat.checkSelfPermission(getContext(), permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(getContext(), permission.ACCESS_COARSE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {
      return;
    }
    this.googleMap.setMyLocationEnabled(true);
  }

  @SuppressLint("MissingPermission")
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode == MY_LOCATION_PERMISSION_REQUEST_CODE) {
      if (checkPermissions(permission.ACCESS_FINE_LOCATION, permissions, grantResults)) {
        googleMap.setMyLocationEnabled(true);
      }
    } else if (requestCode == LOCATION_LAYER_PERMISSION_REQUEST_CODE) {
      if (checkPermissions(permission.ACCESS_FINE_LOCATION, permissions, grantResults)) {
        googleMap.setMyLocationEnabled(true);
      }
    }
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  private boolean checkPermissions(String p, String[] permissions, int[] grantResults) {
    for (int i = 0; i < permissions.length; i++) {
      if (p.equals(permissions[i])) {
        return grantResults[i] == PackageManager.PERMISSION_GRANTED;
      }
    }
    return false;
  }

  private void requestPermissions(int code) {
    // TODO request permissions
  }

  public void setMode(int mode) {
    this.mode = mode;
    if (mode == 0) {
      confirmButton.hide();
    } else {
      confirmButton.show();
    }
  }

  public void setAdapterCallback(SubListAdapter subListAdapter) {
    adapter = subListAdapter;
  }

  public interface SelectedLocation {
    void onLocationSelected(LatLng location);
  }
}