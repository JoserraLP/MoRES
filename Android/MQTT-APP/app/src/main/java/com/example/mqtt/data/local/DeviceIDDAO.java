package com.example.mqtt.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mqtt.model.AllowedPlaces;
import com.example.mqtt.model.DeviceID;

import java.util.List;

@Dao
public interface DeviceIDDAO {

    @Query("SELECT * FROM DeviceID LIMIT 1")
    LiveData<DeviceID> getDeviceID();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DeviceID deviceID);

}
