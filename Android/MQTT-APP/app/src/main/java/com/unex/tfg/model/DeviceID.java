package com.unex.tfg.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class DeviceID {

    @PrimaryKey
    @NonNull
    private String deviceID;

    /**
     * DeviceID empty constructor
     */
    public DeviceID() {
        deviceID = "";
    }

    /**
     * DeviceID parametrized constructor
     * @param deviceID Device ID
     */
    @Ignore
    public DeviceID(@NonNull String deviceID) {
        this.deviceID = deviceID;
    }

    /**
     * Getter of Device ID
     * @return Device ID
     */
    @NonNull
    public String getDeviceID() {
        return deviceID;
    }

    /**
     * Setter of Device ID
     * @param deviceID Device ID
     */
    public void setDeviceID(@NonNull String deviceID) {
        this.deviceID = deviceID;
    }

    /**
     * equals method
     * @param p1 DeviceID
     * @return true if both objects are equals, false if not
     */
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

    /**
     * toString method
     * @return String representing the object
     */
    @NonNull
    @Override
    public String toString() {
        return "Device ID {" +
                " deviceID = " + deviceID +
                '}';
    }
}
