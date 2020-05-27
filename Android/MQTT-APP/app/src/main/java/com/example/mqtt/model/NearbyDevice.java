package com.example.mqtt.model;

import androidx.annotation.NonNull;

public class NearbyDevice {

    private double geo_lat;

    private double geo_long;

    public NearbyDevice() {
    }

    public NearbyDevice(double geo_lat, double geo_long) {
        this.geo_lat = geo_lat;
        this.geo_long = geo_long;
    }

    public double getGeo_lat() {
        return geo_lat;
    }

    public void setGeo_lat(double geo_lat) {
        this.geo_lat = geo_lat;
    }

    public double getGeo_long() {
        return geo_long;
    }

    public void setGeo_long(double geo_long) {
        this.geo_long = geo_long;
    }


    @NonNull
    @Override
    public String toString() {
        return "NearbyDevice{" +
                ", geo_lat=" + geo_lat +
                ", geo_long=" + geo_long +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NearbyDevice that = (NearbyDevice) o;
        return Double.compare(that.geo_lat, geo_lat) == 0 &&
                Double.compare(that.geo_long, geo_long) == 0;
    }

}
