package com.unex.tfg.model;

import androidx.annotation.NonNull;

// This class is not stored in the database
public class NearbyDevice {

    // Nearby Device latitude
    private double geo_lat;

    // Nearby Device longitude
    private double geo_long;

    /**
     * NearbyDevice empty constructor
     */
    public NearbyDevice() {
    }

    /**
     * NearbyDevice parametrized constructor
     * @param geo_lat NearbyDevice latitude
     * @param geo_long NearbyDevice longitude
     */
    @SuppressWarnings("unused")
    public NearbyDevice(double geo_lat, double geo_long) {
        this.geo_lat = geo_lat;
        this.geo_long = geo_long;
    }

    /**
     * Getter of NearbyDevice latitude
     * @return NearbyDevice latitude
     */
    public double getGeo_lat() {
        return geo_lat;
    }

    /**
     * Setter of NearbyDevice latitude
     * @param geo_lat NearbyDevice latitude
     */
    public void setGeo_lat(double geo_lat) {
        this.geo_lat = geo_lat;
    }

    /**
     * Getter of NearbyDevice longitude
     * @return NearbyDevice longitude
     */
    public double getGeo_long() {
        return geo_long;
    }

    /**
     * Setter of NearbyDevice longitude
     * @param geo_long NearbyDevice longitude
     */
    public void setGeo_long(double geo_long) {
        this.geo_long = geo_long;
    }

    /**
     * equals method
     * @param o NearbyDevice
     * @return true if both objects are equals, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NearbyDevice that = (NearbyDevice) o;
        return Double.compare(that.geo_lat, geo_lat) == 0 &&
                Double.compare(that.geo_long, geo_long) == 0;
    }

    /**
     * toString method
     * @return String representing the object
     */
    @NonNull
    @Override
    public String toString() {
        return "NearbyDevice{" +
                ", geo_lat=" + geo_lat +
                ", geo_long=" + geo_long +
                '}';
    }

}
