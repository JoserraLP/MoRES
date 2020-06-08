package com.unex.tfg.data.repository;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unex.tfg.data.local.AppDatabase;
import com.unex.tfg.data.remote.AllowedPlacesResponse;
import com.unex.tfg.data.remote.AllowedPlacesResponseItem;
import com.unex.tfg.data.remote.PlacesRetrofitClient;
import com.unex.tfg.model.AllowedPlaces;
import com.unex.tfg.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllowedPlacesRepository {

    // TAG for Log messaging
    private static final String TAG = AllowedPlacesRepository.class.getSimpleName();

    // Singleton class instance
    private static AllowedPlacesRepository mInstance;

    // Retrofit places API
    private PlacesRetrofitClient retrofit = PlacesRetrofitClient.getInstance();

    // Local database
    private AppDatabase database;

    /**
     * Get the AllowedPlacesRepository Singleton instance
     * @param application Application context
     * @return AllowedPlacesRepository Singleton instance
     */
    public static synchronized AllowedPlacesRepository getInstance(Application application){
        if(mInstance == null){
            mInstance = new AllowedPlacesRepository(application);
        }
        return mInstance;
    }

    /**
     * AllowedPlacesRepository constructor
     * @param application Application
     */
    private AllowedPlacesRepository(Application application) {
        database = AppDatabase.getDatabase(application);
    }

    /**
     * Return all the allowed places stored in the database
     * @return LiveData List of all the allowed places stored in the database
     */
    public LiveData<List<AllowedPlaces>> getAllAllowedPlaces() {
        return database.getAllowedPlacesDAO().getAll();
    }

    /**
     * Return the allowed places of a given type stored in the database
     * @param types Allowed places types to filter
     * @return LiveData List of the allowed places with a given type stored in the database
     */
    public LiveData<List<AllowedPlaces>> getAllAllowedPlacesByTypes(List<String> types) {
        return database.getAllowedPlacesDAO().getAllByTypes(types);
    }

    /**
     * Load the allowed places from the API and stores it in the database
     * @param location Location of the request (lat, lng)
     */
    public void loadAllowedPlacesByLocation(String location) {
        // Make the retrofit call to the service
        Call<AllowedPlacesResponse> allowedPlacesResponseCall = retrofit.getAllowedPlacesServiceAPI().getAllowedPlaces(location, String.valueOf(Constants.SIZE_PLACES_API_REQUEST), Constants.PLACES_API_ID, Constants.PLACES_API_CODE);

        // Enqueue the response
        allowedPlacesResponseCall.enqueue(new Callback<AllowedPlacesResponse>() {
            @Override
            public void onResponse(@NonNull Call<AllowedPlacesResponse> call, @NonNull Response<AllowedPlacesResponse> response) {
                if (response.isSuccessful()) {
                    // Retrieve the response body
                    AllowedPlacesResponse allowedPlacesResponse = response.body();
                    assert allowedPlacesResponse != null;
                    // Retrieve the results of the body
                    AllowedPlacesResponse.Results results = allowedPlacesResponse.getResults();
                    if (results != null){
                        // Retrieve all the items in the results
                        ArrayList<AllowedPlacesResponseItem> listAllowedPlaces = results.getItems();
                        for (AllowedPlacesResponseItem placesResponseItem : listAllowedPlaces) {
                            // Insert the allowed places in the database
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

    /**
     * Parse a AllowedPlacesResponseItem object to a AllowedPlaces object
     * @param allowedPlacesResponseItem AllowedPlacesResponseItem object
     * @return Parsed AllowedPlaces object
     */
    private AllowedPlaces processResponseItem (AllowedPlacesResponseItem allowedPlacesResponseItem){
        AllowedPlaces allowedPlaces = new AllowedPlaces();

        allowedPlaces.setId(allowedPlacesResponseItem.getId());
        allowedPlaces.setName(allowedPlacesResponseItem.getTitle());
        allowedPlaces.setTypes(allowedPlacesResponseItem.getCategory().getId());
        allowedPlaces.setGeo_lat(allowedPlacesResponseItem.getPosition().get(0)); // 0 is Latitude
        allowedPlaces.setGeo_long(allowedPlacesResponseItem.getPosition().get(1)); // 1 is Longitude
        allowedPlaces.setIcon(allowedPlacesResponseItem.getIcon());

        return allowedPlaces;
    }

    /**
     * Insert a AllowedPlaces in the database
     * @param allowedPlace LiveData of the AllowedPlaces object
     */
    private void insertAllowedPlace(LiveData<AllowedPlaces> allowedPlace) {
        AppDatabase.databaseWriteExecutor.execute(() -> database.getAllowedPlacesDAO().insert(allowedPlace.getValue()));
    }

}
