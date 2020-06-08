package com.unex.tfg.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.unex.tfg.R;
import com.unex.tfg.mqtt.MQTTClient;
import com.unex.tfg.receiver.Receiver;
import com.unex.tfg.utils.Constants;
import com.unex.tfg.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class ForegroundService extends Service {

    // TAG for Log messaging
    private static final String TAG = ForegroundService.class.getSimpleName();

    // Package name
    private static final String PACKAGE_NAME = ForegroundService.class.getName();

    /** Notification **/
    // Notification manager
    private NotificationManager mNotificationManager;

    // The name of the channel for notifications.
    private static final String CHANNEL_ID = "channel_01";

    // The identifier for the notification displayed for the foreground service.
    private static final int NOTIFICATION_ID = 123456789;

    /** Service **/
    // Used to check whether the bound activity has really gone away and not unbound as part of an orientation change
    private boolean mChangingConfiguration = false;

    // Service handler
    private Handler mServiceHandler;

    // Service binder
    private final IBinder mBinder = new LocalBinder();

    /** Location **/
    // Location request, it contains parameters used by {@link LocationServices}.
    private LocationRequest mLocationRequest;

    // Provides access to the Fused Location Provider API
    private FusedLocationProviderClient mFusedLocationClient;

    // Callback for location changes
    private LocationCallback mLocationCallback;

    // Current location.
    private Location mLocation;

    /** Broadcast **/
    // Broadcast action string
    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";

    // String representing the location broadcast
    public static final String EXTRA_LOCATION = PACKAGE_NAME + ".location";

    // Broadcast receiver
    private Receiver receiver;

    /**
     * ForegroundService empty constructor
     */
    public ForegroundService() {
    }

    /**
     * onCreate method
     */
    @Override
    public void onCreate() {
        // ------- Location ------- //
        // Get the Fused Location Provider Client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Define the location callback
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult.getLastLocation());
            }
        };

        // Create a default location request
        createLocationRequest();

        // Retrieve the last known location
        getLastLocation();

        // ------- Service ------- //
        // Start a thread to run the service
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());

        // ------- Notification ------- //
        // Define the Notification Manager
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Set the channel name as the application name
        CharSequence name = getString(R.string.app_name);

        // Create the channel for the notification
        NotificationChannel mChannel =
                new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

        // Set the Notification Channel for the Notification Manager.
        mNotificationManager.createNotificationChannel(mChannel);

        // ------- Receiver ------- //
        // Register the receiver
        receiver = new Receiver(getApplication());

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(ForegroundService.ACTION_BROADCAST));

        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(MQTTClient.ACTION_BROADCAST));

    }

    /**
     * Set the location request values depending of the device battery level
     */
    private void setLocationRequestByBattery(){
        // Retrieve the battery Intent Filter
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);

        // Retrieve the battery status Intent
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        assert batteryStatus != null;

        // Retrieve the battery level and scale
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Calculate the battery level percentage
        float batteryPct = level * 100 / (float) scale;

        if (batteryPct >= Constants.BATTERY_UPPER_BOUND || batteryPct == 0.0) { // 0.0 is 100%
            Log.d(TAG, " Battery above " + Constants.BATTERY_UPPER_BOUND + "%");
            updateLocationRequest(Constants.UPPER_UPDATE_INTERVAL_IN_MILLISECONDS, Constants.UPPER_FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS ,LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        else if (batteryPct >= Constants.BATTERY_LOWER_BOUND) {
            Log.d(TAG, " Battery above " + Constants.BATTERY_LOWER_BOUND + "%");
            updateLocationRequest(Constants.MID_UPDATE_INTERVAL_IN_MILLISECONDS, Constants.MID_FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS,  LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
        else if (batteryPct < Constants.BATTERY_LOWER_BOUND){
            Log.d(TAG, " Battery under " + Constants.BATTERY_LOWER_BOUND + "%");
            updateLocationRequest(Constants.LOWER_UPDATE_INTERVAL_IN_MILLISECONDS, Constants.LOWER_FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        }
    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.UPPER_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(Constants.UPPER_FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Update the location request
     * @param interval Location request interval
     * @param fastestInterval Location request fastest interval
     * @param priority Location request priority
     */
    public void updateLocationRequest(long interval, long fastestInterval, int priority){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(fastestInterval);
        mLocationRequest.setPriority(priority);
    }

    /**
     * onStartMethod
     * @param intent Service intent
     * @param flags Service flags
     * @param startId Service ID
     * @return Service preferences
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Service started");

        // Update location request by battery level
        setLocationRequestByBattery();

        // Request location updates with the updated location request
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }

        // Tells the system to not try to recreate the service after it has been killed.
        return START_NOT_STICKY;
    }

    /**
     * onConfigurationChanged
     * @param newConfig New configuration
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    /**
     * Called when a client comes to the foreground and binds with this service.
     * The service should cease to be a foreground service when that happens.
     * @param intent Intent to bind
     * @return IBinder
     */
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "in onBind()");
        // Stop foreground service
        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    /**
     * Called when a client returns to the foreground and binds once again with this service.
     * The service should cease to be a foreground service when that happens.
     * @param intent Intent to rebind
     */
    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "in onRebind()");
        // Stop foreground service
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    /**
     * Called when the last client unbinds from this service.
     * @param intent Intent to unbind
     * @return Bind status
     */
    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Last client unbound from service");

        // If this method is called due to a configuration change nothing happens
        // Otherwise, we make this service a foreground service.
        if (!mChangingConfiguration) {
            Log.i(TAG, "Starting foreground service");
            // Start foreground service
            startForeground(NOTIFICATION_ID, getNotification());
        }
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    /**
     * onDestroy method
     */
    @Override
    public void onDestroy() {
        // Remove the service callbacks
        mServiceHandler.removeCallbacksAndMessages(null);

        // Unregister the receivers
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    /**
     * Makes a request for location updates.
     */
    public void requestLocationUpdates() {
        Log.i(TAG, "Requesting location updates");

        // Start the Foreground service
        startService(new Intent(getApplicationContext(), ForegroundService.class));

        // Request location updates with the updated location request
        try {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback, Looper.myLooper());
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
        }
    }

    /**
     * Get the last known location
     */
    private void getLastLocation() {
        try {
            // Get last location with the FusedLocationClient
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            Log.w(TAG, "Success to get location.");
                            // Set the result value
                            mLocation = task.getResult();

                            // Notify anyone listening for broadcasts about the new location.
                            Intent intent = new Intent(ACTION_BROADCAST);
                            intent.putExtra(EXTRA_LOCATION, mLocation);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                        } else {
                            Log.w(TAG, "Failed to get location.");
                        }
                    });
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }

    /**
     * Get new location and broadcast it
     * @param location new location
     */
    private void onNewLocation(Location location) {
        // Notify anyone listening for broadcasts about the new location.
        Intent intent = new Intent(ACTION_BROADCAST);
        intent.putExtra(EXTRA_LOCATION, location);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);

        // Update notification content if running as a foreground service.
        if (serviceIsRunningInForeground(this)) {
            mNotificationManager.notify(NOTIFICATION_ID, getNotification());
        }
    }


    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     * @return Notification
     */
    private Notification getNotification() {
        // Notification text
        CharSequence text = Utils.getLocationText(mLocation);

        // Create notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentText(text)
                .setContentTitle(Utils.getLocationTitle(this))
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());

        // Set the Channel ID
        builder.setChannelId(CHANNEL_ID);

        return builder.build();
    }

    /**
     * Returns true if this is a foreground service.
     * @param context Context
     * @return state of the foreground service
     */
    @SuppressWarnings("deprecation")
    public boolean serviceIsRunningInForeground(Context context) {
        // Get activity manager
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            // Check if the actual service is running foreground
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Class used for the client Binder. Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public ForegroundService getService() {
            return ForegroundService.this;
        }
    }
}
