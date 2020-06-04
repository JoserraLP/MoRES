package com.example.mqtt.ui.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mqtt.DrawerActivity;
import com.example.mqtt.R;
import com.example.mqtt.data.repository.AllowedPlacesRepository;
import com.example.mqtt.data.repository.NearbyDevicesRepository;
import com.example.mqtt.model.AllowedPlaces;
import com.example.mqtt.model.AllowedPlacesType;
import com.example.mqtt.model.NearbyDevice;
import com.example.mqtt.service.ForegroundService;
import com.example.mqtt.ui.filter.viewmodel.AllowedPlacesTypeViewModel;
import com.example.mqtt.ui.map.viewmodel.AllowedPlacesViewModel;
import com.example.mqtt.ui.map.viewmodel.NearbyDevicesViewModel;
import com.example.mqtt.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.material.snackbar.Snackbar;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = MapFragment.class.getSimpleName();

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

    private ArrayList<NearbyDevice> nearbyDevices;

    private TileOverlay mOverlay;

    private AsyncTask iconLoaderTask;

    private ArrayList<AllowedPlaces> allowedPlaces;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        // Inflate the map fragment
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        locationReceiver = new LocationReceiver();

        allowedPlacesTypes = new ArrayList<>();
        allowedPlaces = new ArrayList<>();
        nearbyDevices = new ArrayList<>();

        //noinspection deprecation
        allowedPlacesViewModel = ViewModelProviders.of(requireActivity()).get(AllowedPlacesViewModel.class);

        //noinspection deprecation
        nearbyDevicesViewModel = ViewModelProviders.of(requireActivity()).get(NearbyDevicesViewModel.class);

        @SuppressWarnings("deprecation")
        AllowedPlacesTypeViewModel allowedPlacesTypeViewModel = ViewModelProviders.of(requireActivity()).get(AllowedPlacesTypeViewModel.class);

        allowedPlacesTypeViewModel.getAllAllowedPlacesType().observe(requireActivity(), allAllowedPlacesType -> allowedPlacesTypes.addAll(allAllowedPlacesType));

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        // Get the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        // Register Location receiver
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(locationReceiver,
                new IntentFilter(ForegroundService.ACTION_BROADCAST));

        // Get the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        allowedTypes = new ArrayList<>();
        allowedPlaces = new ArrayList<>();

        for (AllowedPlacesType allowedPlacesType : allowedPlacesTypes){
            if (allowedPlacesType.isChecked())
                allowedTypes.add(allowedPlacesType.getType());
        }

        if (curLocation != null) {
            map.setMyLocationEnabled(true);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), zoom));
        }

/*
        // Create the Handler object (on the main thread by default)
        Handler handler = new Handler();
        // Define the code block to be executed
        Runnable runnableCode = new Runnable() { //TODO hacer que sea cada más tiempo
            @Override
            public void run() {
                if (curLocation != null) {
                    setNearbyDevicesOnHeatMap();
                    handler.postDelayed(this, Constants.NEARBY_DEVICES_MILLIS_REQUEST);
                }
            }
        };

        // Start the initial runnable task by posting through the handler
        handler.post(runnableCode);

 */
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady");
        // Save the map instance
        map = googleMap;
        map.getUiSettings().setZoomControlsEnabled(true);

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
                    setNearbyDevicesOnHeatMap();
                    if (curLocation.getLatitude() != location.getLatitude() && curLocation.getLongitude() != location.getLongitude()) {
                        curLocation = location;
                        if (lastNearbyLocation != null) {
                            if (curLocation.distanceTo(lastNearbyLocation) > Constants.NEARBY_ALLOWED_PLACES_MIN_DISTANCE) {
                                lastNearbyLocation = curLocation;

                                setNearbyPlacesOnMap();
                            }

                        } else {
                            lastNearbyLocation = curLocation;
                            setNearbyPlacesOnMap();
                        }
                    }
                    else
                        return;
                } else {
                    curLocation = location;
                    lastNearbyLocation = location;
                    setNearbyDevicesOnHeatMap();
                    setNearbyPlacesOnMap();
                }

                map.setMyLocationEnabled(true);
            }
        }
    }


    private void setNearbyPlacesOnMap(){
        if (isAdded()) {
            map.clear();

            allowedPlaces = new ArrayList<>();

            Log.d(TAG, "Loading nearby locations by distance");
            String locationString = curLocation.getLatitude() + "," + curLocation.getLongitude();
            // Load all the allowed places
            AllowedPlacesRepository.getInstance(Objects.requireNonNull(requireActivity().getApplication())).loadAllowedPlacesByLocation(locationString);
            allowedPlacesViewModel.getAllAllowedPlacesByTypes(allowedTypes).observe(requireActivity(), allAllowedPlaces -> {
                allowedPlaces.addAll(allAllowedPlaces);
                addPlacesToMap(allowedPlaces);
            });
        }
    }

    private void addPlacesToMap(ArrayList<AllowedPlaces> allowedPlaces){
        for (AllowedPlaces place : allowedPlaces) {
            Log.d(TAG, "New nearby places");
            Location placeLocation = new Location("");
            placeLocation.setLatitude(place.getGeo_lat());
            placeLocation.setLongitude(place.getGeo_long());
            if (curLocation.distanceTo(placeLocation) < 200) {
                Log.d(TAG, place.toString());
                ArrayList<Object> taskParameters = new ArrayList<>();
                taskParameters.add(place.getIcon());
                taskParameters.add(new LatLng(place.getGeo_lat(), place.getGeo_long()));
                taskParameters.add(place.getName());
                //noinspection unchecked
                iconLoaderTask = new IconLoaderTask().execute(taskParameters);
            }
        }
    }


    private ArrayList<LatLng> processNearbyDevices(ArrayList<NearbyDevice> nearbyDevices){
        ArrayList<LatLng> nearbyDevicesLatLng = new ArrayList<>();
        for (NearbyDevice nearbyDevice : nearbyDevices)
            nearbyDevicesLatLng.add(new LatLng(nearbyDevice.getGeo_lat(), nearbyDevice.getGeo_long()));
        return nearbyDevicesLatLng;
    }

    /**
     * Set the nearby devices on the heat map
     */

    private void setNearbyDevicesOnHeatMap(){
        if (isAdded()) {
            NearbyDevicesRepository.getInstance(requireActivity().getApplication()).loadNearbyDevices(curLocation.getLatitude(), curLocation.getLongitude(), Constants.NEARBY_DEVICES_RADIUS);
            nearbyDevicesViewModel.getNearbyDevices().observe(requireActivity(), allNearbyDevices -> {
                nearbyDevices.clear();
                nearbyDevices.addAll(allNearbyDevices);
                addHeatMap(processNearbyDevices(nearbyDevices));
            });
        }
    }


    /**
     *  Heat Map
     */

    private int[] colors = {
            Color.rgb(102, 225, 0), // green
            Color.rgb(255, 165, 0)    // red
    };

    private float[] startpoints = {
            0.2f, 1f
    };

    private void addHeatMap(ArrayList<LatLng> nearbyDevicesLatLng){
        // Create a heat map tile provider, passing it the latlngs of the police stations.
        if (!nearbyDevicesLatLng.isEmpty()) {
            if (mOverlay != null) {
                mOverlay.remove();
            }

            Gradient gradient = new Gradient(colors,startpoints);
            int radius = 15;
            double opacity = 0.4;
            TileProvider mProvider = new HeatmapTileProvider.Builder()
                    .data(nearbyDevicesLatLng)
                    .gradient(gradient)
                    .radius(radius)
                    .opacity(opacity)
                    .build();
            // Add a tile overlay to the map, using the heat map tile provider.
            mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

        }

    }

    /**
     * Allowed Places Icon Loader
     */
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
