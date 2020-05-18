package com.example.mqtt.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Objects;

@Entity
public class AllowedPlaces {

    @SerializedName("id")
    @Expose
    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @SerializedName("name")
    @Expose
    @ColumnInfo(name = "name")
    private String name;

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    @SerializedName("types")
    @Expose
    @ColumnInfo(name = "type") // TODO ask if it is better to have a foreign key
    private ArrayList<String> type;

    public AllowedPlaces(int id, String name, Geometry geometry, ArrayList<String> type) {
        this.id = id;
        this.name = name;
        this.geometry = geometry;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public ArrayList<String> getType() {
        return type;
    }

    public void setType(ArrayList<String> type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        String types = "[";
        for (String typeItem : type)
            types+=typeItem + " ,";
        types.substring(0, types.length()-2);
        types+="]";

        return "News item {" +
                " id = " + id +
                " , name = '" + name + '\'' +
                " , geometry = " + geometry.toString() +
                " , types = " + types +
                '}';
    }

    @Override
    public boolean equals(Object p1){
        if (!p1.getClass().isInstance(AllowedPlaces.class)) {
            return false;
        } else {
            if (p1 instanceof AllowedPlaces){
                return ((AllowedPlaces) p1).getId() == (this.id) &&
                        ((AllowedPlaces) p1).getName().equals(this.name) &&
                        ((AllowedPlaces) p1).getGeometry().equals(this.geometry) &&
                        ((AllowedPlaces) p1).getType().equals(this.type);
            }
        }
        return false;
    }

    private class Geometry {

        @SerializedName("location")
        @Expose
        private Location location;

        private class Location {
            @SerializedName("lat")
            @Expose
            @ColumnInfo(name = "geoLat")
            private double geoLat;

            @SerializedName("lng")
            @Expose
            @ColumnInfo(name = "geoLong")
            private double geoLong;

            public double getGeoLat() {
                return geoLat;
            }

            public void setGeoLat(double geoLat) {
                this.geoLat = geoLat;
            }

            public double getGeoLong() {
                return geoLong;
            }

            public void setGeoLong(double geoLong) {
                this.geoLong = geoLong;
            }

            @Override
            public String toString() {
                return "Location{" +
                        "geoLat=" + geoLat +
                        ", geoLong=" + geoLong +
                        '}';
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                Location location = (Location) o;
                return Double.compare(location.geoLat, geoLat) == 0 &&
                        Double.compare(location.geoLong, geoLong) == 0;
            }

        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        @Override
        public String toString() {
            return "Geometry{" +
                    "location=" + location.toString() +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Geometry geometry = (Geometry) o;
            return Objects.equals(location, geometry.location);
        }

    }


}
