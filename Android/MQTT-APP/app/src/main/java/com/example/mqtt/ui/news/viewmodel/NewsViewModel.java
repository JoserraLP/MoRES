package com.example.mqtt.ui.news.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mqtt.data.repository.NewsRepository;
import com.example.mqtt.model.News;

import java.util.List;

public class NewsViewModel extends AndroidViewModel {

    private NewsRepository mRepository;

    private LiveData<List<News>> mAllNews;

    private MutableLiveData<News> selected = new MutableLiveData<>();

    private MutableLiveData<String> mText;

    public NewsViewModel(Application application) {
        super(application);
        mRepository = NewsRepository.getInstance(application);
        mAllNews = mRepository.getAllNews();
    }

    public LiveData<List<News>> getAllNews() {
        return mAllNews;
    }

    public LiveData<List<News>> searchByDate(String date) {
        return mRepository.searchByDate(date);
    }

    public void select(News newsItem) {
        selected.setValue(newsItem);
    }

    public LiveData<News> getSelected() {
        return selected;
    }
}