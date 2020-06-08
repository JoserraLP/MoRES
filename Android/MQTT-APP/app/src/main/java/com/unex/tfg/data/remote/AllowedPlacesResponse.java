package com.unex.tfg.data.remote;

import java.util.ArrayList;

public class AllowedPlacesResponse {

    // Results array of Results
    private Results results;

    /**
     * Return all the Results of the response
     * @return A Result class
     */
    public Results getResults() { return results; }

    /**
     * Set the results of the response
     * @param results Result class
     */
    @SuppressWarnings("unused")
    public void setResults(Results results) {
        this.results = results;
    }

    public static class Results {

        // Results array of AllowedPlacesResponseItem
        private ArrayList<AllowedPlacesResponseItem> items;

        /**
         * Return all the AllowedPlacesResponseItem of the response
         * @return A ArrayList of AllowedPlacesResponseItem
         */
        public ArrayList<AllowedPlacesResponseItem> getItems() {
            return items;
        }

        /**
         * Set the items (AllowedPlacesResponseItem) of the results
         * @param items ArrayList of AllowedPlacesResponseItem
         */
        @SuppressWarnings("unused")
        public void setResults(ArrayList<AllowedPlacesResponseItem> items) {
            this.items = items;
        }

    }
}
