package com.example.mqtt.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mqtt.model.AllowedPlacesType;

import java.util.List;

@Dao
public interface AllowedPlacesTypeDAO {

    @Query("SELECT * FROM AllowedPlacesType")
    LiveData<List<AllowedPlacesType>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AllowedPlacesType allowedPlacesType);

}
