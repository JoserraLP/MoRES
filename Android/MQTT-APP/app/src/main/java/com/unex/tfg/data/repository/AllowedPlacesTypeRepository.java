package com.unex.tfg.data.repository;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unex.tfg.data.local.AppDatabase;
import com.unex.tfg.data.remote.AllowedPlacesTypeResponse;
import com.unex.tfg.data.remote.AllowedPlacesTypeResponseItem;
import com.unex.tfg.data.remote.ServerRetrofitClient;
import com.unex.tfg.model.AllowedPlacesType;
import com.unex.tfg.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllowedPlacesTypeRepository {

    // TAG for Log messaging
    private static final String TAG = AllowedPlacesTypeRepository.class.getSimpleName();

    // Singleton class instance
    private static AllowedPlacesTypeRepository mInstance;

    // Retrofit server
    private ServerRetrofitClient retrofit = ServerRetrofitClient.getInstance();

    // Local database
    private AppDatabase database;

    /**
     * Get the AllowedPlacesTypeRepository Singleton instance
     * @param application Application context
     * @return AllowedPlacesTypeRepository Singleton instance
     */
    public static synchronized AllowedPlacesTypeRepository getInstance(Application application){
        if(mInstance == null){
            mInstance = new AllowedPlacesTypeRepository(application);
        }
        return mInstance;
    }

    /**
     * AllowedPlacesTypeRepository constructor
     * @param application Application
     */
    private AllowedPlacesTypeRepository(Application application) {
        database = AppDatabase.getDatabase(application);
    }

    /**
     * Return all the allowed places types stored in the database
     * @return LiveData List of all the allowed places types stored in the database
     */
    public LiveData<List<AllowedPlacesType>> getAllAllowedPlacesTypes() {
        return database.getAllowedPlacesTypeDAO().getAll();
    }

    /**
     * Load the allowed places types from the API and stores it in the database
     */
    public void loadAllAllowedPlacesTypes(String curLocality) {

        // Make the retrofit call to the service
        Call<AllowedPlacesTypeResponse> allowedPlacesTypeResponseCall = retrofit.getAllowedPlacesTypeServiceAPI().getAllowedPlacesType(Constants.LOCATION_TYPE, curLocality);

        // Enqueue the response
        allowedPlacesTypeResponseCall.enqueue(new Callback<AllowedPlacesTypeResponse>() {
            @Override
            public void onResponse(@NonNull Call<AllowedPlacesTypeResponse> call, @NonNull Response<AllowedPlacesTypeResponse> response) {
                if (response.isSuccessful()) {
                    // Delete previous elements
                    deleteAllAllowedPlacesTypes();

                    // Retrieve the response body
                    AllowedPlacesTypeResponse allowedPlacesTypeResponse = response.body();
                    assert allowedPlacesTypeResponse != null;
                    // Retrieve the results of the body
                    ArrayList<AllowedPlacesTypeResponseItem> listAllowedPlacesType = allowedPlacesTypeResponse.getResults();
                    if (!listAllowedPlacesType.isEmpty()) {
                        for (AllowedPlacesTypeResponseItem allowedPlacesTypeResponseItem : listAllowedPlacesType) {
                            AllowedPlacesType parsedPlaceType = processResponseItem(allowedPlacesTypeResponseItem);
                            // Insert the allowed place type on the database
                            insertAllowedPlaceType(new MutableLiveData<>(parsedPlaceType));
                        }
                    }
                }
            }


            @Override
            public void onFailure(@NonNull Call<AllowedPlacesTypeResponse> call, @NonNull Throwable t) {
                Log.d(TAG, Objects.requireNonNull(t.getMessage()));
            }
        });
    }

    /**
     * Parse a AllowedPlacesTypeResponseItem object to a AllowedPlacesType object
     * @param allowedPlacesTypeResponseItem AllowedPlacesTypeResponseItem object
     * @return Parsed AllowedPlacesType object
     */
    private AllowedPlacesType processResponseItem (AllowedPlacesTypeResponseItem allowedPlacesTypeResponseItem){
        AllowedPlacesType allowedPlacesType = new AllowedPlacesType();

        allowedPlacesType.setChecked(true);
        allowedPlacesType.setIcon(allowedPlacesTypeResponseItem.getIcon());
        allowedPlacesType.setTitle(allowedPlacesTypeResponseItem.getTitle());
        allowedPlacesType.setType(allowedPlacesTypeResponseItem.getType());

        return allowedPlacesType;
    }

    /**
     * Insert a AllowedPlacesType in the database
     * @param allowedPlaceType LiveData of the AllowedPlacesType object
     */
    private void insertAllowedPlaceType(LiveData<AllowedPlacesType> allowedPlaceType) {
        AppDatabase.databaseWriteExecutor.execute(() -> database.getAllowedPlacesTypeDAO().insert(allowedPlaceType.getValue()));
    }

    /**
     * Update a AllowedPlacesType in the database
     * @param allowedPlaceType LiveData of the AllowedPlacesType object
     */
    public void updateAllowedPlaceType(LiveData<AllowedPlacesType> allowedPlaceType){
        AppDatabase.databaseWriteExecutor.execute(() -> database.getAllowedPlacesTypeDAO().update(allowedPlaceType.getValue()));
    }

    /**
     * Delete all the allowed places types stored in the database
     */
    private void deleteAllAllowedPlacesTypes() {
        AppDatabase.databaseWriteExecutor.execute(() -> database.getAllowedPlacesTypeDAO().deleteAll());
    }

}
