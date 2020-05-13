package com.example.mqtt.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mqtt.model.AllowedPlace;

import java.util.List;

@Dao
public interface AllowedPlacesDAO {

    @Query("SELECT * FROM AllowedPlace")
    LiveData<List<AllowedPlace>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AllowedPlace allowedPlace);

}
