package com.example.mqtt.data.remote;

import com.example.mqtt.model.AllowedPlaces;
import com.example.mqtt.model.AllowedPlacesType;

import java.util.ArrayList;

public class AllowedPlacesResponse {

    private Results results;

    public Results getResults() { return results; }

    public void setResults(Results results) {
        this.results = results;
    }

    public class Results {

        private ArrayList<AllowedPlacesResponseItem> items;

        public ArrayList<AllowedPlacesResponseItem> getItems() {
        return items;
    }

    @SuppressWarnings("unused")
        public void setResults(ArrayList<AllowedPlacesResponseItem> items) {
            this.items = items;
        }

    }
}
