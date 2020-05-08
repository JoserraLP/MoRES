package com.example.mqtt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mqtt.client.MQTTClient;
import com.example.mqtt.data.repository.NewsRepository;
import com.example.mqtt.model.News;
import com.example.mqtt.service.BackgroundService;
import com.example.mqtt.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class DrawerActivity extends AppCompatActivity {

    private static final String TAG = DrawerActivity.class.getSimpleName();

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    private AppBarConfiguration mAppBarConfiguration;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    private BackgroundService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    private Location curLocation;

    // Monitors the state of the connection to the service.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        myReceiver = new MyReceiver();

        // Check that the user hasn't revoked permissions by going to Settings.
        if (Utils.requestingLocationUpdates(this)) {
            if (checkPermissions()) {
                requestPermissions();
            }
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_news, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Check that the user hasn't revoked permissions by going to Settings.
        if (Utils.requestingLocationUpdates(this)) {
            if (checkPermissions()) {
                requestPermissions();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (checkPermissions()) {
            requestPermissions();
        } else {
            mService.requestLocationUpdates();
        }

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, BackgroundService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE);

    }


    @Override
    protected void onResume() {
        super.onResume();
        // Register Location receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(BackgroundService.ACTION_BROADCAST));


        LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
                new IntentFilter(MQTTClient.ACTION_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myReceiver);
    }

    @Override
    protected void onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            unbindService(mServiceConnection);
            mBound = false;
        }

        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /**
     * Returns the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @SuppressLint("ResourceType")
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.layout.activity_main),
                    "Location permission is needed for core functionality"
                    ,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", view -> {
                        // Request permission
                        ActivityCompat.requestPermissions(DrawerActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_PERMISSIONS_REQUEST_CODE);
                    })
                    .show();

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(DrawerActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                mService.requestLocationUpdates();
            } else {
                // Permission denied.
                Log.i(TAG, "Permission denied.");
            }
        }
    }


    // TODO extract receiver
    /**
     * Receiver for broadcasts sent by {@link BackgroundService}.
     */
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(BackgroundService.EXTRA_LOCATION);
            if (location != null) {
                Log.d(TAG, location.toString());
                curLocation = location;
            }
            String news = intent.getStringExtra(MQTTClient.EXTRA_NEWS);
            if (news != null && curLocation != null){
                Log.d(TAG, news);
                try {
                    JSONObject jsonObject = new JSONObject(news);
                    // Create the news object
                    News newsItem = new News();

                    Geocoder geocoder = new Geocoder(context);
                    List<Address> addresses = geocoder.getFromLocation(curLocation.getLatitude(), curLocation.getLongitude(), 1);
                    boolean accepted = false;

                    String newsLoc = jsonObject.getString("location");
                    String newsRel = jsonObject.getString("relevance");

                    for (Address address : addresses){
                        if (address.getLocality().equals(newsLoc)){
                            accepted = true;
                            break;
                        } else if (newsRel.toLowerCase().equals("relevant") && address.getCountryName().equals(newsLoc)){
                            accepted = true;
                            break;
                        } else if (newsRel.toLowerCase().equals("urgent")){ // TODO Here add a notification
                            accepted = true;
                            break;
                        }
                    }
                    if (accepted) {
                        newsItem.setTitle(jsonObject.getString("title"));
                        newsItem.setDescription(jsonObject.getString("description"));
                        newsItem.setDate(jsonObject.getString("date"));
                        newsItem.setLocation(newsLoc);
                        newsItem.setImage(jsonObject.getString("image"));
                        newsItem.setRelevance(newsRel);

                        NewsRepository.getInstance(getApplication()).insertNews(new MutableLiveData<>(newsItem));
                    } else
                        Log.d(TAG, "The news item is no relevant for the user");

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
