package com.unex.tfg.data.repository;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unex.tfg.data.local.AppDatabase;
import com.unex.tfg.data.remote.DeviceIDResponse;
import com.unex.tfg.data.remote.ServerRetrofitClient;
import com.unex.tfg.model.DeviceID;
import com.unex.tfg.utils.Constants;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceIDRepository {

    // TAG for Log messaging
    private static final String TAG = DeviceIDRepository.class.getSimpleName();

    // Application
    private Application application;

    // Singleton class instance
    private static DeviceIDRepository mInstance;

    // Retrofit server
    private ServerRetrofitClient retrofit = ServerRetrofitClient.getInstance();

    // Local database
    private AppDatabase database;

    /**
     * Get the DeviceIDRepository Singleton instance
     * @param application Application context
     * @return DeviceIDRepository Singleton instance
     */
    public static synchronized DeviceIDRepository getInstance(Application application){
        if(mInstance == null){
            mInstance = new DeviceIDRepository(application);
        }
        return mInstance;
    }

    /**
     * DeviceIDRepository constructor
     * @param application Application
     */
    private DeviceIDRepository(Application application) {
        database = AppDatabase.getDatabase(application);
        this.application = application;
    }

    /**
     * Return the device ID stored in the database
     * @return LiveData of a Device ID
     */
    public LiveData<DeviceID> getDeviceID() {
        return database.getDeviceIDDAO().getDeviceID();
    }

    public void loadDeviceID() {
        // Make the retrofit call to the service
        Call<DeviceIDResponse> deviceIDResponseCall = retrofit.getDeviceIDAPI().getDeviceID();

        // Enqueue the response
        deviceIDResponseCall.enqueue(new Callback<DeviceIDResponse>() {
            @Override
            public void onResponse(@NonNull Call<DeviceIDResponse> call, @NonNull Response<DeviceIDResponse> response) {
                if (response.isSuccessful()) {
                    // Retrieve the response body
                    DeviceIDResponse deviceIDResponse = response.body();
                    assert deviceIDResponse != null;
                    // Retrieve the id of the body
                    String id = deviceIDResponse.get_id();
                    // Create DeviceID object
                    DeviceID deviceID = new DeviceID();
                    deviceID.setDeviceID(id);
                    // Insert the device ID in the database
                    insertDeviceID(new MutableLiveData<>(deviceID));


                    // Save the device ID on the preferences of the app
                    SharedPreferences pref = application.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(Constants.PREFERENCES_DEVICE_ID, id);
                    editor.apply();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeviceIDResponse> call, @NonNull Throwable t) {
                Log.d(TAG, Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    /**
     * Insert a DeviceID in the database
     * @param deviceIDLiveData LiveData of the DeviceID object
     */
    private void insertDeviceID(LiveData<DeviceID> deviceIDLiveData) {
        AppDatabase.databaseWriteExecutor.execute(() -> database.getDeviceIDDAO().insert(deviceIDLiveData.getValue()));
    }
}

