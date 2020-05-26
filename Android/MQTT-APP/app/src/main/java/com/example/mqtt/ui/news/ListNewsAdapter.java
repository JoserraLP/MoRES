package com.example.mqtt.ui.news;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mqtt.R;
import com.example.mqtt.model.News;
import com.example.mqtt.ui.news.viewmodel.NewsViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListNewsAdapter extends RecyclerView.Adapter<ListNewsAdapter.ViewHolder> {

    private ArrayList<News> data;
    private Context context;

    private NewsViewModel newsViewModel;

    public ListNewsAdapter(Context context, NewsViewModel newsViewModel) {
        this.context = context;
        this.data = new ArrayList<>();
        this.newsViewModel = newsViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final News newsItem = data.get(position);
        holder.newsTitle.setText(newsItem.getTitle());

        if(newsItem.getImage() != null)
            Glide.with(context)
                .load(newsItem.getImage())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.newsImageView);

        holder.newsImageView.setOnClickListener(view -> {
            newsViewModel.select(newsItem);
            Navigation.findNavController((Activity) context, R.id.nav_host_fragment).navigate(R.id.nav_news_item);
        });

        holder.newsTitle.setOnClickListener(view -> {
            newsViewModel.select(newsItem);
            Navigation.findNavController((Activity) context, R.id.nav_host_fragment).navigate(R.id.nav_news_item);
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addNewsList (List<News> newsList) {
        data.clear();
        data.addAll(newsList);
        notifyDataSetChanged();
    }



    public ArrayList<News> getNews(){
        return data;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView newsImageView;
        private TextView newsTitle;

        ViewHolder(View itemView) {
            super(itemView);

            newsImageView = itemView.findViewById(R.id.news_image);
            newsTitle = itemView.findViewById(R.id.allowed_places_type_value);
        }
    }


    @NonNull
    @Override
    public String toString() {
        StringBuilder total= new StringBuilder();
        for (News newsItem : data)
            total.append(newsItem.toString());
        return total.toString();
    }
}
