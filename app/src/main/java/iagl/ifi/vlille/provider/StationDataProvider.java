package iagl.ifi.vlille.provider;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.StrictMode;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import iagl.ifi.vlille.R;
import iagl.ifi.vlille.model.Station;
import iagl.ifi.vlille.xml.StationDetailsParserHandler;
import iagl.ifi.vlille.xml.StationParserHandler;

/**
 * Created by Antoine on 22/09/2014.
 */
public class StationDataProvider {

    private static List<Station> allStations;

    public static List<Station> getAllStations() {
        return getAllStations(false);
    }

    public static List<Station> getAllStations(boolean force) {
        if (allStations == null || force) {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                SAXParser saxParser = saxParserFactory.newSAXParser();
                StationParserHandler handler = new StationParserHandler();
                saxParser.parse(StationParserHandler.HTTP_LIST_STATION_URL, handler);
                allStations = handler.getStations();
                Collections.sort(allStations, new Comparator<Station>() {
                    @Override
                    public int compare(Station lhs, Station rhs) {
                        return lhs.getName().compareToIgnoreCase(rhs.getName());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return allStations;
    }

    public static void getDetails(Station station) {
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            SAXParser saxParser = saxParserFactory.newSAXParser();
            StationDetailsParserHandler handler = new StationDetailsParserHandler();
            saxParser.parse(StationDetailsParserHandler.HTTP_DETAILS_STATION_URL(station.getId()), handler);
            station.setDetails(handler.getDetails());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
