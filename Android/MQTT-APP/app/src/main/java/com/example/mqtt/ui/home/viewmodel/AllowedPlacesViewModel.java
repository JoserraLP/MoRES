package com.example.mqtt.ui.home.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mqtt.data.repository.AllowedPlacesRepository;
import com.example.mqtt.model.AllowedPlaces;

import java.util.List;

public class AllowedPlacesViewModel extends AndroidViewModel {

    private AllowedPlacesRepository mRepository;

    private LiveData<List<AllowedPlaces>> mAllAllowedPlaces;

    public AllowedPlacesViewModel(Application application) {
        super(application);
        mRepository = AllowedPlacesRepository.getInstance(application);
        mAllAllowedPlaces = mRepository.getAllAllowedPlaces();
    }

    public LiveData<List<AllowedPlaces>> getAllAllowedPlaces() {
        return mAllAllowedPlaces;
    }
}