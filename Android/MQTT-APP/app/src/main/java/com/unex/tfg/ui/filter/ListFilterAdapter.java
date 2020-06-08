package com.unex.tfg.ui.filter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.unex.tfg.R;
import com.unex.tfg.model.AllowedPlacesType;
import com.unex.tfg.ui.filter.viewmodel.AllowedPlacesTypeViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListFilterAdapter extends RecyclerView.Adapter<ListFilterAdapter.ViewHolder> {

    // AllowedPlacesViewModel to retrieve the information
    private AllowedPlacesTypeViewModel allowedPlacesTypeViewModel;

    // Allowed places types data
    private ArrayList<AllowedPlacesType> data;

    // Adapter context
    private Context context;

    /**
     * ListFilterAdapter constructor
     * @param context Adapter context
     * @param allowedPlacesTypeViewModel AllowedPlacesTypeViewModel
     */
    public ListFilterAdapter(Context context, AllowedPlacesTypeViewModel allowedPlacesTypeViewModel) {
        this.context = context;
        this.data = new ArrayList<>();
        this.allowedPlacesTypeViewModel = allowedPlacesTypeViewModel;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allowed_places_type, parent, false);
        return new ViewHolder(view);
    }

    /**
     * onBindViewHolder
     * @param holder ViewHolder
     * @param position Item position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        // Get the allowed place type item
        final AllowedPlacesType allowedPlacesTypeItem = data.get(position);

        // Set values on UI fields
        holder.allowedPlacesTypeTitle.setText(allowedPlacesTypeItem.getTitle());
        holder.allowedPlacesTypeSwitch.setChecked(allowedPlacesTypeItem.isChecked());

        if(allowedPlacesTypeItem.getIcon() != null)
            Glide.with(context)
                    .load(allowedPlacesTypeItem.getIcon())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.allowedPlacesTypeImage);

        // Define onClick callback
        holder.allowedPlacesTypeSwitch.setOnClickListener(view -> {
            allowedPlacesTypeItem.setChecked(!allowedPlacesTypeItem.isChecked());
            allowedPlacesTypeViewModel.updateAllowedPlacesTypeChecked(allowedPlacesTypeItem);
        });

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
     * Add a AllowedPlacesTypesList to the current list and sort it
     * @param allowedPlacesTypesList new AllowedPlacesTypes list
     */
    public void addAllowedPlacesTypeList (List<AllowedPlacesType> allowedPlacesTypesList) {
        data.clear();
        data.addAll(allowedPlacesTypesList);
        // Sort types by title
        data.sort((o1, o2) -> o1.getTitle().compareTo(o2.getTitle()));
        // Notify that the list has been changed
        notifyDataSetChanged();
    }

    // ViewHolder class to represent the allowed places types in the adapter
    static class ViewHolder extends RecyclerView.ViewHolder {
        private Switch allowedPlacesTypeSwitch;
        private TextView allowedPlacesTypeTitle;
        private ImageView allowedPlacesTypeImage;

        /**
         * ViewHolder constructor
         * @param itemView View
         */
        ViewHolder(View itemView) {
            super(itemView);

            allowedPlacesTypeSwitch = itemView.findViewById(R.id.allowed_places_type_switch);
            allowedPlacesTypeTitle = itemView.findViewById(R.id.allowed_places_type_value);
            allowedPlacesTypeImage = itemView.findViewById(R.id.allowed_places_type_image);
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
        for (AllowedPlacesType allowedPlacesType : data)
            total.append(allowedPlacesType.toString());
        return total.toString();
    }
}
