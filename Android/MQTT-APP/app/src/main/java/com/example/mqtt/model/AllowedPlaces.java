package com.example.mqtt.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

@Entity
public class AllowedPlaces {

    @SerializedName("id")
    @Expose
    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @SerializedName("name")
    @Expose
    @ColumnInfo(name = "name")
    private String name;

    @SerializedName("types")
    @Expose
    @ColumnInfo(name = "type")
    private String types;

    @SerializedName("geo_lat")
    @Expose
    @ColumnInfo(name = "geo_lat")
    private double geo_lat;

    @SerializedName("geo_long")
    @Expose
    @ColumnInfo(name = "geo_long")
    private double geo_long;

    @SerializedName("icon")
    @Expose
    @ColumnInfo(name = "icon")
    private String icon;

    public AllowedPlaces() {
    }

    public AllowedPlaces(String id, String name, String types, double geo_lat, double geo_long, String icon) {
        this.id = id;
        this.name = name;
        this.types = types;
        this.geo_lat = geo_lat;
        this.geo_long = geo_long;
        this.icon = icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @NonNull
    @Override
    public String toString() {
        return "AllowedPlaces{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", types='" + types + '\'' +
                ", geo_lat=" + geo_lat +
                ", geo_long=" + geo_long +
                ", icon='" + icon + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllowedPlaces that = (AllowedPlaces) o;
        return Double.compare(that.geo_lat, geo_lat) == 0 &&
                Double.compare(that.geo_long, geo_long) == 0 &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(types, that.types) &&
                Objects.equals(icon, that.icon);
    }

}
