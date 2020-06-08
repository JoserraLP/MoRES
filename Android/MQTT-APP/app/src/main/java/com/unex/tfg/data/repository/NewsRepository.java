package com.unex.tfg.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.unex.tfg.data.local.AppDatabase;
import com.unex.tfg.model.News;

import java.util.List;

public class NewsRepository {

    // Singleton class instance
    private static NewsRepository mInstance;

    // Local database
    private AppDatabase database;

    /**
     * Get the NewsRepository Singleton instance
     * @param application Application context
     * @return NewsRepository Singleton instance
     */
    public static synchronized NewsRepository getInstance(Application application){
        if(mInstance == null){
            mInstance = new NewsRepository(application);
        }
        return mInstance;
    }

    /**
     * NewsRepository constructor
     * @param application Application
     */
    private NewsRepository(Application application) {
        database = AppDatabase.getDatabase(application);
    }

    /**
     * Return all the news stored in the database
     * @return LiveData List of all the news stored in the database
     */
    public LiveData<List<News>> getAllNews() {
        return database.getNewsDAO().getAll();
    }

    /**
     * Insert a News item in the database
     * @param newsItem LiveData of the News object
     */
    public void insertNews(final LiveData<News> newsItem) {
        AppDatabase.databaseWriteExecutor.execute(() -> database.getNewsDAO().insert(newsItem.getValue()));
    }
}

