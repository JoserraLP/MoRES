package com.unex.tfg.ui.map.view;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
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

import com.google.android.gms.maps.model.MapStyleOptions;
import com.unex.tfg.R;
import com.unex.tfg.data.repository.AllowedPlacesRepository;
import com.unex.tfg.data.repository.NearbyDevicesRepository;
import com.unex.tfg.model.AllowedPlaces;
import com.unex.tfg.model.AllowedPlacesType;
import com.unex.tfg.model.NearbyDevice;
import com.unex.tfg.service.ForegroundService;
import com.unex.tfg.ui.filter.viewmodel.AllowedPlacesTypeViewModel;
import com.unex.tfg.ui.map.viewmodel.AllowedPlacesViewModel;
import com.unex.tfg.ui.map.viewmodel.NearbyDevicesViewModel;
import com.unex.tfg.utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    // TAG for Log messaging
    private static final String TAG = MapFragment.class.getSimpleName();

    // ------- Map ------- //
    // Google Map instance
    private GoogleMap map;

    // Heat Map Overlay
    private TileOverlay mOverlay;

    // ------- Location ------- //
    // Current device location
    private Location curLocation;

    // Last nearby places location
    private Location lastNearbyLocation;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private LocationReceiver locationReceiver;

    // ------- ViewModel ------- //
    // AllowedPlacesViewModel
    private AllowedPlacesViewModel allowedPlacesViewModel;

    // NearbyDevicesViewModel
    private NearbyDevicesViewModel nearbyDevicesViewModel;

    // ------- Data ------- //

    // ArrayList with all the allowed places types
    private ArrayList<AllowedPlacesType> allowedPlacesTypes;

    // ArrayList with the nearby devices
    private ArrayList<NearbyDevice> nearbyDevices;

    // ArrayList with the nearby allowed places
    private ArrayList<AllowedPlaces> allowedPlaces;

    /**
     * Get the map fragment
     */
    private void getMapFragment(){
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);
    }

    /**
     * If the current location is known make zoom on it
     */
    private void zoomInCurLocation(){
        if (curLocation != null) {
            map.setMyLocationEnabled(true);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), Constants.ZOOM));
        }
    }

    /**
     * onCreateView method
     * @param inflater Fragment inflater
     * @param container Fragment container
     * @param savedInstanceState Bundle where instance is saved
     * @return Fragment View
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Inflate the map fragment
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        // Get the map fragment
        getMapFragment();

        // Initialize variables
        locationReceiver = new LocationReceiver();
        allowedPlacesTypes = new ArrayList<>();
        allowedPlaces = new ArrayList<>();
        nearbyDevices = new ArrayList<>();

        // Get AllowedPlacesViewModel
        //noinspection deprecation
        allowedPlacesViewModel = ViewModelProviders.of(requireActivity()).get(AllowedPlacesViewModel.class);

        // Get NearbyDevicesViewModel
        //noinspection deprecation
        nearbyDevicesViewModel = ViewModelProviders.of(requireActivity()).get(NearbyDevicesViewModel.class);

        // Get AllowedPlacesTypeViewModel
        @SuppressWarnings("deprecation")
        AllowedPlacesTypeViewModel allowedPlacesTypeViewModel = ViewModelProviders.of(requireActivity()).get(AllowedPlacesTypeViewModel.class);

        // Load all the allowed places types
        allowedPlacesTypeViewModel.getAllAllowedPlacesType().observe(requireActivity(), allAllowedPlacesType -> allowedPlacesTypes.addAll(allAllowedPlacesType));

        return root;
    }

    /**
     * onViewCreated method
     * @param view View created
     * @param savedInstanceState Bundle where the instance state is saved
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the map fragment
        getMapFragment();
    }

    /**
     * onResume method
     */
    @Override
    public void onResume() {
        super.onResume();

        // Register Location receiver
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(locationReceiver,
                new IntentFilter(ForegroundService.ACTION_BROADCAST));

        // Get the map fragment
        getMapFragment();

        // If the current location is known make zoom on it
        zoomInCurLocation();
    }


    /**
     * onMapReady
     * @param googleMap GoogleMap instance
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Save the map instance
        map = googleMap;

        // Enable zoom controls
        map.getUiSettings().setZoomControlsEnabled(true);

        // If the current location is known make zoom on it
        zoomInCurLocation();

        try {
            boolean success = map.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style.", e);
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
                    // If the current location is known, get the nearby devices
                    setNearbyDevicesOnHeatMap();
                    if (curLocation.getLatitude() != location.getLatitude() && curLocation.getLongitude() != location.getLongitude()) {
                        // If the current location is not the same as the previous one
                        curLocation = location;
                        if (lastNearbyLocation != null) {
                            if (curLocation.distanceTo(lastNearbyLocation) > Constants.NEARBY_ALLOWED_PLACES_MIN_DISTANCE) {
                                // If the distance between the current location and the last location where
                                // the nearby places were loaded is big enough,
                                // load the nearby places in the map
                                lastNearbyLocation = curLocation;
                                setNearbyPlacesOnMap();
                            }

                        } else {
                            // If its the first time processing the nearby places,
                            // load and show them in the map
                            lastNearbyLocation = curLocation;
                            setNearbyPlacesOnMap();
                        }
                    }
                    else
                        return;
                } else {
                    // Update the location variables
                    curLocation = location;
                    lastNearbyLocation = location;
                    // Set the nearby devices on a heat map
                    setNearbyDevicesOnHeatMap();
                    // Set the nearby places on the map
                    setNearbyPlacesOnMap();
                }

                // Set the blue dot location
                map.setMyLocationEnabled(true);
            }
        }
    }

    /**
     * Parse the list of allowed places types to a list of string representing the types
     * @param allowedPlacesTypes AllowedPlacesTypes list
     * @return List of string types
     */
    private List<String> processAllowedPlacesTypes (ArrayList<AllowedPlacesType> allowedPlacesTypes){
        List<String> allowedTypes = new ArrayList<>();
        for (AllowedPlacesType allowedPlacesType : allowedPlacesTypes){
            // If it is checked by the user
            if (allowedPlacesType.isChecked())
                allowedTypes.add(allowedPlacesType.getType());
        }
        return allowedTypes;
    }

    /**
     * Load and set the nearby places on the map
     */
    private void setNearbyPlacesOnMap(){
        // If the fragment is attached to an activity
        if (isAdded()) {
            Log.d(TAG, "Loading nearby locations by distance");

            // Clear previous places
            map.clear();

            // Initialize allowedPlaces list
            allowedPlaces = new ArrayList<>();

            // Get location string
            String locationString = curLocation.getLatitude() + "," + curLocation.getLongitude();

            // Load all the allowed places within a location
            AllowedPlacesRepository.getInstance(Objects.requireNonNull(requireActivity().getApplication())).loadAllowedPlacesByLocation(locationString);

            // Get parsed types
            List<String> queryTypes = processAllowedPlacesTypes(allowedPlacesTypes);

            // Get all the allowed places by specific types
            allowedPlacesViewModel.getAllAllowedPlacesByTypes(queryTypes).observe(requireActivity(), allAllowedPlaces -> {
                allowedPlaces.addAll(allAllowedPlaces);
                // Add places to the map
                addPlacesToMap(allowedPlaces);
            });
        }
    }

    /**
     * Add a list of places to the map
     * @param allowedPlaces Allowed places to be set on the map
     */
    private void addPlacesToMap(ArrayList<AllowedPlaces> allowedPlaces){
        for (AllowedPlaces place : allowedPlaces) {
            Log.d(TAG, "New nearby places");
            // Create a place location
            Location placeLocation = new Location("");
            placeLocation.setLatitude(place.getGeo_lat());
            placeLocation.setLongitude(place.getGeo_long());

            if (curLocation.distanceTo(placeLocation) < Constants.NEARBY_ALLOWED_PLACES_MIN_DISTANCE) {
                // If the place is nearby the current location

                // Create a async task to set the markers and icons on the map
                ArrayList<Object> taskParameters = new ArrayList<>();
                // First parameter is the icon to load
                taskParameters.add(place.getIcon());
                // Second one is the location (LatLng)
                taskParameters.add(new LatLng(place.getGeo_lat(), place.getGeo_long()));
                // Third one is the place name
                taskParameters.add(place.getName());
                // Execute the task
                //noinspection unchecked
                new IconLoaderTask().execute(taskParameters);
            }
        }
    }

    // ------- Heat Map ------- //
    /**
     * Parse the list of nearby devices to a list of LatLng representing the devices
     * @param nearbyDevices NearbyDevices list
     * @return List of string types
     */
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
        // If the fragment is attached to an activity
        if (isAdded()) {
            // Load the nearby devices by the current location
            NearbyDevicesRepository.getInstance(requireActivity().getApplication()).loadNearbyDevices(curLocation.getLatitude(), curLocation.getLongitude(), Constants.NEARBY_DEVICES_RADIUS, Constants.NEARBY_DEVICES_MINS);

            // Get all the nearby devices
            nearbyDevicesViewModel.getNearbyDevices().observe(requireActivity(), allNearbyDevices -> {
                nearbyDevices.clear();
                nearbyDevices.addAll(allNearbyDevices);
                // Add the nearby devices to a heat map
                addHeatMap(processNearbyDevices(nearbyDevices));
            });
        }
    }

    /**
     * Add a heat map overlay to the map
     * @param nearbyDevicesLatLng LatLng items to set in the heat map
     */
    private void addHeatMap(ArrayList<LatLng> nearbyDevicesLatLng){
        // Create a heat map tile provider, passing it the lat lng of the nearby devices.
        if (!nearbyDevicesLatLng.isEmpty()) {
            if (mOverlay != null) {
                // Remove the overlay is there is no nearby devices
                mOverlay.remove();
            }

            // Create the heat map gradient
            Gradient gradient = new Gradient(Constants.COLORS, Constants.STARTPOINTS);

            // Create the TileProvider
            TileProvider mProvider = new HeatmapTileProvider.Builder()
                    .data(nearbyDevicesLatLng)
                    .gradient(gradient)
                    .radius(Constants.OVERLAY_RADIUS)
                    .opacity(Constants.OVERLAY_OPACITY)
                    .build();

            // Add a tile overlay to the map, using the heat map tile provider.
            mOverlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
        }

    }

    // Allowed Places Icon Loader
    @SuppressLint("StaticFieldLeak")
    private class IconLoaderTask extends AsyncTask <ArrayList<Object>, String, Bitmap>
    {
        private LatLng position;
        private String title;

        /**
         * doInBackground method
         * @param arrayLists ArrayList of params
         * @return A bitmap with the icon
         */
        @SafeVarargs
        @Override
        protected final Bitmap doInBackground(ArrayList<Object>... arrayLists) {
            URL icon_url;
            Bitmap bmp = null;
            try {
                // First parameter is the icon to load
                icon_url = new URL(arrayLists[0].get(0).toString());
                // Second one is the location (LatLng)
                position = (LatLng) arrayLists[0].get(1);
                // Third one is the place name
                title = arrayLists[0].get(2).toString();
                // Create the bpm with the icon
                bmp = BitmapFactory.decodeStream(icon_url.openConnection().getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bmp;
        }

        /**
         * onPreExecute method
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * onPostExecute method
         * @param result Bitmap icon result of the doInBackground method
         */
        @Override
        protected void onPostExecute(Bitmap result) {
            // If the fragment is attached to an activity
            if (isAdded()) {
                super.onPostExecute(result);
                // Add the marker to the map
                map.addMarker(new MarkerOptions()
                        .position(position)
                        .title(title)
                        .icon(BitmapDescriptorFactory.fromBitmap(result)));
            }
        }
    }
}
