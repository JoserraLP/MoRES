package com.example.mqtt.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AllowedPlacesTypeService {

    @GET("allowed_places")
    Call<AllowedPlacesTypeResponse>getAllowedPlacesType();
}