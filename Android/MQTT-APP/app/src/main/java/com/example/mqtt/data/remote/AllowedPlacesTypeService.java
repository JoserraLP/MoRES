package com.example.mqtt.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AllowedPlacesService {

    @GET("allowed_places")
    Call<AllowedPlacesResponse>getAllowedPlaces();
}