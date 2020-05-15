package com.example.mqtt.data.remote;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DeviceIDService {

    @POST("device_id")
    Call<DeviceIDResponse> getDeviceID();
}