package com.example.mqtt.data.remote;

import java.util.ArrayList;

public class NearbyDevicesResponse {

    private ArrayList<NearbyDevicesResponseItem> results;

    public ArrayList<NearbyDevicesResponseItem> getResults() { return results; }

    public void setResults(ArrayList<NearbyDevicesResponseItem> results) {
        this.results = results;
    }

}
