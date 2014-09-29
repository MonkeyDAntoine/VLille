package iagl.ifi.vlille.view;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import iagl.ifi.vlille.MainActivity;
import iagl.ifi.vlille.R;
import iagl.ifi.vlille.model.Station;
import iagl.ifi.vlille.provider.StationDataProvider;

public class ListStationFragment extends Fragment implements View.OnClickListener {
    public static int SECTION_NUMBER = 2;
    private ListView stationList;
    private List<Station> stations;
    private LinkedHashMap<String, Integer> mapIndex;

    public ListStationFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_list_stations, container, false);

        stations = StationDataProvider.getAllStations();

        stationList = (ListView) rootView.findViewById(R.id.list_stations);
        stationList.setAdapter(new ArrayAdapter<Station>(getActivity(),
                android.R.layout.simple_list_item_1, stations.toArray(new Station[stations.size()])));

        stationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.container, new SearchStationFragment().setStationToShow((Station) stationList.getAdapter().getItem(position)));
                transaction.commit();
            }
        });

        getIndexList(stations);

        LinearLayout indexLayout = (LinearLayout) rootView.findViewById(R.id.side_index);

        TextView textView;
        List<String> indexList = new ArrayList<String>(mapIndex.keySet());
        for (String index : indexList) {
            textView = (TextView) getActivity().getLayoutInflater().inflate(
                    R.layout.side_index_item, null);
            textView.setText(index);
            textView.setOnClickListener(this);
            indexLayout.addView(textView);
        }

        return rootView;
    }

    private void getIndexList(List<Station> stations) {
        mapIndex = new LinkedHashMap<String, Integer>();

        Iterator<Station> it = stations.iterator();
        int i = 0;
        while (it.hasNext()) {
            String letter = it.next().getName().substring(0, 1);
            if (mapIndex.get(letter) == null) {
                mapIndex.put(letter, i);
            }
            i++;
        }

    }

    public void onClick(View view) {
        TextView selectedIndex = (TextView) view;
        stationList.setSelection(mapIndex.get(selectedIndex.getText()));
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(SECTION_NUMBER);
    }
}