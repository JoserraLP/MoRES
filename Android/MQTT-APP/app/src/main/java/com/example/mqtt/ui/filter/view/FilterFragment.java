package com.example.mqtt.ui.filter.view;

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

import com.example.mqtt.R;
import com.example.mqtt.ui.filter.ListFilterAdapter;
import com.example.mqtt.ui.filter.viewmodel.AllowedPlacesTypeViewModel;

import java.util.Objects;

public class FilterFragment extends Fragment {

    private AllowedPlacesTypeViewModel allowedPlacesTypeViewModel;

    private ListFilterAdapter listFilterAdapter;

    @SuppressLint("UseRequireInsteadOfGet")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_filter, container, false);

        //noinspection deprecation
        allowedPlacesTypeViewModel =
                ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(AllowedPlacesTypeViewModel.class);

        listFilterAdapter = new ListFilterAdapter(this.getContext(), allowedPlacesTypeViewModel);

        allowedPlacesTypeViewModel.getAllAllowedPlacesType().observe(requireActivity(), allAllowedPlacesType -> listFilterAdapter.addAllowedPlacesTypeList(allAllowedPlacesType));

        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(listFilterAdapter);
        recyclerView.setHasFixedSize(true);
        final GridLayoutManager layoutManager = new GridLayoutManager(null, 1);
        recyclerView.setLayoutManager(layoutManager);

        return root;
    }

}
