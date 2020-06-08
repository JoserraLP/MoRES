package com.unex.tfg.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AllowedPlacesTypeService {

    /**
     * GET method to request the allowed places types of the server
     * @return A Call with the server response
     */
    @GET("allowed_places_types")
    Call<AllowedPlacesTypeResponse>getAllowedPlacesType();
}