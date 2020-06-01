package com.example.mqtt.data.repository;

import android.app.Application;
import android.content.SharedPreferences;
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

    private ArrayList<NearbyDevice> nearbyDevices;

    private String deviceID;

    private SharedPreferences pref;


    public static synchronized NearbyDevicesRepository getInstance(Application application){
        if(mInstance == null){
            mInstance = new NearbyDevicesRepository(application);
        }
        return mInstance;
    }

    private NearbyDevicesRepository(Application application){
        nearbyDevices = new ArrayList<>();
        pref = application.getSharedPreferences("settings", 0); // 0 - for private mode
    }

    public void loadNearbyDevices(double lat, double lng, double radius) {
        Call<NearbyDevicesResponse> nearbyDeviceResponseCall = retrofit.getNearbyDevicesServiceAPI().getNearbyDevices(lat, lng, radius);


        nearbyDeviceResponseCall.enqueue(new Callback<NearbyDevicesResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbyDevicesResponse> call, @NonNull Response<NearbyDevicesResponse> response) {
                if (response.isSuccessful()) {
                    NearbyDevicesResponse nearbyDevicesResponse = response.body();
                    assert nearbyDevicesResponse != null;
                    ArrayList<NearbyDevicesResponseItem> listNearbyDevices = nearbyDevicesResponse.getResults();
                    deviceID = pref.getString("DeviceID", null);
                    for (NearbyDevicesResponseItem nearbyDevicesResponseItem : listNearbyDevices) {
                        Log.d(TAG, nearbyDevicesResponseItem.get_id());
                        if(deviceID != null && !deviceID.equals(nearbyDevicesResponseItem.get_id()))
                            insertNearbyDevice(processResponseItem(nearbyDevicesResponseItem));
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NearbyDevicesResponse> call, @NonNull Throwable t) {
                Log.d(TAG, Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    private void insertNearbyDevice(NearbyDevice nearbyDevice){
        this.nearbyDevices.add(nearbyDevice);
        Log.d(TAG, "Added nearby device " + nearbyDevice.toString());
    }

    public LiveData<List<NearbyDevice>> getNearbyDevices(){
        return new MutableLiveData<>(this.nearbyDevices);
    }

    private NearbyDevice processResponseItem (NearbyDevicesResponseItem nearbyDevicesResponseItem){
        NearbyDevice nearbyDevice = new NearbyDevice();

        nearbyDevice.setGeo_lat(nearbyDevicesResponseItem.getLocation().getCoordinates().get(0));
        nearbyDevice.setGeo_long(nearbyDevicesResponseItem.getLocation().getCoordinates().get(1));

        return nearbyDevice;
    }

}
