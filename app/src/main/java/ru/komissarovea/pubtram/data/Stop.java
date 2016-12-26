package ru.komissarovea.pubtram.data;

import android.location.Location;

public class Stop {
    private int ID;
    private String Name;
    private String Street;
    private String Info;
    private double Longitude;
    private double Latitude;
    private String StopsString;
    private String StopNumber;
    private Double currentDistance;

    public int getID() {
        return ID;
    }

    public void setID(int iD) {
        ID = iD;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public String getStopsString() {
        return StopsString;
    }

    public void setStopsString(String stopsString) {
        StopsString = stopsString;
    }

    public String getStopNumber() {
        return StopNumber;
    }

    public void setStopNumber(String stopNumber) {
        StopNumber = stopNumber;
    }

    public Double getCurrentDistance() {
        return currentDistance;
    }

    public void setCurrentDistance(double currentDistance) {
        this.currentDistance = currentDistance;
    }

    public Location getLocation() {
        Location location = new Location("stop");
        location.setLatitude(Latitude);
        location.setLongitude(Longitude);
        return location;
    }
}
