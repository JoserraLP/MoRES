package com.example.mqtt.ui.home;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mqtt.R;
import com.example.mqtt.data.repository.NearbyDevicesRepository;
import com.example.mqtt.model.AllowedPlaces;
import com.example.mqtt.model.AllowedPlacesType;
import com.example.mqtt.model.NearbyDevice;
import com.example.mqtt.service.ForegroundService;
import com.example.mqtt.ui.filter.viewmodel.AllowedPlacesTypeViewModel;
import com.example.mqtt.ui.home.viewmodel.AllowedPlacesViewModel;
import com.example.mqtt.ui.home.viewmodel.NearbyDevicesViewModel;
import com.example.mqtt.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.data.kml.KmlGroundOverlay;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.net.URL;
import java.security.Provider;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private GoogleMap map;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private LocationReceiver locationReceiver;

    private Location curLocation;

    private Location lastNearbyLocation;

    private Float zoom = 18.0f;

    private ArrayList<AllowedPlacesType> allowedPlacesTypes;

    private List<String> allowedTypes;

    private AllowedPlacesViewModel allowedPlacesViewModel;

    private NearbyDevicesViewModel nearbyDevicesViewModel;

    private ArrayList<AllowedPlaces> allowedPlaces;

    private ArrayList<NearbyDevice> nearbyDevices;

    private TileOverlay mOverlay;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the map fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        locationReceiver = new LocationReceiver();

        allowedPlacesTypes = new ArrayList<>();
        allowedPlaces = new ArrayList<>();
        nearbyDevices = new ArrayList<>();

        @SuppressWarnings("deprecation")
        AllowedPlacesTypeViewModel allowedPlacesTypeViewModel = ViewModelProviders.of(requireActivity()).get(AllowedPlacesTypeViewModel.class);

        //noinspection deprecation
        allowedPlacesViewModel = ViewModelProviders.of(requireActivity()).get(AllowedPlacesViewModel.class);

        allowedPlacesTypeViewModel.getAllAllowedPlacesType().observe(requireActivity(), allAllowedPlacesType -> allowedPlacesTypes.addAll(allAllowedPlacesType));

        //noinspection deprecation
        nearbyDevicesViewModel = ViewModelProviders.of(requireActivity()).get(NearbyDevicesViewModel.class);

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
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(locationReceiver,
                new IntentFilter(ForegroundService.ACTION_BROADCAST));

        // Get the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        allowedTypes = new ArrayList<>();

        for (AllowedPlacesType allowedPlacesType : allowedPlacesTypes){
            if (allowedPlacesType.isChecked())
                allowedTypes.add(allowedPlacesType.getType());
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Save the map instance
        map = googleMap;

        if (curLocation != null) {
            map.setMyLocationEnabled(true);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), zoom));
        }
    }

    /**
     * Receiver for broadcasts sent by {@link ForegroundService}.
     */
    public class LocationReceiver extends BroadcastReceiver {
        @SuppressLint("UseRequireInsteadOfGet")
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(ForegroundService.EXTRA_LOCATION);
            if (location != null) {
                if (curLocation != null) {
                    if (curLocation.getLatitude() != location.getLatitude() && curLocation.getLongitude() != location.getLongitude()) {
                        curLocation = location;
                        if (lastNearbyLocation != null) {
                            if (curLocation.distanceTo(lastNearbyLocation) > 200) { // TODO load near places. Do this with the location from the first time for example.
                                lastNearbyLocation = curLocation;
                                Log.d(TAG, "New nearby places - by last near location");

                                map.clear();

                                //if (getActivity() != null) {
                                    allowedPlacesViewModel.getAllAllowedPlacesByTypes(allowedTypes).observe(Objects.requireNonNull(getActivity()), allAllowedPlaces -> allowedPlaces.addAll(allAllowedPlaces));
                                    // allowedPlacesViewModel.getAllAllowedPlaces().observe(requireActivity(), allAllowedPlaces -> allowedPlaces.addAll(allAllowedPlaces));

                                    NearbyDevicesRepository.getInstance(getActivity().getApplication()).loadNearbyDevices(curLocation.getLatitude(), curLocation.getLongitude(), Constants.NEARBY_DEVICES_RADIUS);
                                    nearbyDevicesViewModel.getNearbyDevices().observe(Objects.requireNonNull(getActivity()), allNearbyDevices -> nearbyDevices.addAll(allNearbyDevices));

                                    for (NearbyDevice nearbyDevice : nearbyDevices){ // TODO insert in the heatmap
                                        Log.d(TAG, nearbyDevice.toString());
                                    }

                                    addHeatMap(processNearbyDevices(nearbyDevices));

                                    for (AllowedPlaces place : allowedPlaces) {
                                        Location placeLocation = new Location("");
                                        placeLocation.setLatitude(place.getGeo_lat());
                                        placeLocation.setLongitude(place.getGeo_long());
                                        if (curLocation.distanceTo(placeLocation) < 200) {
                                            ArrayList<Object> taskParameters = new ArrayList<>();
                                            taskParameters.add(place.getIcon());
                                            taskParameters.add(new LatLng(place.getGeo_lat(), place.getGeo_long()));
                                            taskParameters.add(place.getName());
                                            //noinspection unchecked
                                            new IconLoaderTask().execute(taskParameters);

                                        }
                                    }
                               // }
                            }

                        } else {
                            lastNearbyLocation = curLocation;

                            map.clear();
                           // if (getActivity() != null) {
                                allowedPlacesViewModel.getAllAllowedPlacesByTypes(allowedTypes).observe(requireActivity(), allAllowedPlaces -> allowedPlaces.addAll(allAllowedPlaces));

                                //allowedPlacesViewModel.getAllAllowedPlaces().observe(requireActivity(), allAllowedPlaces -> allowedPlaces.addAll(allAllowedPlaces)); // TODO check if pattern repository is right

                            NearbyDevicesRepository.getInstance(Objects.requireNonNull(getActivity()).getApplication()).loadNearbyDevices(curLocation.getLatitude(), curLocation.getLongitude(), Constants.NEARBY_DEVICES_RADIUS);
                            nearbyDevicesViewModel.getNearbyDevices().observe(Objects.requireNonNull(getActivity()), allNearbyDevices -> nearbyDevices.addAll(allNearbyDevices));

                            for (NearbyDevice nearbyDevice : nearbyDevices){ // TODO insert in the heatmap
                                Log.d(TAG, nearbyDevice.toString());
                            }

                            addHeatMap(processNearbyDevices(nearbyDevices));

                                for (AllowedPlaces place : allowedPlaces) {
                                    Log.d(TAG, "New nearby places");
                                    Location placeLocation = new Location("");
                                    location.setLatitude(place.getGeo_lat());
                                    location.setLongitude(place.getGeo_long());
                                    if (curLocation.distanceTo(placeLocation) < 200) {
                                        Log.d(TAG, place.toString());
                                        ArrayList<Object> taskParameters = new ArrayList<>();
                                        taskParameters.add(place.getIcon());
                                        taskParameters.add(new LatLng(place.getGeo_lat(), place.getGeo_long()));
                                        taskParameters.add(place.getName());
                                        //noinspection unchecked
                                        new IconLoaderTask().execute(taskParameters);
                                    }
                                }
                          //  }
                        }
                    }
                    else
                        return;
                } else
                    curLocation = location;

                map.setMyLocationEnabled(true);
            }
        }
    }

    private ArrayList<LatLng> processNearbyDevices(ArrayList<NearbyDevice> nearbyDevices){
        ArrayList<LatLng> nearbyDevicesLatLng = new ArrayList<>();

        for (NearbyDevice nearbyDevice : nearbyDevices)
            nearbyDevicesLatLng.add(new LatLng(nearbyDevice.getGeo_lat(), nearbyDevice.getGeo_long()));

        return nearbyDevicesLatLng;
    }

    private void addHeatMap(ArrayList<LatLng> nearbyDevicesLatLng){
        // Create a heat map tile provider, passing it the latlngs of the police stations.
        if (!nearbyDevicesLatLng.isEmpty()) {
            TileProvider mProvider = new HeatmapTileProvider.Builder()
                    .data(nearbyDevicesLatLng)
                    .build();
            // Add a tile overlay to the map, using the heat map tile provider.
            mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }else{
            if (mOverlay != null)
                mOverlay.remove();
        }
    }

    @SuppressLint("StaticFieldLeak")
    class IconLoaderTask extends AsyncTask <ArrayList<Object>, String, Bitmap>
    {
        private LatLng position;
        private String title;

        @SafeVarargs
        @Override
        protected final Bitmap doInBackground(ArrayList<Object>... arrayLists) {
            URL url ;
            Bitmap bmp = null;
            try {
                url = new URL(arrayLists[0].get(0).toString());
                position = (LatLng) arrayLists[0].get(1);
                title = arrayLists[0].get(2).toString();
                bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bmp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (isAdded()) {
                super.onPostExecute(result);
                map.addMarker(new MarkerOptions()
                        .position(position)
                        .title(title)
                        .icon(BitmapDescriptorFactory.fromBitmap(result)));
            }
        }
    }
}
