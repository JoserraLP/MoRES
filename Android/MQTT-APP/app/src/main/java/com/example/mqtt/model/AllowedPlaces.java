package com.example.mqtt.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AllowedPlaces {

    @PrimaryKey
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "geoLat")
    private double geoLat;

    @ColumnInfo(name = "geoLong")
    private double geoLong;

    @ColumnInfo(name = "type") // TODO ask if it is better to have a foreign key
    private String type;

    public AllowedPlaces(int id, String name, double geoLat, double geoLong, String type) {
        this.id = id;
        this.name = name;
        this.geoLat = geoLat;
        this.geoLong = geoLong;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return "News item {" +
                " id = " + id +
                " , name = '" + name + '\'' +
                " , geoLat = '" + geoLat + '\'' +
                " , geoLong = " + geoLong +
                " , type = '" + type + '\'' +
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
                        ((AllowedPlaces) p1).getGeoLat() == (this.geoLat) &&
                        ((AllowedPlaces) p1).getGeoLong() == (this.geoLong) &&
                        ((AllowedPlaces) p1).getType().equals(this.type);
            }
        }
        return false;
    }


}
