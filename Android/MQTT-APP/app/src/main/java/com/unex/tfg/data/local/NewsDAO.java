package com.unex.tfg.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.unex.tfg.model.News;

import java.util.List;

@Dao
public interface NewsDAO {

    /**
     * Return all the news stored in the local database
     * @return A LiveData List for all the news
     */
    @Query("SELECT * FROM News")
    LiveData<List<News>> getAll();

    /**
     * Insert a NewsItem on the DB
     * @param newsItem News item
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(News newsItem);

}
