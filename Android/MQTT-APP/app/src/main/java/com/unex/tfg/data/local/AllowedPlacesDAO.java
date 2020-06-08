package com.unex.tfg.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.unex.tfg.model.AllowedPlaces;

import java.util.List;

@Dao
public interface AllowedPlacesDAO {

    /**
     * Return all the allowed places stored in the local database
     * @return A LiveData List for all the allowed places
     */
    @Query("SELECT * FROM AllowedPlaces")
    LiveData<List<AllowedPlaces>> getAll();

    /**
     * Return the allowed places given a set of types
     * @param types Allowed types
     * @return A LiveData List for the allowed places with a specific type
     */
    @Query("SELECT * FROM AllowedPlaces WHERE type IN (:types)")
    LiveData<List<AllowedPlaces>> getAllByTypes(List<String> types);

    /**
     * Insert a AllowedPlaces on the DB
     * @param allowedPlaces Allowed Place
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AllowedPlaces allowedPlaces);

}
