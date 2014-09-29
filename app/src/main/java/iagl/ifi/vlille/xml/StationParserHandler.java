package iagl.ifi.vlille.xml;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import iagl.ifi.vlille.model.Station;

/**
 * Created by Antoine on 16/09/2014.
 */
public class StationParserHandler extends DefaultHandler {

    public static final String HTTP_LIST_STATION_URL = "http://vlille.fr/stations/xml-stations.aspx";
    private List<Station> stations;
    private TagXML currentTag;
    private Station currentStation;

    public StationParserHandler() {
    }

    public List<Station> getStations() {
        return stations;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentTag = null;

        try {
            currentTag = TagXML.valueOf(qName.toUpperCase());
            if (currentTag.equals(TagXML.MARKERS)) {
                stations = new ArrayList<Station>();
            } else if (currentTag.equals(TagXML.MARKER)) {
                currentStation = new Station();
                currentStation.setId(Integer.parseInt(attributes.getValue(AttributeXML.ID.toString().toLowerCase())));
                currentStation.setName(attributes.getValue(AttributeXML.NAME.toString().toLowerCase()));
                currentStation.setLongitude(Double.parseDouble(attributes.getValue(AttributeXML.LNG.toString().toLowerCase())));
                currentStation.setLatitude(Double.parseDouble(attributes.getValue(AttributeXML.LAT.toString().toLowerCase())));
            }
        } catch (IllegalArgumentException iae) {
            Log.e("unknown tag", qName.toUpperCase());
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (stations != null && currentStation != null) {
            stations.add(currentStation);
        }
    }

    public static enum TagXML {
        MARKERS, MARKER
    }

    public static enum AttributeXML {
        ID, LAT, LNG, NAME
    }
}
