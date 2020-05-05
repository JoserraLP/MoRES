package com.example.mqtt.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mqtt.R;
import com.example.mqtt.service.BackgroundService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private GoogleMap map;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private LocationReceiver locationReceiver;

    private Location curLocation;

    private Marker prevMarker;

    private Float zoom = 12.0f;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the map fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        // Initialize the location receiver
        locationReceiver = new LocationReceiver();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register Location receiver
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(locationReceiver,
                new IntentFilter(BackgroundService.ACTION_BROADCAST));

        // Get the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Save the map instance
        map = googleMap;
    }

    /**
     * Receiver for broadcasts sent by {@link BackgroundService}.
     */
    public class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(BackgroundService.EXTRA_LOCATION);
            if (location != null) {
                if (curLocation != null) {
                    if (curLocation.getLatitude() != location.getLatitude() && curLocation.getLongitude() != location.getLongitude())
                        curLocation = location;
                    else
                        return;
                } else
                    curLocation = location;
                if (prevMarker != null)
                    prevMarker.remove();
                prevMarker = map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Current location"));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), zoom));
            }
        }
    }
}
