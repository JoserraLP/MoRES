package com.unex.tfg.ui.news.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.unex.tfg.R;
import com.unex.tfg.ui.news.viewmodel.NewsViewModel;

import java.util.Objects;

public class NewsItemFragment extends Fragment {

    /**
     * onCreateView method
     * @param inflater Fragment inflater
     * @param container Fragment container
     * @param savedInstanceState Bundle where instance is saved
     * @return Fragment View
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Inflate fragment
        View root = inflater.inflate(R.layout.fragment_news_info, container, false);

        // UI fields
        ImageView news_image = root.findViewById(R.id.image_view_news);
        TextView news_title = root.findViewById(R.id.text_view_news_title);
        TextView news_author = root.findViewById(R.id.text_view_news_author_value);
        TextView news_date = root.findViewById(R.id.text_view_news_date_value);
        TextView news_description = root.findViewById(R.id.text_view_news_desc_value);

        // Get the NewsViewModel
        @SuppressWarnings("deprecation") @SuppressLint("UseRequireInsteadOfGet")
        NewsViewModel newsViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(NewsViewModel.class);

        // Get selected and set UI fields values
        newsViewModel.getSelected().observe(requireActivity(), newsItem -> {

            Glide.with(root)
                    .load(newsItem.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(news_image);

            news_author.setText(newsItem.getAuthor());
            news_title.setText(newsItem.getTitle());
            news_date.setText(newsItem.getDate());
            news_description.setText(newsItem.getDescription());
        });

        return root;
    }
}
