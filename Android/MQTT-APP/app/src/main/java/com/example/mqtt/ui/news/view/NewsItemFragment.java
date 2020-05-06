package com.example.mqtt.ui.news.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mqtt.R;
import com.example.mqtt.model.News;
import com.example.mqtt.ui.news.viewmodel.NewsViewModel;

public class NewsItemFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //noinspection deprecation
        NewsViewModel newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);

        final View root = inflater.inflate(R.layout.fragment_news_info, container, false);

        final ImageView news_image = root.findViewById(R.id.image_view_news);
        final TextView news_title = root.findViewById(R.id.text_view_news_title);
        final TextView news_date = root.findViewById(R.id.text_view_news_date_value);
        final TextView news_description = root.findViewById(R.id.text_view_news_desc_value);

        newsViewModel.getSelected().observe(getViewLifecycleOwner(), new Observer<News>() {
            @Override
            public void onChanged(News newsItem) {
                Glide.with(root)
                        .load(newsItem)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(news_image);

                news_title.setText(newsItem.getTitle());
                news_date.setText(newsItem.getDate());
                news_description.setText(newsItem.getDescription());
            }
        });


        return root;
    }
}
