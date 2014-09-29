package iagl.ifi.vlille.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import iagl.ifi.vlille.MainActivity;
import iagl.ifi.vlille.R;
import iagl.ifi.vlille.listener.RightDrawableOnTouchListener;
import iagl.ifi.vlille.model.Station;
import iagl.ifi.vlille.provider.LocationDataProvider;
import iagl.ifi.vlille.provider.StationDataProvider;

public class SearchStationFragment extends Fragment {

    public static final LatLng LILLE = new LatLng(50.629325, 3.057460);
    private static final String ARG_SECTION_NUMBER = "section_number";
    public static int SECTION_NUMBER = 2;
    private static View rootView;
    private Spinner spinner;
    private EditText nbMinEditText;
    private LinearLayout optionSearchLayout;
    private CheckBox checkBoxNearestStation;
    private EditText distanceMaxEditText;
    private ProgressBar progressBar;
    private AutoCompleteTextView autoCompleteTextView;
    private GoogleMap map;
    private Marker markerStation;
    private Marker markerCurrentPosition;
    private Polyline newPolyline;

    private Station stationToShow;

    public SearchStationFragment() {
        super();
    }

    private void setResources() {
        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        nbMinEditText = (EditText) rootView.findViewById(R.id.searchNb);

        optionSearchLayout = (LinearLayout) rootView.findViewById(R.id.optionSearch);
        checkBoxNearestStation = (CheckBox) optionSearchLayout.findViewById(R.id.isWithNearestStation);
        distanceMaxEditText = (EditText) optionSearchLayout.findViewById(R.id.searchMaxM);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.incrementProgressBy(1);
        progressBar.setProgress(0);

        autoCompleteTextView = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextView);

        MapFragment mapFrag = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
        map = mapFrag.getMap();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LILLE, 13.5f));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            rootView = inflater.inflate(R.layout.fragment_search_stations, container, false);
        } catch (InflateException ie) {
        }

        setResources();

        if (stationToShow != null) {
            showStation(stationToShow);
            stationToShow = null;
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    autoCompleteTextView.setVisibility(View.VISIBLE);
                    new SearchStationsTask() {
                        @Override
                        protected void onPostExecuteTask(List<Station> stations) {
                            StationAdapter adapter = new StationAdapter(getActivity(), stations);
                            autoCompleteTextView.setAdapter(adapter);
                            autoCompleteTextView.setThreshold(1);
                        }
                    }.execute();
                    nbMinEditText.setVisibility(View.INVISIBLE);
                } else if (position == 1 || position == 2) {
                    nbMinEditText.setVisibility(View.VISIBLE);
                    progressBar.setMax(StationDataProvider.getAllStations().size());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (getActivity().getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                }

                Station station = (Station) autoCompleteTextView.getAdapter().getItem(position);

                showStation(station);
            }
        });

        autoCompleteTextView.setOnTouchListener(new RightDrawableOnTouchListener(autoCompleteTextView) {
            @Override
            public boolean onDrawableTouch(final MotionEvent event) {
                autoCompleteTextView.setText("");
                autoCompleteTextView.clearListSelection();
                return false;
            }
        });

        nbMinEditText.setOnKeyListener(new SearchStationListener() {
            @Override
            protected boolean ifOnly() {
                return distanceMaxEditText.getVisibility() != View.VISIBLE;
            }
        });
        distanceMaxEditText.setOnKeyListener(new SearchStationListener() {
            @Override
            protected boolean ifOnly() {
                return true;
            }
        });
        autoCompleteTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN && autoCompleteTextView.getText().length() == 0) {
                    autoCompleteTextView.showDropDown();
                    return true;
                }
                return false;
            }
        });

        checkBoxNearestStation
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                    if (isChecked && LocationDataProvider.canGetLocation(getActivity())) {

                                                        LatLng latLng;
                                                        latLng = LocationDataProvider.getCurrentLocation(getActivity());
                                                        if (map != null && latLng != null) {
                                                            markerCurrentPosition = map.addMarker(new MarkerOptions().position(latLng)
                                                                    .title(getResources().getString(R.string.marker_current_position_title)));
                                                            markerCurrentPosition.showInfoWindow();
                                                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
                                                        }

                                                        distanceMaxEditText.setVisibility(View.VISIBLE);
                                                        optionSearchLayout.findViewById(R.id.prefixUnity).setVisibility(View.VISIBLE);
                                                        optionSearchLayout.findViewById(R.id.distanceUnity).setVisibility(View.VISIBLE);
                                                        return;
                                                    }
                                                    checkBoxNearestStation.setChecked(false);
                                                    distanceMaxEditText.setText("");
                                                    distanceMaxEditText.setVisibility(View.INVISIBLE);
                                                    optionSearchLayout.findViewById(R.id.prefixUnity).setVisibility(View.INVISIBLE);
                                                    optionSearchLayout.findViewById(R.id.distanceUnity).setVisibility(View.INVISIBLE);
                                                }
                                            }

                );

        return rootView;
    }

    private void showStation(Station station) {
        StationDataProvider.getDetails(station);

        TextView nameText = (TextView) rootView.findViewById(R.id.stationName);
        TextView addressText = (TextView) rootView.findViewById(R.id.stationAddress);
        TextView nbBikesText = (TextView) rootView.findViewById(R.id.stationNbBikes);
        TextView nbAttachesText = (TextView) rootView.findViewById(R.id.stationNbAttaches);
        TextView lastUpdateText = (TextView) rootView.findViewById(R.id.stationLastUpdate);

        ImageView imgTpe = (ImageView) rootView.findViewById(R.id.img_tpe);
        if (station.getDetails().isWithTPE()) {
            imgTpe.setImageDrawable(getResources().getDrawable(R.drawable.ic_tpe));
        } else {
            imgTpe.setImageDrawable(getResources().getDrawable(R.drawable.ic_notpe));
        }

        nameText.setText(station.getName());
        addressText.setText(station.getDetails().getAdress());
        nbBikesText.setText(station.getDetails().getBikes()+"");
        nbAttachesText.setText(station.getDetails().getAttachs()+"");
        lastUpdateText.setText(String.format(getResources().getString(R.string.lastupdt_info), station.getDetails().getLastupd()));

        if (map != null) {
            if (markerStation != null) {
                markerStation.remove();
            }

            LatLng latLng = new LatLng(station.getLatitude(), station.getLongitude());
            markerStation = map.addMarker(new MarkerOptions().position(latLng)
                    .title(station.getName()));
            markerStation.showInfoWindow();

            if (markerCurrentPosition != null) {
                LatLngBounds.Builder bc = new LatLngBounds.Builder();
                bc.include(markerCurrentPosition.getPosition());
                bc.include(markerStation.getPosition());
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bc.build(), 150));
            } else {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }

                   /* Marker kiel = map.addMarker(new MarkerOptions()
                            .position(new LatLng(53.551, 9.993))
                            .title("Kiel")
                            .snippet("Kiel is cool")
                            .icon(BitmapDescriptorFactory
                                    .fromResource(R.drawable.ic_launcher)));*/
        }
    }

    public boolean keepStation(Station station, int mode, int nb) {
        if (mode == 0) {
            return true;
        }

        StationDataProvider.getDetails(station);
        if (station.getDetails() != null) {
            if (mode == 1) {
                return (nb <= station.getDetails().getAttachs());
            } else if (mode == 2) {
                return (nb <= station.getDetails().getBikes());
            }
        }
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(SECTION_NUMBER);
    }

    public SearchStationFragment setStationToShow(Station stationToShow) {
        this.stationToShow = stationToShow;
        return this;
    }

    private abstract class SearchStationsTask extends AsyncTask<Void, Integer, List<Station>> {

        @Override
        protected List<Station> doInBackground(Void... params) {
            List<Station> stations = new ArrayList<Station>();
            String nbText = nbMinEditText.getText().toString().trim();
            String distanceMaxText = distanceMaxEditText.getText().toString().trim();

            int nb = 0;
            if (!nbText.isEmpty()) {
                nb = Integer.parseInt(nbText);
            }

            Integer distanceMax = null;
            if (!distanceMaxText.isEmpty()) {
                distanceMax = Integer.parseInt(distanceMaxText);
            }

            String[] choices = getResources().getStringArray(R.array.search_choices);

            int mode = 0;
            if (spinner.getSelectedItem().equals(choices[1])) {
                mode = 1;
            } else if (spinner.getSelectedItem().equals(choices[2])) {
                mode = 2;
            }

            Location startLocation = null;
            if (checkBoxNearestStation.isChecked() && LocationDataProvider.canGetLocation(getActivity())) {
                LatLng latLng = LocationDataProvider.getCurrentLocation(getActivity());
                if (latLng != null) {
                    startLocation = new Location("start");
                    startLocation.setLatitude(latLng.latitude);
                    startLocation.setLongitude(latLng.longitude);
                }
            }

            int nbProgress = 0;
            if (startLocation != null && distanceMax != null) {
                for (Station station : StationDataProvider.getAllStations()) {
                    publishProgress(nbProgress++);
                    int distance = station.distanceTo(startLocation);
                    if (distance <= distanceMax.intValue()) {
                        if (keepStation(station, mode, nb)) {
                            stations.add(station);
                        }
                    }
                }
            } else {
                for (Station station : StationDataProvider.getAllStations()) {
                    publishProgress(nbProgress++);
                    if (keepStation(station, mode, nb)) {
                        stations.add(station);
                    }
                }
            }

            if (startLocation != null) {
                final Location finalStartLocation = startLocation;
                Collections.sort(stations, new Comparator<Station>() {
                    @Override
                    public int compare(Station station1, Station station2) {
                        int d1 = station1.distanceTo(finalStartLocation);
                        int d2 = station2.distanceTo(finalStartLocation);
                        station1.setInfos(String.format(getResources().getString(R.string.distance_info), station1.distanceTo(finalStartLocation)));
                        station2.setInfos(String.format(getResources().getString(R.string.distance_info), station2.distanceTo(finalStartLocation)));

                        if (d1 < d2) {
                            return -1;
                        } else if (d2 < d1) {
                            return 1;
                        }
                        return 0;
                    }
                });
            }

            return stations;
        }

        @Override
        protected void onPostExecute(List<Station> stations) {
            if (stations.isEmpty()) {
                Toast.makeText(getActivity(), getResources().getString(R.string.no_result), Toast.LENGTH_LONG).show();
            }
            progressBar.setProgress(0);

            pgd.dismiss();
            if (!isCancelled()) {
                onPostExecuteTask(stations);
            }
        }

        @Override
        protected void onCancelled(List<Station> stations) {
            super.onCancelled(stations);
            pgd.dismiss();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Toast.makeText(getActivity(), getResources().getString(R.string.search_cancelled), Toast.LENGTH_SHORT).show();
        }

        protected abstract void onPostExecuteTask(List<Station> stations);

        @Override
        protected void onPreExecute() {
            final SearchStationsTask task = this;
           pgd = ProgressDialog.show(getActivity(), getResources().getString(R.string.app_name), getResources().getString(R.string.search_progress), true, true, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    task.cancel(true);
                }
            });
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setProgress(values[0]);
        }
    }

    private abstract class SearchStationListener implements View.OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN && ifOnly()) {
                new SearchStationsTask() {
                    @Override
                    protected void onPostExecuteTask(List<Station> stations) {
                        final StationAdapter adapter = new StationAdapter(getActivity(), stations);
                        autoCompleteTextView.setAdapter(adapter);
                        autoCompleteTextView.showDropDown();
                    }
                }.execute();
                return true;
            }
            return false;
        }

        protected abstract boolean ifOnly();
    }

    private ProgressDialog pgd;
    @Override
    public void onDestroy(){
        super.onDestroy();
        if ( pgd!=null && pgd.isShowing() ){
            pgd.dismiss();
        }
    }


}
