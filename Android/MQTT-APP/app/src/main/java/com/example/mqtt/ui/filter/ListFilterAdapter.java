package com.example.mqtt.ui.filter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.mqtt.R;
import com.example.mqtt.model.AllowedPlacesType;
import com.example.mqtt.ui.filter.viewmodel.AllowedPlacesTypeViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListFilterAdapter extends RecyclerView.Adapter<ListFilterAdapter.ViewHolder> {

    private ArrayList<AllowedPlacesType> data;
    private Context context;

    private AllowedPlacesTypeViewModel allowedPlacesTypeViewModel;

    public ListFilterAdapter(Context context, AllowedPlacesTypeViewModel allowedPlacesTypeViewModel) {
        this.context = context;
        this.data = new ArrayList<>();
        this.allowedPlacesTypeViewModel = allowedPlacesTypeViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allowed_places_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final AllowedPlacesType allowedPlacesTypeItem = data.get(position);

        holder.allowedPlacesTypeTitle.setText(allowedPlacesTypeItem.getType());
        holder.allowedPlacesTypeSwitch.setChecked(allowedPlacesTypeItem.isChecked());

        holder.allowedPlacesTypeSwitch.setOnClickListener(view -> {
            allowedPlacesTypeItem.setChecked(!allowedPlacesTypeItem.isChecked());
            allowedPlacesTypeViewModel.updateAllowedPlacesTypeChecked(allowedPlacesTypeItem);
        });

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void addAllowedPlacesTypeList (List<AllowedPlacesType> allowedPlacesTypesList) {
        data.clear();
        data.addAll(allowedPlacesTypesList);
        notifyDataSetChanged();
    }



    public ArrayList<AllowedPlacesType> getAllowedPlacesTypes(){
        return data;
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        private Switch allowedPlacesTypeSwitch;
        private TextView allowedPlacesTypeTitle;

        ViewHolder(View itemView) {
            super(itemView);

            allowedPlacesTypeSwitch = itemView.findViewById(R.id.allowed_places_type_switch);
            allowedPlacesTypeTitle = itemView.findViewById(R.id.allowed_places_type_value);
        }
    }


    @NonNull
    @Override
    public String toString() {
        StringBuilder total= new StringBuilder();
        for (AllowedPlacesType allowedPlacesType : data)
            total.append(allowedPlacesType.toString());
        return total.toString();
    }
}
