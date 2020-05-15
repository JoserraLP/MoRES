package com.example.mqtt.data.repository;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mqtt.data.local.AppDatabase;
import com.example.mqtt.data.remote.AllowedPlacesTypeResponse;
import com.example.mqtt.data.remote.DeviceIDResponse;
import com.example.mqtt.data.remote.RetrofitClient;
import com.example.mqtt.model.AllowedPlacesType;
import com.example.mqtt.model.DeviceID;
import com.example.mqtt.model.News;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceIDRepository {

    private static final String TAG = DeviceIDRepository.class.getSimpleName();

    private Application application;

    private RetrofitClient retrofit = RetrofitClient.getInstance();

    private static DeviceIDRepository mInstance;

    private AppDatabase database;

    public static synchronized DeviceIDRepository getInstance(Application application){
        if(mInstance == null){
            mInstance = new DeviceIDRepository(application);
        }
        return mInstance;
    }

    private DeviceIDRepository(Application application) {
        database = AppDatabase.getDatabase(application);
        this.application = application;
    }

    public void loadDeviceID() {
        Call<DeviceIDResponse> deviceIDResponseCall = retrofit.getDeviceIDAPI().getDeviceID();

        deviceIDResponseCall.enqueue(new Callback<DeviceIDResponse>() {
            @Override
            public void onResponse(@NonNull Call<DeviceIDResponse> call, @NonNull Response<DeviceIDResponse> response) {
                if (response.isSuccessful()) {
                    DeviceIDResponse deviceIDResponse = response.body();
                    assert deviceIDResponse != null;
                    String id = deviceIDResponse.get_id();
                    DeviceID deviceID = new DeviceID();
                    deviceID.setDeviceID(id);
                    insertDeviceID(new MutableLiveData<>(deviceID));

                    // Save the data on the preferences of the app
                    SharedPreferences pref = application.getSharedPreferences("MyPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("DeviceID", id);
                    editor.apply();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeviceIDResponse> call, @NonNull Throwable t) {
                Log.d(TAG, Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public LiveData<DeviceID> getDeviceID() {
        return database.getDeviceIDDAO().getDeviceID();
    }


    private void insertDeviceID(LiveData<DeviceID> deviceIDLiveData) {
        AppDatabase.databaseWriteExecutor.execute(() -> database.getDeviceIDDAO().insert(deviceIDLiveData.getValue()));
    }
}

