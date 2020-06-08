package com.unex.tfg.ui.news.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.unex.tfg.data.repository.NewsRepository;
import com.unex.tfg.model.News;

import java.util.List;

public class NewsViewModel extends AndroidViewModel {

    // LiveData of the news
    private LiveData<List<News>> mAllNews;

    // Selected news item
    private MutableLiveData<News> selected = new MutableLiveData<>();

    /**
     * NewsViewModel constructor
     * @param application Application
     */
    public NewsViewModel(Application application) {
        super(application);
        // Get repository instance
        NewsRepository mRepository = NewsRepository.getInstance(application);
        // Get all news
        mAllNews = mRepository.getAllNews();
    }

    /**
     * Get all news
     * @return LiveData list with all news in the database
     */
    public LiveData<List<News>> getAllNews() {
        return mAllNews;
    }

    /**
     * Get selected news item
     * @return News item selected
     */
    public LiveData<News> getSelected() {
        return selected;
    }

    /**
     * Select a news item
     * @param newsItem Selected news item
     */
    public void select(News newsItem) {
        selected.setValue(newsItem);
    }
}