package com.unex.tfg.data.remote;

import java.util.ArrayList;

public class NearbyDevicesResponse {

    // Results array of NearbyDevicesResponseItem
    private ArrayList<NearbyDevicesResponseItem> results;

    /**
     * Return all the NearbyDevicesResponseItem of the response
     * @return A ArrayList of NearbyDevicesResponseItem
     */
    public ArrayList<NearbyDevicesResponseItem> getResults() { return results; }

    /**
     * Set the results of the response
     * @param results ArrayList of NearbyDevicesResponseItem
     */
    @SuppressWarnings("unused")
    public void setResults(ArrayList<NearbyDevicesResponseItem> results) {
        this.results = results;
    }

}
