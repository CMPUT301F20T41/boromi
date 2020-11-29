package com.team41.boromi.adapters;

import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.team41.boromi.BookActivity;
import com.team41.boromi.R;
import com.team41.boromi.book.MapFragment;

/**
 * Custom window for google maps markers
 */
public class MapInfoWindowAdapter implements InfoWindowAdapter {

  private final View contents;

  /**
   * Constructor of custom window
   * @param mapFragment instance of MapFragment
   */
  public MapInfoWindowAdapter(MapFragment mapFragment) {
    contents = mapFragment.getLayoutInflater().inflate(R.layout.model_map_info, null);
  }

  @Override
  public View getInfoWindow(Marker marker) {
    return null;
  }

  /**
   * Sets value in the view
   */
  @Override
  public View getInfoContents(Marker marker) {
    String[] desc = marker.getSnippet().split(",");
    TextView title = contents.findViewById(R.id.model_map_info_book_title);
    TextView owner_borrower = contents.findViewById(R.id.model_map_info_owner_borrower);
    title.setText("Title: " + desc[0]);
    if (desc[1].equals("0")) {
      owner_borrower.setText("Borrowing from " + desc[2]);
    } else {
      owner_borrower.setText("Lending to " + desc[2]);
    }

    return contents;
  }
}
