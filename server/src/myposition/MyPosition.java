package myposition;

import base.BaseJson;
import net.sf.json.JSONObject;

public class MyPosition implements BaseJson {
    private final double SCALE_NUMBER = 10000.0;
    private int myLatitude;
    private int myLongitude;
    private double latitude;
    private double longitude;

    public MyPosition() {
        this.myLatitude = 0;
        this.myLongitude = 0;
        this.latitude = 0;
        this.longitude = 0;
    }

    public MyPosition(double latitude, double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }

    public int getMyLatitude() {
        return myLatitude;
    }

    public void setMyLatitude(int myLatitude) {
        this.myLatitude = myLatitude;
        latitude = myLatitude / SCALE_NUMBER;
    }

    public int getMyLongitude() {
        return myLongitude;
    }

    public void setMyLongitude(int myLongitude) {
        this.myLongitude = myLongitude;
        longitude = myLongitude / SCALE_NUMBER;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        myLatitude = (int) (latitude * SCALE_NUMBER);
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        myLongitude = (int) (longitude * SCALE_NUMBER);
    }

    public double getSCALE_NUMBER() {
        return SCALE_NUMBER;
    }

    @Override
    public String toString() {
        return "MyPosition{" +
                "myLatitude=" + myLatitude +
                ", myLongitude=" + myLongitude +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", SCALE_NUMBER=" + SCALE_NUMBER +
                '}';
    }

    @Override
    public void fromJSONObject(JSONObject param) {
        myLatitude = Integer.parseInt(param.get("myLatitude").toString());
        myLongitude = Integer.parseInt(param.get("myLongitude").toString());
        latitude = Double.parseDouble(param.get("latitude").toString());
        longitude = Double.parseDouble(param.get("longitude").toString());
    }
}