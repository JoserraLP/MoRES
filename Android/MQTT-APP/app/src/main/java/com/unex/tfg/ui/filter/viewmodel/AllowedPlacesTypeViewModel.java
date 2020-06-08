package com.unex.tfg.ui.filter.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unex.tfg.data.repository.AllowedPlacesTypeRepository;
import com.unex.tfg.model.AllowedPlacesType;

import java.util.List;

public class AllowedPlacesTypeViewModel extends AndroidViewModel {

    // AllowedPlacesTypeRepository to access the allowed place types
    private AllowedPlacesTypeRepository mRepository;

    // LiveData of the allowed place types
    private LiveData<List<AllowedPlacesType>> mAllAllowedPlacesType;

    /**
     * AllowedPlacesTypeViewModel constructor
     * @param application Application
     */
    public AllowedPlacesTypeViewModel(Application application) {
        super(application);
        // Get repository instance
        mRepository = AllowedPlacesTypeRepository.getInstance(application);
        // Get all allowed place types
        mAllAllowedPlacesType = mRepository.getAllAllowedPlacesTypes();
    }

    /**
     * Getter of all the allowed place types
     * @return LiveData list of all the allowed place types
     */
    public LiveData<List<AllowedPlacesType>> getAllAllowedPlacesType() {
        return mAllAllowedPlacesType;
    }

    /**
     * Update a allowed place type
     * @param allowedPlacesType Updated allowed place type
     */
    public void updateAllowedPlacesTypeChecked(AllowedPlacesType allowedPlacesType){
        mRepository.updateAllowedPlaceType(new MutableLiveData<>(allowedPlacesType));
    }
}