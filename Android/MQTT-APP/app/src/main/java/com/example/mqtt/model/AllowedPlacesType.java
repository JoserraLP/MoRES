package com.example.mqtt.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AllowedPlace {

    @ColumnInfo(name = "type")
    @PrimaryKey
    @NonNull
    String type;

    public AllowedPlace(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
