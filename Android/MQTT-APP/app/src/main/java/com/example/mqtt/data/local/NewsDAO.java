package com.example.mqtt.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.mqtt.model.News;

import java.util.List;

@Dao
public interface NewsDAO {

    @Query("SELECT * FROM News")
    LiveData<List<News>> getAll();

    @Query("SELECT COUNT(*) FROM News")
    int getSize();

    @Query("SELECT * FROM News " +
            "WHERE News.date >= :date" +
            " ORDER BY News.date")
    LiveData<List<News>> getAllByDate(String date);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(News newsItem);

}
