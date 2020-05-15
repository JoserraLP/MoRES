package com.example.mqtt.ui.home;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mqtt.R;
import com.example.mqtt.model.AllowedPlacesType;
import com.example.mqtt.service.ForegroundService;
import com.example.mqtt.ui.home.viewmodel.AllowedPlacesTypeViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private GoogleMap map;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private LocationReceiver locationReceiver;

    private Location curLocation;

    private ArrayList<Place> nearbyPlaces;

    private Marker prevMarker;

    private Float zoom = 15.0f;

    private PlacesClient placesClient;

    private List<Place.Field> placeFields;

    private ArrayList<AllowedPlacesType> allowedPlacesTypes;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Inflate the map fragment
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        if (savedInstanceState != null) {
            //Restore the fragment's state here

            nearbyPlaces = savedInstanceState.getParcelableArrayList("nearbyPlaces");
            curLocation = savedInstanceState.getParcelable("curLocation");

            for (Place place : nearbyPlaces)
                map.addMarker(new MarkerOptions().position(Objects.requireNonNull(place.getLatLng())).title(place.getName()));
            if (curLocation != null) {
                map.setMyLocationEnabled(true);
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), zoom));
            }
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

        // Initialize the location receiver
        locationReceiver = new LocationReceiver();

        // Initialize the SDK
        Places.initialize(requireContext(), getString(R.string.google_maps_key));

        // Create a new Places client instance
        placesClient = Places.createClient(requireContext());

        placeFields = new ArrayList<>();
        placeFields.add(Place.Field.NAME);
        placeFields.add(Place.Field.LAT_LNG);
        placeFields.add(Place.Field.TYPES);

        // Use fields to define the data types to return.
        // placeFields = Collections.singletonList(Place.Field.NAME);

        @SuppressWarnings("deprecation")
        AllowedPlacesTypeViewModel allowedPlacesTypeViewModel = ViewModelProviders.of(requireActivity()).get(AllowedPlacesTypeViewModel.class);

        allowedPlacesTypes = new ArrayList<>();

        allowedPlacesTypeViewModel.getAllAllowedPlaces().observe(requireActivity(), allAllowedPlaces -> allowedPlacesTypes.addAll(allAllowedPlaces));

        if (curLocation != null) {
            map.setMyLocationEnabled(true);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), zoom));
        }

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

        if (curLocation != null) {
            map.setMyLocationEnabled(true);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), zoom));
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Save the map instance
        map = googleMap;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's state here
        outState.putParcelable("curLocation", curLocation);
        outState.putParcelableArrayList("nearbyPlaces", nearbyPlaces);
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
                    if (curLocation.getLatitude() != location.getLatitude() && curLocation.getLongitude() != location.getLongitude())
                        curLocation = location;
                    else
                        return;
                } else
                    curLocation = location;

                map.setMyLocationEnabled(true);

                /* TODO This is commented because i dont know if the findCurrentPlace costs.
                // TODO This works but make it efficiently with location radius
                // Use the builder to create a FindCurrentPlaceRequest.
                FindCurrentPlaceRequest request =
                        FindCurrentPlaceRequest.newInstance(placeFields);


                // TODO Call findCurrentPlace and handle the response (first check that the user has granted permission).
                Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request); // TODO ver si esto hace que se cobre
                placeResponse.addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        FindCurrentPlaceResponse response = task.getResult();
                        if (response != null) {
                            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                                Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                                        placeLikelihood.getPlace().getName(),
                                        placeLikelihood.getLikelihood()));

                                // TODO Check types
                                // https://developers.google.com/places/android-sdk/reference/com/google/android/libraries/places/api/model/Place.Type

                                for (Place.Type type : Objects.requireNonNull(placeLikelihood.getPlace().getTypes())) {
                                    for (AllowedPlacesType allowedPlacesType : allowedPlaces) {
                                        if (allowedPlacesType.getType().equals(type.toString().toLowerCase()))
                                            map.addMarker(new MarkerOptions().position(Objects.requireNonNull(placeLikelihood.getPlace().getLatLng())).title(placeLikelihood.getPlace().getName()));
                                    }

                                }

                            }
                        }
                    } else {
                        Exception exception = task.getException();
                        if (exception instanceof ApiException) {
                            ApiException apiException = (ApiException) exception;
                            Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                        }
                    }
                });


                 */

                //TODO this is done with the Places API
                /* Now i dont have more request per day
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(getContext());
                String url ="https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + curLocation.getLatitude() + "," + curLocation.getLongitude() + "&radius=1500&key=AIzaSyDlR3UBqr8GuUTJuYsS3bJ6xozqc4jfnhw";
                Log.d(TAG, url);

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Display the first 500 characters of the response string.
                                try {
                                    JSONObject jsonObject = new JSONObject(response);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, "That didn't work!");
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

                 */
            }
        }
    }
}
