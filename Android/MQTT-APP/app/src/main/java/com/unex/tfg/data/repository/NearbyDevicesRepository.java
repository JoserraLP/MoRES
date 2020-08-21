package com.unex.tfg.data.repository;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unex.tfg.data.remote.NearbyDevicesResponse;
import com.unex.tfg.data.remote.NearbyDevicesResponseItem;
import com.unex.tfg.data.remote.ServerRetrofitClient;
import com.unex.tfg.model.NearbyDevice;
import com.unex.tfg.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearbyDevicesRepository {

    // TAG for Log messaging
    private static final String TAG = NearbyDevicesRepository.class.getSimpleName();

    // Application
    private Application application;

    // Singleton class instance
    private static NearbyDevicesRepository mInstance;

    // Retrofit server
    private ServerRetrofitClient retrofit = ServerRetrofitClient.getInstance();

    // List of nearby devices
    private ArrayList<NearbyDevice> nearbyDevices;

    /**
     * Get the NearbyDevicesRepository Singleton instance
     * @param application Application context
     * @return NearbyDevicesRepository Singleton instance
     */
    public static synchronized NearbyDevicesRepository getInstance(Application application){
        if(mInstance == null){
            mInstance = new NearbyDevicesRepository(application);
        }
        return mInstance;
    }

    /**
     * NearbyDevicesRepository constructor
     * @param application Application
     */
    private NearbyDevicesRepository(Application application){
        nearbyDevices = new ArrayList<>();
        this.application = application;
    }

    /**
     * Return the nearby devices
     * @return Nearby devices
     */
    public LiveData<List<NearbyDevice>> getNearbyDevices(){
        return new MutableLiveData<>(this.nearbyDevices);
    }

    /**
     * Clear the nearby devices list
     */
    private void clearNearbyDevices(){
        this.nearbyDevices.clear();
        Log.d(TAG, "Clearing nearby devices");
    }

    public void loadNearbyDevices(double lat, double lng, double radius, int mins) {
        // Make the retrofit call to the service
        Call<NearbyDevicesResponse> nearbyDeviceResponseCall = retrofit.getNearbyDevicesServiceAPI().getNearbyDevices(lat, lng, radius, mins);

        nearbyDeviceResponseCall.enqueue(new Callback<NearbyDevicesResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbyDevicesResponse> call, @NonNull Response<NearbyDevicesResponse> response) {
                if (response.isSuccessful()) {
                    // Retrieve the response body
                    NearbyDevicesResponse nearbyDevicesResponse = response.body();
                    assert nearbyDevicesResponse != null;
                    // Get the Device ID from the app preferences
                    SharedPreferences pref = application.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE);
                    String deviceID = pref.getString(Constants.PREFERENCES_DEVICE_ID, null);
                    // Retrieve the results of the body
                    ArrayList<NearbyDevicesResponseItem> listNearbyDevices = nearbyDevicesResponse.getResults();
                    if (!listNearbyDevices.isEmpty())
                        for (NearbyDevicesResponseItem nearbyDevicesResponseItem : listNearbyDevices) {
                            if(deviceID != null && !deviceID.equals(nearbyDevicesResponseItem.get_id())) {
                                // If the nearby device is not the current device, insert it on the list
                                insertNearbyDevice(processResponseItem(nearbyDevicesResponseItem));
                            }
                        }
                    else
                        clearNearbyDevices();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NearbyDevicesResponse> call, @NonNull Throwable t) {
                Log.d(TAG, Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    /**
     * Parse a NearbyDevicesResponseItem object to a NearbyDevice object
     * @param nearbyDevicesResponseItem NearbyDevicesResponseItem object
     * @return Parse NearbyDevice object
     */
    private NearbyDevice processResponseItem (NearbyDevicesResponseItem nearbyDevicesResponseItem){
        NearbyDevice nearbyDevice = new NearbyDevice();

        nearbyDevice.setGeo_lat(nearbyDevicesResponseItem.getLocation().getCoordinates().get(0));
        nearbyDevice.setGeo_long(nearbyDevicesResponseItem.getLocation().getCoordinates().get(1));

        return nearbyDevice;
    }

    /**
     * Insert a NearbyDevice in the list
     * @param nearbyDevice NearbyDevice object
     */
    private void insertNearbyDevice(NearbyDevice nearbyDevice){
        this.nearbyDevices.add(nearbyDevice);
        Log.d(TAG, "Added nearby device " + nearbyDevice.toString());
    }

}
