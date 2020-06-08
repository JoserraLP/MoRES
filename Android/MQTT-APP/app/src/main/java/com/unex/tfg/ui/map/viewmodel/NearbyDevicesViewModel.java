package com.unex.tfg.ui.map.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.unex.tfg.data.repository.NearbyDevicesRepository;
import com.unex.tfg.model.NearbyDevice;

import java.util.List;

public class NearbyDevicesViewModel extends AndroidViewModel {

    // NearbyDevicesRepository to access the nearby devices
    private NearbyDevicesRepository mRepository;

    /**
     * NearbyDevicesViewModel constructor
     * @param application Application
     */
    public NearbyDevicesViewModel(Application application) {
        super(application);
        // Get the repository instance
        mRepository = NearbyDevicesRepository.getInstance(application);
    }

    /**
     * Get the nearby devices
     * @return LiveData list with the nearby devices
     */
    public LiveData<List<NearbyDevice>> getNearbyDevices() {
        return mRepository.getNearbyDevices();
    }

}