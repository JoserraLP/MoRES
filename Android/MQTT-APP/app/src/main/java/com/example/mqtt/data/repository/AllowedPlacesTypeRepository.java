package com.example.mqtt.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mqtt.data.local.AppDatabase;
import com.example.mqtt.data.remote.AllowedPlacesTypeResponse;
import com.example.mqtt.data.remote.RetrofitClient;
import com.example.mqtt.model.AllowedPlacesType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllowedPlaceRepository {

    private static final String TAG = AllowedPlaceRepository.class.getSimpleName();

    private static AllowedPlaceRepository mInstance;

    private RetrofitClient retrofit = RetrofitClient.getInstance();

    private AppDatabase database;

    private static MutableLiveData<List<AllowedPlacesType>> allAllowedPlaces = new MutableLiveData<>();


    public static synchronized AllowedPlaceRepository getInstance(Application application){
        if(mInstance == null){
            mInstance = new AllowedPlaceRepository(application);
        }
        return mInstance;
    }

    private AllowedPlaceRepository(Application application) {
        database = AppDatabase.getDatabase(application);
    }

    public void loadAllAllowedPlaces() {
        Call<AllowedPlacesTypeResponse> allowedPlacesResponseCall = retrofit.getAPI().getAllowedPlacesType();

        allowedPlacesResponseCall.enqueue(new Callback<AllowedPlacesTypeResponse>() {
            @Override
            public void onResponse(@NonNull Call<AllowedPlacesTypeResponse> call, @NonNull Response<AllowedPlacesTypeResponse> response) {
                if (response.isSuccessful()) {
                    AllowedPlacesTypeResponse allowedPlacesTypeResponse = response.body();
                    assert allowedPlacesTypeResponse != null;
                    ArrayList<AllowedPlacesType> listAllowedPlacesTypes = allowedPlacesTypeResponse.getResults();
                    allAllowedPlaces.setValue(listAllowedPlacesTypes);
                    for (AllowedPlacesType place : listAllowedPlacesTypes)
                        insertAllowedPlace(new MutableLiveData<>(place));

                }
            }

            @Override
            public void onFailure(@NonNull Call<AllowedPlacesTypeResponse> call, @NonNull Throwable t) {
                Log.d(TAG, Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public LiveData<List<AllowedPlacesType>> getAllAllowedPlaces() {
        return database.getAllowedPlacesTypeDAO().getAll();
    }


    private void insertAllowedPlace(LiveData<AllowedPlacesType> allowedPlace) {
        AppDatabase.databaseWriteExecutor.execute(() -> database.getAllowedPlacesTypeDAO().insert(allowedPlace.getValue()));
    }

}
