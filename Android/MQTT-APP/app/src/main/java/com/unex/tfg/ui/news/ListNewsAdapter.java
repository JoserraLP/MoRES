package com.unex.tfg.ui.news;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.unex.tfg.R;
import com.unex.tfg.model.News;
import com.unex.tfg.ui.news.viewmodel.NewsViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListNewsAdapter extends RecyclerView.Adapter<ListNewsAdapter.ViewHolder> {

    // NewsViewModel to retrieve the information
    private NewsViewModel newsViewModel;

    // News data
    private ArrayList<News> data;

    // Adapter context
    private Context context;

    /**
     * ListNewsAdapter constructor
     * @param context Adapter context
     * @param newsViewModel NewsTypeViewModel
     */
    public ListNewsAdapter(Context context, NewsViewModel newsViewModel) {
        this.context = context;
        this.data = new ArrayList<>();
        this.newsViewModel = newsViewModel;
    }

    /**
     * onCreateViewHolder method
     * @param parent ViewGroup parent
     * @param viewType ViewType
     * @return ViewHolder with the adapter
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news, parent, false);
        return new ViewHolder(view);
    }

    /**
     * onBindViewHolder
     * @param holder ViewHolder
     * @param position Item position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // Get the news item
        final News newsItem = data.get(position);

        // Set values on UI fields
        holder.newsTitle.setText(newsItem.getTitle());
        holder.newsDate.setText(newsItem.getDate());

        if(newsItem.getImage() != null)
            Glide.with(context)
                .load(newsItem.getImage())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.newsImageView);

        // Define onClick callback
        holder.linearLayout.setOnClickListener(view -> {
            newsViewModel.select(newsItem);
            Navigation.findNavController((Activity) context, R.id.nav_host_fragment).navigate(R.id.nav_news_item);
        });
    }

    /**
     * Get all the news in the adapter
     * @return News in the adapter
     */
    public ArrayList<News> getNews(){
        return data;
    }

    /**
     * Get the number of elements in the adapter
     * @return Number of elements in the adapter
     */
    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * Add a newsList to the current list
     * @param newsList new News list
     */
    public void addNewsList (List<News> newsList) {
        data.clear();
        data.addAll(newsList);
        // Notify that the list has been changed
        notifyDataSetChanged();
    }

    // ViewHolder class to represent the news in the adapter
    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView newsImageView;
        private TextView newsTitle;
        private TextView newsDate;
        private LinearLayout linearLayout;

        /**
         * ViewHolder constructor
         * @param itemView View
         */
        ViewHolder(View itemView) {
            super(itemView);

            newsImageView = itemView.findViewById(R.id.news_image);
            newsTitle = itemView.findViewById(R.id.news_title);
            newsDate = itemView.findViewById(R.id.news_date);
            linearLayout = itemView.findViewById(R.id.layout_news_item);

        }
    }

    /**
     * toString method
     * @return String representing the adapter
     */
    @NonNull
    @Override
    public String toString() {
        StringBuilder total = new StringBuilder();
        for (News newsItem : data)
            total.append(newsItem.toString());
        return total.toString();
    }
}
