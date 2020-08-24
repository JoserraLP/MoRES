package com.unex.tfg.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.unex.tfg.model.AllowedPlacesType;

import java.util.List;

@Dao
public interface AllowedPlacesTypeDAO {

    /**
     * Return all the allowed places type stored in the local database
     * @return A LiveData List for all the allowed places types
     */
    @Query("SELECT * FROM AllowedPlacesType")
    LiveData<List<AllowedPlacesType>> getAll();

    /**
     * Insert a AllowedPlacesType on the DB
     * @param allowedPlacesType Allowed Places Type
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AllowedPlacesType allowedPlacesType);

    /**
     * Update a AllowedPlacesType on the DB
     * @param allowedPlacesType Allowed Places Type
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(AllowedPlacesType allowedPlacesType);


    /**
     * Delete all the AllowedPlacesType on the DB
     */
    @Query("DELETE FROM AllowedPlacesType")
    void deleteAll();

}
