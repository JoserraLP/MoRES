package com.example.mqtt.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class AllowedPlacesType {

    @ColumnInfo(name = "type")
    @PrimaryKey
    @NonNull
    private String type;

    @ColumnInfo(name = "isChecked")
    private boolean isChecked;

    public AllowedPlacesType(@NonNull String type) {
        this.type = type;
        this.isChecked = false;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AllowedPlacesType that = (AllowedPlacesType) o;
        return isChecked == that.isChecked &&
                type.equals(that.type);
    }

    @NonNull
    @Override
    public String toString() {
        return "AllowedPlacesType{" +
                "type='" + type + '\'' +
                ", isChecked=" + isChecked +
                '}';
    }
}
