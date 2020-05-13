package com.example.mqtt.data.remote;

import com.example.mqtt.model.AllowedPlacesType;

import java.util.ArrayList;

public class AllowedPlacesTypeResponse {

    private ArrayList<AllowedPlacesType> results;

    public ArrayList<AllowedPlacesType> getResults() {
        return results;
    }

    @SuppressWarnings("unused")
    public void setResults(ArrayList<AllowedPlacesType> results) {
        this.results = results;
    }
}
