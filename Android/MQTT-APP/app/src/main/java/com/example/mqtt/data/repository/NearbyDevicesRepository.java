package com.example.mqtt.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mqtt.data.local.AppDatabase;
import com.example.mqtt.data.remote.AllowedPlacesResponse;
import com.example.mqtt.data.remote.AllowedPlacesResponseItem;
import com.example.mqtt.data.remote.NearbyDevicesResponse;
import com.example.mqtt.data.remote.NearbyDevicesResponseItem;
import com.example.mqtt.data.remote.PlacesRetrofitClient;
import com.example.mqtt.data.remote.RetrofitClient;
import com.example.mqtt.model.AllowedPlaces;
import com.example.mqtt.model.NearbyDevice;
import com.example.mqtt.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyDevicesRepository {

    private static final String TAG = NearbyDevicesRepository.class.getSimpleName();

    private static NearbyDevicesRepository mInstance;

    private RetrofitClient retrofit = RetrofitClient.getInstance();


    public static synchronized NearbyDevicesRepository getInstance(){
        if(mInstance == null){
            mInstance = new NearbyDevicesRepository();
        }
        return mInstance;
    }

    private NearbyDevicesRepository() {
    }

    public LiveData<List<NearbyDevice>> getNearbyDevices(double lat, double lng, double radius) {
        Call<NearbyDevicesResponse> nearbyDeviceResponseCall = retrofit.getNearbyDevicesServiceAPI().getNearbyDevices(lat, lng, radius);

        List<NearbyDevice> nearbyDevicesList = new ArrayList<>();
        LiveData<List<NearbyDevice>> nearbyDevices;

        nearbyDeviceResponseCall.enqueue(new Callback<NearbyDevicesResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbyDevicesResponse> call, @NonNull Response<NearbyDevicesResponse> response) {
                if (response.isSuccessful()) {
                    NearbyDevicesResponse nearbyDevicesResponse = response.body();
                    assert nearbyDevicesResponse != null;
                    ArrayList<NearbyDevicesResponseItem> listNearbyDevices = nearbyDevicesResponse.getResults();
                    for (NearbyDevicesResponseItem nearbyDevicesResponseItem : listNearbyDevices) {
                        Log.d(TAG, nearbyDevicesResponseItem.toString());
                        nearbyDevicesList.add(processResponseItem(nearbyDevicesResponseItem));
                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<NearbyDevicesResponse> call, @NonNull Throwable t) {
                Log.d(TAG, Objects.requireNonNull(t.getMessage()));
            }
        });

        nearbyDevices = new MutableLiveData<>(nearbyDevicesList);

        return nearbyDevices;
    }

    private NearbyDevice processResponseItem (NearbyDevicesResponseItem nearbyDevicesResponseItem){
        NearbyDevice nearbyDevice = new NearbyDevice();

        nearbyDevice.setGeo_lat(nearbyDevicesResponseItem.getLocation().getCoordinates().get(0));
        nearbyDevice.setGeo_long(nearbyDevicesResponseItem.getLocation().getCoordinates().get(1));

        return nearbyDevice;
    }

}
