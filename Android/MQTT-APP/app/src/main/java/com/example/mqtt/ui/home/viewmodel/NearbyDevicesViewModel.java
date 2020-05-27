package com.example.mqtt.ui.home.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mqtt.data.repository.AllowedPlacesRepository;
import com.example.mqtt.data.repository.NearbyDevicesRepository;
import com.example.mqtt.model.AllowedPlaces;
import com.example.mqtt.model.NearbyDevice;

import java.util.List;

public class NearbyDevicesViewModel extends AndroidViewModel {

    private NearbyDevicesRepository mRepository;

    private LiveData<List<NearbyDevice>> mNearbyDevices;

    public NearbyDevicesViewModel(Application application) {
        super(application);
        mRepository = NearbyDevicesRepository.getInstance();
    }

    public LiveData<List<NearbyDevice>> getNearbyDevices(double lat, double lng, double radius) {
        return mRepository.getNearbyDevices(lat, lng, radius);
    }

}