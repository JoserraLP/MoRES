package com.example.mqtt.ui.news.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mqtt.R;
import com.example.mqtt.model.News;
import com.example.mqtt.ui.news.ListNewsAdapter;
import com.example.mqtt.ui.news.viewmodel.NewsViewModel;

import java.util.List;

public class NewsFragment extends Fragment {

    private NewsViewModel newsViewModel;

    private ListNewsAdapter listNewsAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_news, container, false);

        //noinspection deprecation
        newsViewModel =
                ViewModelProviders.of(this).get(NewsViewModel.class);

        listNewsAdapter = new ListNewsAdapter(this.getContext(), newsViewModel);

        newsViewModel.getAllNews().observe(getViewLifecycleOwner(), new Observer<List<News>>() {
            @Override
            public void onChanged(List<News> allNews) {
                listNewsAdapter.addNewsList(allNews);
            }
        });

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(listNewsAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(null, 3);
        recyclerView.setLayoutManager(layoutManager);

        return root;
    }
}
