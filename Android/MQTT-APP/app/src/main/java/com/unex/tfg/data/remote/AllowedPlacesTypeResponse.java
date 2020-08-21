package com.unex.tfg.data.remote;

import com.unex.tfg.model.AllowedPlacesType;

import java.util.ArrayList;

public class AllowedPlacesTypeResponse {

    // Results array of NearbyDevicesResponseItem
    private ArrayList<AllowedPlacesTypeResponseItem> results;

    /**
     * Return all the AllowedPlacesTypeResponseItem of the response
     * @return A ArrayList of AllowedPlacesTypeResponseItem
     */
    public ArrayList<AllowedPlacesTypeResponseItem> getResults() { return results; }

    /**
     * Set the results of the response
     * @param results ArrayList of AllowedPlacesTypeResponseItem
     */
    @SuppressWarnings("unused")
    public void setResults(ArrayList<AllowedPlacesTypeResponseItem> results) {
        this.results = results;
    }
}
