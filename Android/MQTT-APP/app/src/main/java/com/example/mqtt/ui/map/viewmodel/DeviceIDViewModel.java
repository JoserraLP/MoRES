package com.example.mqtt.ui.map.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.mqtt.data.repository.DeviceIDRepository;
import com.example.mqtt.model.DeviceID;

public class DeviceIDViewModel extends AndroidViewModel {

    private DeviceIDRepository mRepository;

    private LiveData<DeviceID> deviceID;

    public DeviceIDViewModel(Application application) {
        super(application);
        mRepository = DeviceIDRepository.getInstance(application);
        deviceID = mRepository.getDeviceID();
    }

    public LiveData<DeviceID> getDeviceID() {
        return deviceID;
    }
}
