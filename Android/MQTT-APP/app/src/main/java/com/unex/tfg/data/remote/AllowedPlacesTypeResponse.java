package com.unex.tfg.data.remote;

import com.unex.tfg.model.AllowedPlacesType;

import java.util.ArrayList;

public class AllowedPlacesTypeResponse {

    // Results array of AllowedPlacesType
    private ArrayList<AllowedPlacesType> results;

    /**
     * Return all the AllowedPlacesType of the response
     * @return A ArrayList of AllowedPlacesType
     */
    public ArrayList<AllowedPlacesType> getResults() {
        return results;
    }

    /**
     * Set the results of the response
     * @param results ArrayList of AllowedPlacesType
     */
    @SuppressWarnings("unused")
    public void setResults(ArrayList<AllowedPlacesType> results) {
        this.results = results;
    }
}
