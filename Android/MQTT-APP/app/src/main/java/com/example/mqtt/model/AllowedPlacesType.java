package com.example.mqtt.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AllowedPlacesType {

    @ColumnInfo(name = "type")
    @PrimaryKey
    @NonNull
    private String type;

    public AllowedPlacesType(@NonNull String type) {
        this.type = type;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String toString() {
        return "News item {" +
                " type = " + type +
                '}';
    }

    @Override
    public boolean equals(Object p1){
        if (!p1.getClass().isInstance(AllowedPlacesType.class)) {
            return false;
        } else {
            if (p1 instanceof AllowedPlacesType){
                return ((AllowedPlacesType) p1).getType().equals(this.type);
            }
        }
        return false;
    }
}
