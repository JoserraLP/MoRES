package com.unex.tfg.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AllowedPlacesTypeService {

    //     * @param location_type String representing location type
    //     * @param location String representing location name

    /**
     * GET method to request the allowed places types of the server

     * @return A Call with the server response
     */
    @GET("allowed_places_types")
    Call<AllowedPlacesTypeResponse>getAllowedPlacesType(@Query("location_type") String location_type, @Query("location") String location);
}