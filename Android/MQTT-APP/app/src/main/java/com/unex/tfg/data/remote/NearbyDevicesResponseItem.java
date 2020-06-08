package com.unex.tfg.data.remote;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Objects;

public class NearbyDevicesResponseItem {

    // Nearby device ID
    private String _id;

    // Device Location
    private DeviceLocation location;

    /**
     * NearbyDevicesResponseItem constructor
     * @param _id Nearby device ID
     * @param location Nearby device location
     */
    public NearbyDevicesResponseItem(String _id, DeviceLocation location) {
        this._id = _id;
        this.location = location;
    }

    /**
     * Getter of Nearby device ID
     * @return Nearby device ID
     */
    public String get_id() {
        return _id;
    }

    /**
     * Setter of Nearby device ID
     * @param _id Nearby device ID
     */
    @SuppressWarnings("unused")
    public void set_id(String _id) {
        this._id = _id;
    }

    /**
     * Getter of Nearby device location
     * @return Nearby device location
     */
    public DeviceLocation getLocation() {
        return location;
    }

    /**
     * Setter of Nearby device location
     * @param location Nearby device location
     */
    public void setLocation(DeviceLocation location) {
        this.location = location;
    }

    /**
     * equals method
     * @param o NearbyDevicesResponseItem
     * @return true if both objects are equals, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NearbyDevicesResponseItem that = (NearbyDevicesResponseItem) o;
        return Objects.equals(_id, that._id) &&
                Objects.equals(location, that.location);
    }

    /**
     * toString method
     * @return String representing the object
     */
    @NonNull
    @Override
    public String toString() {
        return "NearbyDevicesResponseItem{" +
                "_id='" + _id + '\'' +
                ", location=" + location +
                '}';
    }

    public static class DeviceLocation {

        // Coordinates of the device location
        private ArrayList<Double> coordinates;

        /**
         * Getter of the coordinates
         * @return ArrayList of coordinates
         */
        public ArrayList<Double> getCoordinates() {
            return coordinates;
        }

        /**
         * Setter of the coordinates
         * @param coordinates Coordinates of the location
         */
        @SuppressWarnings("unused")
        public void setCoordinates(ArrayList<Double> coordinates) {
            this.coordinates = coordinates;
        }

        /**
         * equals method
         * @param o Device location
         * @return true if both objects are equals, false if not
         */
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            DeviceLocation that = (DeviceLocation) o;
            return Objects.equals(coordinates, that.coordinates);
        }

        /**
         * toString method
         * @return String representing the object
         */
        @NonNull
        @Override
        public String toString() {
            return "DeviceLocation{" +
                    "coordinates=" + coordinates +
                    '}';
        }
    }

}
