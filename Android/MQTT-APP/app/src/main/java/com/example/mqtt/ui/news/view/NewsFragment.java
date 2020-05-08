package com.example.mqtt.ui.news.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Objects;

public class NewsFragment extends Fragment {

    private NewsViewModel newsViewModel;

    private ListNewsAdapter listNewsAdapter;

    @SuppressLint("UseRequireInsteadOfGet")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_news, container, false);

        //noinspection deprecation
        newsViewModel =
                ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(NewsViewModel.class);

        listNewsAdapter = new ListNewsAdapter(this.getContext(), newsViewModel);

        newsViewModel.getAllNews().observe(requireActivity(), allNews -> listNewsAdapter.addNewsList(allNews));

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(listNewsAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(null, 1);
        recyclerView.setLayoutManager(layoutManager);

        return root;
    }
}
