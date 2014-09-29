package iagl.ifi.vlille.provider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import iagl.ifi.vlille.R;

/**
 * Created by Antoine on 28/09/2014.
 */
public class LocationDataProvider {

    private static boolean wasInitialized = false;

    public static boolean canGetLocation(final Activity activity) {

        if (!isGPSEnabled(activity) && !isNetworkEnabled(activity)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
            // Setting Dialog Title
            alertDialog.setTitle("GPS");
            // Setting Dialog Message
            alertDialog.setMessage(activity.getResources().getString(R.string.prompt_to_enable_gps_message));
            // Setting Icon to Dialog
            //alertDialog.setIcon(R.drawable.delete);
            // On pressing Settings button
            alertDialog.setPositiveButton(activity.getResources().getString(R.string.settings_text), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    activity.startActivity(intent);
                }
            });
            // on pressing cancel button
            alertDialog.setNegativeButton(activity.getResources().getString(R.string.cancel_text), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            // Showing Alert Message
            alertDialog.show();
            return false;
        } else {
            return true;
        }
    }

    private static boolean isGPSEnabled(Activity activity) {
        LocationManager locationManager = (LocationManager) activity
                .getSystemService(Context.LOCATION_SERVICE);
        return locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private static boolean isNetworkEnabled(Activity activity) {
        LocationManager locationManager = (LocationManager) activity
                .getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static LatLng getCurrentLocation(final Activity activity) {
        LocationManager locationManager = (LocationManager) activity
                .getSystemService(Context.LOCATION_SERVICE);

        if (canGetLocation(activity) && locationManager != null) {
            if (!wasInitialized) {
                wasInitialized = true;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        // Log.d("location", location.toString());
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        //Log.d("provider", provider + " " + status);
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        //Log.d("provider", provider);
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        //Log.d("provider", provider);
                    }
                });
            }

            // First get location from Network Provider
            Location location = null;
            Double latitude = null;
            Double longitude = null;
            if (isNetworkEnabled(activity)) {
/*                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);*/
                if (locationManager != null) {
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
            // if GPS Enabled get lat/long using GPS Services
            if (isGPSEnabled(activity)) {
                if (location == null) {
                 /*   locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);*/
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }

            if (latitude != null && longitude != null) {
                return new LatLng(latitude, longitude);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}
