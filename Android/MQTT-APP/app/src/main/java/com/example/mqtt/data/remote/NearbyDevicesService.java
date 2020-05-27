package com.example.mqtt.data.remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NearbyDevicesService {

    @GET("nearby_devices")
    Call<NearbyDevicesResponse>getNearbyDevices(@Query("lat") double lat, @Query("lng") double lng, @Query("rad") double rad);
}