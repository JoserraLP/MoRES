package com.unex.tfg.ui.news.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unex.tfg.R;
import com.unex.tfg.ui.news.ListNewsAdapter;
import com.unex.tfg.ui.news.viewmodel.NewsViewModel;

import java.util.Objects;

public class NewsFragment extends Fragment {

    /**
     * onCreateView method
     * @param inflater Fragment inflater
     * @param container Fragment container
     * @param savedInstanceState Bundle where instance is saved
     * @return Fragment View
     */
    @SuppressLint("UseRequireInsteadOfGet")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate fragment
        View root = inflater.inflate(R.layout.fragment_news, container, false);

        // Get the NewsViewModel
        @SuppressWarnings("deprecation") NewsViewModel newsViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(NewsViewModel.class);

        // Create the ListNewsAdapter object
        ListNewsAdapter listNewsAdapter = new ListNewsAdapter(this.getContext(), newsViewModel);

        // Get all the news and save it to the adapter
        newsViewModel.getAllNews().observe(requireActivity(), listNewsAdapter::addNewsList);

        // RecyclerView settings
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(listNewsAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(null, 1);
        recyclerView.setLayoutManager(layoutManager);

        return root;
    }
}
