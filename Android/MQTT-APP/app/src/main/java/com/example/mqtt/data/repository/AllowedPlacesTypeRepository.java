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

public class AllowedPlacesTypeRepository {

    private static final String TAG = AllowedPlacesTypeRepository.class.getSimpleName();

    private static AllowedPlacesTypeRepository mInstance;

    private RetrofitClient retrofit = RetrofitClient.getInstance();

    private AppDatabase database;

    private static MutableLiveData<List<AllowedPlacesType>> allAllowedPlacesType = new MutableLiveData<>();


    public static synchronized AllowedPlacesTypeRepository getInstance(Application application){
        if(mInstance == null){
            mInstance = new AllowedPlacesTypeRepository(application);
        }
        return mInstance;
    }

    private AllowedPlacesTypeRepository(Application application) {
        database = AppDatabase.getDatabase(application);
    }

    public void loadAllAllowedPlacesTypes() {
        Call<AllowedPlacesTypeResponse> allowedPlacesTypeResponseCall = retrofit.getAllowedPlacesTypeServiceAPI().getAllowedPlacesType();

        allowedPlacesTypeResponseCall.enqueue(new Callback<AllowedPlacesTypeResponse>() {
            @Override
            public void onResponse(@NonNull Call<AllowedPlacesTypeResponse> call, @NonNull Response<AllowedPlacesTypeResponse> response) {
                if (response.isSuccessful()) {
                    AllowedPlacesTypeResponse allowedPlacesTypeResponse = response.body();
                    assert allowedPlacesTypeResponse != null;
                    ArrayList<AllowedPlacesType> listAllowedPlacesTypes = allowedPlacesTypeResponse.getResults();
                    allAllowedPlacesType.setValue(listAllowedPlacesTypes);
                    for (AllowedPlacesType placeType : listAllowedPlacesTypes)
                        insertAllowedPlaceType(new MutableLiveData<>(placeType));

                }
            }

            @Override
            public void onFailure(@NonNull Call<AllowedPlacesTypeResponse> call, @NonNull Throwable t) {
                Log.d(TAG, Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public LiveData<List<AllowedPlacesType>> getAllAllowedPlacesTypes() {
        return database.getAllowedPlacesTypeDAO().getAll();
    }


    private void insertAllowedPlaceType(LiveData<AllowedPlacesType> allowedPlaceType) {
        AppDatabase.databaseWriteExecutor.execute(() -> database.getAllowedPlacesTypeDAO().insert(allowedPlaceType.getValue()));
    }

}
