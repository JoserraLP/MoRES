package com.example.mqtt.data.remote;

import com.example.mqtt.model.AllowedPlacesType;
import com.example.mqtt.model.DeviceID;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DeviceIDResponse {

    private String _id;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
