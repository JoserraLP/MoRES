package com.unex.tfg.ui.filter.view;

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
import com.unex.tfg.ui.filter.ListFilterAdapter;
import com.unex.tfg.ui.filter.viewmodel.AllowedPlacesTypeViewModel;

import java.util.Objects;

public class FilterFragment extends Fragment {

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
        View root = inflater.inflate(R.layout.fragment_filter, container, false);

        // Get the AllowedPlacesTypeViewModel
        @SuppressWarnings("deprecation") AllowedPlacesTypeViewModel allowedPlacesTypeViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(AllowedPlacesTypeViewModel.class);

        // Create the ListFilterAdapter object
        ListFilterAdapter listFilterAdapter = new ListFilterAdapter(this.getContext(), allowedPlacesTypeViewModel);

        // Get all allowed places types and save it to the adapter
        allowedPlacesTypeViewModel.getAllAllowedPlacesType().observe(requireActivity(), listFilterAdapter::addAllowedPlacesTypeList);

        // RecyclerView settings
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(listFilterAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(null, 1);
        recyclerView.setLayoutManager(layoutManager);

        return root;
    }

}
