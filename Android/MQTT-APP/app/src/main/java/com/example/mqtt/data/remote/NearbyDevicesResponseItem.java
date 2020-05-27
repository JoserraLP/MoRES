package com.example.mqtt.data.remote;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Objects;

public class NearbyDevicesResponseItem {

    private DeviceLocation location;

    public NearbyDevicesResponseItem(DeviceLocation location) {
        this.location = location;
    }

    public DeviceLocation getLocation() {
        return location;
    }

    public void setLocation(DeviceLocation location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NearbyDevicesResponseItem that = (NearbyDevicesResponseItem) o;
        return Objects.equals(location, that.location);
    }

    @NonNull
    @Override
    public String toString() {
        return "NearbyDevicesResponseItem{" +
                "location=" + location +
                '}';
    }

    public class DeviceLocation {

        private ArrayList<Double> coordinates;

        public ArrayList<Double> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(ArrayList<Double> coordinates) {
            this.coordinates = coordinates;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DeviceLocation that = (DeviceLocation) o;
            return Objects.equals(coordinates, that.coordinates);
        }

        @NonNull
        @Override
        public String toString() {
            return "DeviceLocation{" +
                    "coordinates=" + coordinates +
                    '}';
        }
    }

}