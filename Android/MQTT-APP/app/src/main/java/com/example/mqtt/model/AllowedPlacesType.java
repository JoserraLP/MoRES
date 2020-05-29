package com.example.mqtt.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
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

    public AllowedPlacesType(@NonNull String type, @NonNull String title, String icon) {
        this.type = type;
        this.title = title;
        this.icon = icon;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
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
        return "AllowedPlacesType{" +
                "type='" + type + '\'' +
                ", title='" + title + '\'' +
                ", isChecked=" + isChecked +
                ", icon='" + icon + '\'' +
                '}';
    }

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

}
