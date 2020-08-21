package com.unex.tfg.data.remote;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Objects;

public class AllowedPlacesTypeResponseItem {

    private String type;

    private String title;

    private String icon;

    /**
     * AllowedPlaces empty constructor
     */
    @SuppressWarnings("unused")
    public AllowedPlacesTypeResponseItem() {
        this.type = "";
        this.title = "";
        this.icon = "";
    }

    /**
     * AllowedPlacesType parametrized constructor
     * @param type AllowedPlacesType type
     * @param title AllowedPlacesType title
     * @param icon AllowedPlacesType icon
     */
    public AllowedPlacesTypeResponseItem(@NonNull String type, @NonNull String title, String icon) {
        this.type = type;
        this.title = title;
        this.icon = icon;
    }

    /**
     * Getter of AllowedPlacesType type
     * @return AllowedPlacesType type
     */
    @NonNull
    public String getType() {
        return type;
    }

    /**
     * Setter of AllowedPlacesType type
     * @param type AllowedPlacesType type
     */
    public void setType(@NonNull String type) {
        this.type = type;
    }

    /**
     * Getter of AllowedPlacesType title
     * @return AllowedPlacesType title
     */
    @NonNull
    public String getTitle() {
        return title;
    }

    /**
     * Setter of AllowedPlacesType title
     * @param title AllowedPlacesType title
     */
    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    /**
     * Getter of AllowedPlacesType icon
     * @return AllowedPlacesType icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Setter of AllowedPlacesType icon
     * @param icon AllowedPlacesType icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * equals method
     * @param o AllowedPlacesType
     * @return true if both objects are equals, false if not
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllowedPlacesTypeResponseItem that = (AllowedPlacesTypeResponseItem) o;
        return type.equals(that.type) &&
                title.equals(that.title) &&
                Objects.equals(icon, that.icon);
    }

    /**
     * toString method
     * @return String representing the object
     */
    @NonNull
    @Override
    public String toString() {
        return "AllowedPlacesType{" +
                "type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }

}
