package com.example.mqtt.data.remote;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AllowedPlacesService {

    @GET("around")
    Call<AllowedPlacesResponse>getAllowedPlaces(@Query("at") String location, @Query("size") String size, @Query("app_id") String appID, @Query("app_code") String appCode);
}