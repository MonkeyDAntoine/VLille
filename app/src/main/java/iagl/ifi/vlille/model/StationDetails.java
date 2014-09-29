package iagl.ifi.vlille.model;

import java.util.Date;

/**
 * Created by Antoine on 16/09/2014.
 */
public class StationDetails {

    private String adress;
    private Integer status;
    private Integer bikes;
    private Integer attachs;
    private String paiement;
    private String lastupd;

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setBikes(Integer bikes) {
        this.bikes = bikes;
    }

    public void setAttachs(Integer attachs) {
        this.attachs = attachs;
    }

    public void setPaiement(String paiement) {
        this.paiement = paiement;
    }

    public void setLastupd(String lastupd) {
        this.lastupd = lastupd;
    }

    public Integer getStatus() {
        return status;
    }

    public String getPaiement() {
        return paiement;
    }

    public String getLastupd() {
        return lastupd;
    }

    public boolean isWithTPE() {
        return  "AVEC_TPE".equals(getPaiement()); //SANS_TPE
    }
    public Integer getBikes() {
        return bikes;
    }

    public String getAdress() {
        return adress;
    }

    public Integer getAttachs() {
        return attachs;
    }

    public static enum TagXML {
        STATION, ADRESS, BIKES, ATTACHES, PAIEMENT, LASTUPD
    }

    public boolean isMaintenance() {
        return getStatus() == 1;
    }

}

