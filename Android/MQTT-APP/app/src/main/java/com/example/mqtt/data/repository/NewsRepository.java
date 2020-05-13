package com.example.mqtt.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.mqtt.data.local.AppDatabase;
import com.example.mqtt.model.News;

import java.util.List;

public class NewsRepository {

    private Application application;

    private static NewsRepository mInstance;

    private AppDatabase database;

    public static synchronized NewsRepository getInstance(Application application){
        if(mInstance == null){
            mInstance = new NewsRepository(application);
        }
        return mInstance;
    }

    private NewsRepository(Application application) {
        database = AppDatabase.getDatabase(application);
        this.application = application;
    }

    public LiveData<List<News>> getAllNews() {
        return database.getNewsDAO().getAll();
    }


    public void insertNews(final LiveData<News> newsItem) {
        AppDatabase.databaseWriteExecutor.execute(() -> database.getNewsDAO().insert(newsItem.getValue()));
    }

    public LiveData<List<News>> searchByDate(String date){
        return database.getNewsDAO().getAllByDate(date);
    }
}

