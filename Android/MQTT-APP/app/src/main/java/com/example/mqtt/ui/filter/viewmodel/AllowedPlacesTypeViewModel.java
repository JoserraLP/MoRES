package com.example.mqtt.ui.filter.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mqtt.data.repository.AllowedPlacesTypeRepository;
import com.example.mqtt.model.AllowedPlacesType;

import java.util.List;

public class AllowedPlacesTypeViewModel extends AndroidViewModel {

    private AllowedPlacesTypeRepository mRepository;

    private LiveData<List<AllowedPlacesType>> mAllAllowedPlacesType;

    public AllowedPlacesTypeViewModel(Application application) {
        super(application);
        mRepository = AllowedPlacesTypeRepository.getInstance(application);
        mAllAllowedPlacesType = mRepository.getAllAllowedPlacesTypes();
    }

    public LiveData<List<AllowedPlacesType>> getAllAllowedPlacesType() {
        return mAllAllowedPlacesType;
    }

    public void updateAllowedPlacesTypeChecked(AllowedPlacesType allowedPlacesType){
        mRepository.updateAllowedPlaceType(new MutableLiveData<>(allowedPlacesType));
    }
}