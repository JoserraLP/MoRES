package com.unex.tfg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.unex.tfg.data.repository.DeviceIDRepository;
import com.unex.tfg.service.ForegroundService;
import com.unex.tfg.utils.Constants;

import java.util.Locale;

public class DrawerActivity extends AppCompatActivity {

    // TAG for Log messaging
    private static final String TAG = DrawerActivity.class.getSimpleName();

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // Configuration for the App Bar
    private AppBarConfiguration mAppBarConfiguration;

    // A reference to the background service used.
    private ForegroundService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    // Monitors the state of the connection to the service.
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Get the service binder
            ForegroundService.LocalBinder binder = (ForegroundService.LocalBinder) service;
            // Save the service
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    /**
     * onCreate method
     * @param savedInstanceState Bundle with data
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ------- UI ------- //
        setContentView(R.layout.activity_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder
                (R.id.nav_map, R.id.nav_news, R.id.nav_filter ,R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();

        // Set the basic nav actions
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // ------- Settings ------- //
        // Check if the device has an ID
        SharedPreferences pref = getApplication().getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE);

        if (pref.getString(Constants.PREFERENCES_DEVICE_ID, null) == null) {
            Log.d(TAG, "Retrieving Device ID");
            // Load the device ID
            DeviceIDRepository.getInstance(getApplication()).loadDeviceID();

        }

        // Check app language and set it on the app
        String languageCode = getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE).getString(Constants.PREFERENCES_LANGUAGE, Constants.PREFERENCES_LANGUAGE_DEF_VALUE);
        if (!languageCode.equals(Locale.getDefault().getLanguage()))
            putLanguage(languageCode);

        // ------- User permissions ------- //
        // Check that the user hasn't revoked permissions
        if (checkPermissions()) {
            requestPermissions();
        }

    }

    /**
     * Set the language app
     * @param languageCode language code to be set
     */
    @SuppressWarnings("deprecation")
    public void putLanguage(String languageCode) {
        // Create a Locale with the language code
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        // Configure the Locale
        Configuration config = getResources().getConfiguration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    /**
     * onStart method
     */
    @Override
    protected void onStart() {
        super.onStart();

        // Bind to the service. If the service is in foreground mode, this signals to the service
        // that since this activity is in the foreground, the service can exit foreground mode.
        bindService(new Intent(this, ForegroundService.class), mServiceConnection,
             Context.BIND_AUTO_CREATE);

    }

    /**
     * onResume method
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Check that the user hasn't revoked permissions
        if (checkPermissions()) {
            requestPermissions();
        } else {
            // Start the service if the permissions were granted
            Log.d(TAG, "On resume with permissions");
            startService(new Intent(this, ForegroundService.class));
        }
    }

    /**
     * onDestroy method
     */
    @Override
    public void onDestroy(){
        super.onDestroy();
        // If the service is running, stop it
        if (mService != null)
            mService.stopSelf();
        // Stop the foreground service
        stopService(new Intent(this, ForegroundService.class));
    }

    /**
     * onStop method
     */
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

    /**
     * Allows to navigate up in the navigation drawer
     * @return If navigation up is supported
     */
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // ------- User permissions ------- //

    /**
     * Returns the current state of the permissions needed.
     * @return Permissions state
     */
    private boolean checkPermissions() {
        return PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * Request for user permissions
     */
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
                    findViewById(R.id.snackbar_view),
                    R.string.request_permission_toast_msg,
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
     * @param requestCode Permission request code
     * @param permissions Permissions requested
     * @param grantResults Permissions results
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
                // Request location updates of the foreground service
                if (mService != null)
                    mService.requestLocationUpdates();
            } else {
                // Permission denied.
                Log.i(TAG, "Permission denied.");
            }
        }
    }

}
