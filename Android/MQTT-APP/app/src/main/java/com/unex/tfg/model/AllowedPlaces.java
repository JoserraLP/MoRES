package com.unex.tfg.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
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
    @NonNull
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

    /**
     * AllowedPlaces empty constructor
     */
    public AllowedPlaces() {
        this.id = "";
        this.name = "";
        this.types = "";
        this.geo_lat = 0.0d;
        this.geo_long = 0.0d;
        this.icon = "";
    }

    /**
     * AllowedPlaces parametrized constructor
     * @param id Allowed Places ID
     * @param name Allowed Places name
     * @param types Allowed Places type
     * @param geo_lat Allowed Places latitude
     * @param geo_long Allowed Places  longitude
     * @param icon Allowed Places icon
     */
    @Ignore
    public AllowedPlaces(@NonNull String id, String name, String types, double geo_lat, double geo_long, String icon) {
        this.id = id;
        this.name = name;
        this.types = types;
        this.geo_lat = geo_lat;
        this.geo_long = geo_long;
        this.icon = icon;
    }

    /**
     * Getter of AllowedPlaces ID
     * @return AllowedPlaces ID
     */
    @NonNull
    public String getId() {
        return id;
    }

    /**
     * Setter of AllowedPlaces ID
     * @param id AllowedPlaces ID
     */
    public void setId(@NonNull String id) {
        this.id = id;
    }

    /**
     * Getter of AllowedPlaces name
     * @return AllowedPlaces name
     */
    public String getName() {
        return name;
    }


    /**
     * Setter of AllowedPlaces name
     * @param name AllowedPlaces name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter of AllowedPlaces types
     * @return AllowedPlaces types
     */
    public String getTypes() {
        return types;
    }


    /**
     * Setter of AllowedPlaces types
     * @param types AllowedPlaces types
     */
    public void setTypes(String types) {
        this.types = types;
    }

    /**
     * Getter of AllowedPlaces latitude
     * @return AllowedPlaces latitude
     */
    public double getGeo_lat() {
        return geo_lat;
    }


    /**
     * Setter of AllowedPlaces latitude
     * @param geo_lat AllowedPlaces latitude
     */
    public void setGeo_lat(double geo_lat) {
        this.geo_lat = geo_lat;
    }

    /**
     * Getter of AllowedPlaces longitude
     * @return AllowedPlaces longitude
     */
    public double getGeo_long() {
        return geo_long;
    }


    /**
     * Setter of AllowedPlaces longitude
     * @param geo_long AllowedPlaces longitude
     */
    public void setGeo_long(double geo_long) {
        this.geo_long = geo_long;
    }

    /**
     * Getter of AllowedPlaces icon
     * @return AllowedPlaces icon
     */
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * equals method
     * @param o AllowedPlaces
     * @return true if both objects are equals, false if not
     */
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

    /**
     * toString method
     * @return String representing the object
     */
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

}
