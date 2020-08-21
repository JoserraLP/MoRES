package com.unex.tfg.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NearbyDevicesService {


    /**
     * GET method to request the allowed places types of the server
     * @param lat Center latitude
     * @param lng Center longitude
     * @param rad Radius
     * @param mins Minutes
     * @return A Call with the server response
     */
    @GET("nearby_devices")
    Call<NearbyDevicesResponse>getNearbyDevices(@Query("lat") double lat, @Query("lng") double lng, @Query("rad") double rad, @Query("mins") int mins);
}