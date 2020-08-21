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
public class AllowedPlacesType {

    @ColumnInfo(name = "type")
    @PrimaryKey
    @NonNull
    private String type;

    @ColumnInfo(name = "title")
    @NonNull
    private String title;

    @ColumnInfo(name = "isChecked")
    private boolean isChecked;

    @SerializedName("icon")
    @Expose
    @ColumnInfo(name = "icon")
    private String icon;

    /**
     * AllowedPlaces empty constructor
     */
    @SuppressWarnings("unused")
    public AllowedPlacesType() {
        this.type = "";
        this.title = "";
        this.isChecked = false;
        this.icon = "";
    }

    /**
     * AllowedPlacesType parametrized constructor
     * @param type AllowedPlacesType type
     * @param title AllowedPlacesType title
     * @param icon AllowedPlacesType icon
     * @param locality AllowedPlacesType locality
     */
    @Ignore
    public AllowedPlacesType(@NonNull String type, @NonNull String title, String icon, String locality) {
        this.type = type;
        this.title = title;
        this.isChecked = false;
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
     * Getter of AllowedPlacesType isChecked status
     * @return AllowedPlacesType isChecked status
     */
    public boolean isChecked() {
        return isChecked;
    }

    /**
     * Setter of AllowedPlacesType isChecked status
     * @param checked AllowedPlacesType isChecked status
     */
    public void setChecked(boolean checked) {
        isChecked = checked;
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
        AllowedPlacesType that = (AllowedPlacesType) o;
        return isChecked == that.isChecked &&
                type.equals(that.type) &&
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
                ", isChecked=" + isChecked +
                ", icon='" + icon + '\'' +
                '}';
    }

}
