package iagl.ifi.vlille.view;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;
import org.xml.sax.Locator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import iagl.ifi.vlille.R;
import iagl.ifi.vlille.model.Station;
import iagl.ifi.vlille.provider.StationDataProvider;

/**
 * Created by Antoine on 17/09/2014.
 */
public class StationAdapter extends ArrayAdapter<Station> {

    public StationAdapter(Context context, Station[] stations) {
        super(context, android.R.layout.select_dialog_item, stations);
    }

    public StationAdapter(Context context, List<Station> stations) {
        super(context, android.R.layout.select_dialog_item, stations);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = super.getView(position, convertView, parent).findViewById(android.R.id.text1);

        return rootView;
    }
}
