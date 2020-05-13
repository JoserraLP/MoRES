package com.example.mqtt.ui.home.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mqtt.data.repository.AllowedPlacesTypeRepository;
import com.example.mqtt.model.AllowedPlacesType;

import java.util.List;

public class AllowedPlacesTypeViewModel extends AndroidViewModel {

    private AllowedPlacesTypeRepository mRepository;

    private LiveData<List<AllowedPlacesType>> mAllAllowedPlaces;

    public AllowedPlacesTypeViewModel(Application application) {
        super(application);
        mRepository = AllowedPlacesTypeRepository.getInstance(application);
        mAllAllowedPlaces = mRepository.getAllAllowedPlaces();
    }

    public LiveData<List<AllowedPlacesType>> getAllAllowedPlaces() {
        return mAllAllowedPlaces;
    }
}