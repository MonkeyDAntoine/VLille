package iagl.ifi.vlille.model;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.xml.sax.Attributes;

/**
 * Created by Antoine on 16/09/2014.
 */
public class Station implements Comparable<Station> {

    private Integer id;
    private Double latitude;
    private Double longitude;
    private String name;
    private StationDetails details;

    private String infos;

    @Override
    public String toString() {
        return (infos == null || infos.isEmpty())? name : name+" "+infos;
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        } else return super.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Station) {
            if (id != null) {
                return id.equals(((Station) o).id);
            }
        }
        return super.equals(o);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StationDetails getDetails() {
        return details;
    }

    public void setDetails(StationDetails details) {
        this.details = details;
    }

    @Override
    public int compareTo(Station another) {
        if (another.name == null) {
            return 1;
        }
        if (name == null) {
            return 1;
        }
        return name.compareTo(another.name);
    }

    public int distanceTo(Location startLocation) {
        Location stationLoc = new Location("station");
        Double lat = getLatitude();
        Double lng = getLongitude();
        stationLoc.setLatitude(lat);
        stationLoc.setLongitude(lng);
        return new Float(startLocation.distanceTo(stationLoc)).intValue();
    }

    public String getInfos() {
        return infos;
    }

    public void setInfos(String infos) {
        this.infos = infos;
    }
}

