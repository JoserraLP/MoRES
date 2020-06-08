package com.unex.tfg.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.unex.tfg.model.DeviceID;

@Dao
public interface DeviceIDDAO {

    /**
     * Return the Device ID stored in the local database
     * @return A LiveData with the Device ID
     */
    @Query("SELECT * FROM DeviceID LIMIT 1")
    LiveData<DeviceID> getDeviceID();

    /**
     * Insert a DeviceID on the DB
     * @param deviceID Device ID
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DeviceID deviceID);

}
