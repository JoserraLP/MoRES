package com.example.mqtt.ui.home;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.Navigation;

import com.example.mqtt.DrawerActivity;
import com.example.mqtt.R;
import com.example.mqtt.service.BackgroundService;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.internal.NavigationMenu;

import java.util.Objects;

import static android.location.LocationManager.GPS_PROVIDER;

public class HomeFragment extends Fragment implements OnMapReadyCallback {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private GoogleMap map;

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 34;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private LocationReceiver locationReceiver;

    private Location curLocation;

    private Float zoom = 12.0f;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

        Log.d(TAG, "Home fragment created");

        locationReceiver = new LocationReceiver();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register Location receiver
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(locationReceiver,
                new IntentFilter(BackgroundService.ACTION_BROADCAST));

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    private boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this.getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // permission was granted, yay! Do the
                // location-related task you need to do.
                if (ContextCompat.checkSelfPermission(this.requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    //Request location updates:
                    //String provider = "";
                    //locationManager.requestLocationUpdates(provider, 400, 1, (LocationListener) this);
                }

            }
        }

    }

    // TODO extract receiver
    /**
     * Receiver for broadcasts sent by {@link BackgroundService}.
     */
    public class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(BackgroundService.EXTRA_LOCATION);
            if (location != null && (curLocation == null || !curLocation.equals(location))) {
                curLocation = location;
                map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Current location"));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(curLocation.getLatitude(), curLocation.getLongitude()), zoom));
            }
        }
    }
}
