package iagl.ifi.vlille.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import iagl.ifi.vlille.MainActivity;
import iagl.ifi.vlille.R;
import iagl.ifi.vlille.model.Station;
import iagl.ifi.vlille.provider.StationDataProvider;

public class MapAllStationFragment extends Fragment {
    public static int SECTION_NUMBER = 3;

    private List<Station> stations;
    private GoogleMap map;
    private View rootView;
    private LatLngBounds.Builder bc;

    public MapAllStationFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            rootView = inflater.inflate(R.layout.fragment_map_stations, container, false);
        } catch (InflateException ie) {
        }

        ViewTreeObserver observer = rootView .getViewTreeObserver();

        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 80));
            }
        });

        stations = StationDataProvider.getAllStations();

        MapFragment mapFrag = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_all));
        map = mapFrag.getMap();
        map.setOnMarkerClickListener(new MarkerListener());

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        bc = new LatLngBounds.Builder();
        LatLng latLng = null;

        for (Station station : stations) {
            latLng = new LatLng(station.getLatitude(), station.getLongitude());
            Marker markerStation = map.addMarker(new MarkerOptions().position(latLng).title(station.getName()));
            bc.include(markerStation.getPosition());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(SECTION_NUMBER);
    }

    private class MarkerListener implements GoogleMap.OnMarkerClickListener {
        @Override
        public boolean onMarkerClick(Marker marker) {
            if (marker.isInfoWindowShown()) {
                marker.hideInfoWindow();
            }
            else {
                marker.showInfoWindow();
            }
            return true;
        }
    }
}