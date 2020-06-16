package dam.android.raul.u5_t9;

public class geonamesPlace
{
    private String lng;
    private String lat;
    private String summer;

    public geonamesPlace(String lng, String lat, String summer) {
        this.lng = lng;
        this.lat = lat;
        this.summer = summer;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getSummer() {
        return summer;
    }

    public void setSummer(String summer) {
        this.summer = summer;
    }

    @Override
    public String toString() {
        return this.summer+"\n\nLatitud:"+this.lat+"\tLongitud :"+this.lng;
    }
}
