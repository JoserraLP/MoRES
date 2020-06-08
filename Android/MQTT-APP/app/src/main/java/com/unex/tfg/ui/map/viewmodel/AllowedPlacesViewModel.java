package com.unex.tfg.ui.map.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.unex.tfg.data.repository.AllowedPlacesRepository;
import com.unex.tfg.model.AllowedPlaces;

import java.util.List;

public class AllowedPlacesViewModel extends AndroidViewModel {

    // AllowedPlacesRepository to access the allowed places
    private AllowedPlacesRepository mRepository;

    /**
     * AllowedPlacesViewModel constructor
     * @param application Application
     */
    public AllowedPlacesViewModel(Application application) {
        super(application);
        // Get repository instance
        mRepository = AllowedPlacesRepository.getInstance(application);
    }

    /**
     * Get all the allowed places with a given type
     * @param types Types allowed
     * @return LiveData list with the allowed places
     */
    public LiveData<List<AllowedPlaces>> getAllAllowedPlacesByTypes(List<String> types) {
        return mRepository.getAllAllowedPlacesByTypes(types);
    }
}