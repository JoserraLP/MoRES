package com.example.mqtt.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mqtt.data.local.AppDatabase;
import com.example.mqtt.data.remote.AllowedPlacesResponse;
import com.example.mqtt.data.remote.AllowedPlacesResponseItem;
import com.example.mqtt.data.remote.PlacesRetrofitClient;
import com.example.mqtt.model.AllowedPlaces;
import com.example.mqtt.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllowedPlacesRepository {

    private static final String TAG = AllowedPlacesRepository.class.getSimpleName();

    private static AllowedPlacesRepository mInstance;

    private PlacesRetrofitClient retrofit = PlacesRetrofitClient.getInstance();

    private AppDatabase database;


    public static synchronized AllowedPlacesRepository getInstance(Application application){
        if(mInstance == null){
            mInstance = new AllowedPlacesRepository(application);
        }
        return mInstance;
    }

    private AllowedPlacesRepository(Application application) {
        database = AppDatabase.getDatabase(application);
    }

    public void loadAllowedPlacesByLocation(String location) {

        Call<AllowedPlacesResponse> allowedPlacesResponseCall = retrofit.getAllowedPlacesServiceAPI().getAllowedPlaces(location, String.valueOf(Constants.SIZE_PLACES_API_REQUEST), Constants.PLACES_API_ID, Constants.PLACES_API_CODE); // TODO make the call to the server

        allowedPlacesResponseCall.enqueue(new Callback<AllowedPlacesResponse>() {
            @Override
            public void onResponse(@NonNull Call<AllowedPlacesResponse> call, @NonNull Response<AllowedPlacesResponse> response) {
                if (response.isSuccessful()) {
                    AllowedPlacesResponse allowedPlacesResponse = response.body();
                    assert allowedPlacesResponse != null;
                    AllowedPlacesResponse.Results results = allowedPlacesResponse.getResults();
                    if (results != null){
                        ArrayList<AllowedPlacesResponseItem> listAllowedPlaces = results.getItems();
                        for (AllowedPlacesResponseItem placesResponseItem : listAllowedPlaces) {
                            insertAllowedPlace(new MutableLiveData<>(processResponseItem(placesResponseItem)));
                        }
                    }


                }
            }

            @Override
            public void onFailure(@NonNull Call<AllowedPlacesResponse> call, @NonNull Throwable t) {
                Log.d(TAG, Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    public LiveData<List<AllowedPlaces>> getAllAllowedPlaces() {
        return database.getAllowedPlacesDAO().getAll();
    }

    public LiveData<List<AllowedPlaces>> getAllAllowedPlacesByTypes(List<String> types) {
        return database.getAllowedPlacesDAO().getAllByTypes(types);
    }

    private void insertAllowedPlace(LiveData<AllowedPlaces> allowedPlace) {
        AppDatabase.databaseWriteExecutor.execute(() -> database.getAllowedPlacesDAO().insert(allowedPlace.getValue()));
    }

    private AllowedPlaces processResponseItem (AllowedPlacesResponseItem allowedPlacesResponseItem){
        AllowedPlaces allowedPlaces = new AllowedPlaces();

        allowedPlaces.setId(allowedPlacesResponseItem.getId());
        allowedPlaces.setName(allowedPlacesResponseItem.getTitle());
        allowedPlaces.setTypes(allowedPlacesResponseItem.getCategory().getId());
        allowedPlaces.setGeo_lat(allowedPlacesResponseItem.getPosition().get(0));
        allowedPlaces.setGeo_long(allowedPlacesResponseItem.getPosition().get(1));
        allowedPlaces.setIcon(allowedPlacesResponseItem.getIcon());

        return allowedPlaces;
    }

}
