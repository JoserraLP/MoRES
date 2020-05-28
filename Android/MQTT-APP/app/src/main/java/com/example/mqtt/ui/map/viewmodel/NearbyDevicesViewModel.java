package com.example.mqtt.ui.map.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mqtt.data.repository.NearbyDevicesRepository;
import com.example.mqtt.model.NearbyDevice;

import java.util.List;

public class NearbyDevicesViewModel extends AndroidViewModel {

    private NearbyDevicesRepository mRepository;

    public NearbyDevicesViewModel(Application application) {
        super(application);
        mRepository = NearbyDevicesRepository.getInstance(application);
    }

    public LiveData<List<NearbyDevice>> getNearbyDevices() {
        return mRepository.getNearbyDevices();
    }

}