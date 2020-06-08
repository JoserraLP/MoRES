package com.unex.tfg.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AllowedPlacesService {

    /**
     *  GET method to request the allowed places of the Places API
     * @param location String representing latitude and longitude of the position
     * @param size Number of elements to return in the response
     * @param appID ID of the Places API client
     * @param appCode Code of the Places API client
     * @return A Call with the Places API response
     */
    @GET("around")
    Call<AllowedPlacesResponse>getAllowedPlaces(@Query("at") String location, @Query("size") String size, @Query("app_id") String appID, @Query("app_code") String appCode);
}