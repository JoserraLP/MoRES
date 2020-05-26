package com.example.mqtt.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mqtt.model.AllowedPlaces;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface AllowedPlacesDAO {

    @Query("SELECT * FROM AllowedPlaces")
    LiveData<List<AllowedPlaces>> getAll();

    @Query("SELECT * FROM AllowedPlaces WHERE type IN (:types)")
    LiveData<List<AllowedPlaces>> getAllByTypes(List<String> types);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AllowedPlaces allowedPlaces);

}