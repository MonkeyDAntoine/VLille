package iagl.ifi.vlille.xml;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import iagl.ifi.vlille.model.Station;
import iagl.ifi.vlille.model.StationDetails;

/**
 * Created by Antoine on 16/09/2014.
 * <station>
 * <adress>LMCU RUE DU BALLON </adress>
 * <status>0</status>
 * <bikes>0</bikes>
 * <attachs>36</attachs>
 * <paiement>AVEC_TPE</paiement>
 * <lastupd>7 secondes</lastupd>
 * </station>
 */
public class StationDetailsParserHandler extends DefaultHandler {

    private StationDetails details;
    private TagXML currentTag;

    public static final String HTTP_DETAILS_STATION_URL(Integer idBorne) {
        return "http://vlille.fr/stations/xml-station.aspx?borne=" + idBorne;
    }

    public StationDetails getDetails() {
        return details;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            currentTag = TagXML.valueOf(qName.toUpperCase());
            switch (currentTag) {
                case STATION:
                    details = new StationDetails();
                    break;
                default:
            }
        } catch (IllegalArgumentException iae) {
            Log.e("unknown tag", qName.toUpperCase());
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        currentTag = null;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentTag == null) {
            return;
        }

        String valAsString = new String(ch, start, length);
        switch (currentTag) {
            case ADRESS:
                details.setAdress(valAsString);
                break;
            case ATTACHS:
                details.setAttachs(Integer.parseInt(valAsString));
                break;
            case BIKES:
                details.setBikes(Integer.parseInt(valAsString));
                break;
            case LASTUPD:
                details.setLastupd(valAsString);
                break;
            case PAIEMENT:
                details.setPaiement(valAsString);
                break;
            case STATUS:
                details.setStatus(Integer.parseInt(valAsString));
                break;
            default:
        }
    }

    public static enum TagXML {
        STATION, ADRESS, STATUS, BIKES, ATTACHS, PAIEMENT, LASTUPD
    }

}
