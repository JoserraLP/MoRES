package com.example.mqtt.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DeviceID {

    @PrimaryKey
    @NonNull
    private String deviceID;

    public DeviceID() {
        deviceID = "";
    }

    @NonNull
    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(@NonNull String deviceID) {
        this.deviceID = deviceID;
    }

    @NonNull
    @Override
    public String toString() {
        return "Device ID {" +
                " deviceID = " + deviceID +
                '}';
    }

    @Override
    public boolean equals(Object p1){
        if (!p1.getClass().isInstance(DeviceID.class)) {
            return false;
        } else {
            if (p1 instanceof DeviceID){
                return ((DeviceID) p1).getDeviceID().equals(this.deviceID);
            }
        }
        return false;
    }
}
