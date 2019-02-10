package francesca.pascalau.thesis.common;

import java.io.Serializable;

/**
 * Created by Francesca on 21.06.2018.
 */

public class Position implements Serializable {

    private double latitude;
    private double longitude;

    public Position(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Position() {
        this.latitude = 0;
        this.longitude = 0;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "{" +
                "latitude= " + latitude +
                ", longitude= " + longitude +
                '}';
    }


}
