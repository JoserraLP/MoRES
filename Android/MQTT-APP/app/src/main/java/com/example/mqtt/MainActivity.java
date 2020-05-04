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
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mqtt.client.MQTTClient;
import com.example.mqtt.service.BackgroundService;
import com.example.mqtt.utils.MQTTConfiguration;
import com.example.mqtt.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    // MQTT client
    private MQTTClient mqttClient;

    // Used in checking for runtime permissions.
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private MyReceiver myReceiver;

    // A reference to the service used to get location updates.
    private BackgroundService mService = null;

    // Tracks the bound state of the service.
    private boolean mBound = false;

    // UI elements
    Button connect_btn;
    Button disconnect_btn;

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
        setContentView(R.layout.activity_main);

        connect_btn = findViewById(R.id.connect_btn);
        disconnect_btn = findViewById(R.id.disconnect_btn);

        myReceiver = new MyReceiver();

        // Check that the user hasn't revoked permissions by going to Settings.
        if (Utils.requestingLocationUpdates(this)) {
            if (checkPermissions()) {
                requestPermissions();
            }
        }

        mqttClient = MQTTClient.getInstance(this);

        connect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttClient.startConnection();
                TextView MQTT_Server = findViewById(R.id.Server_MQTT);
                MQTT_Server.setText(MQTTConfiguration.MQTT_BROKER_URL.substring(6));
                setButtonsState(true);
            }
        });

        disconnect_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mqttClient.disconnect();
                TextView MQTT_Server = findViewById(R.id.Server_MQTT);
                MQTT_Server.setText(R.string.no_conn);
                setButtonsState(false);
            }
        });

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
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
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

    /**
     * Receiver for broadcasts sent by {@link BackgroundService}.
     */
    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Location location = intent.getParcelableExtra(BackgroundService.EXTRA_LOCATION);
            if (location != null) {
                TextView loc_value = findViewById(R.id.location_value);
                loc_value.setText(String.format("(%s, %s)", location.getLatitude(), location.getLongitude()));
                mqttClient.publish("Location", String.format("%s, %s", location.getLatitude(), location.getLongitude()));
            }
            Intent batStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            if (batStatus != null) {
                // TODO mirar el register receiver de los datos y el alarm
                int level = batStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

                float batteryPct = level * 100 / (float) scale;

                if (batteryPct != 0.0) {
                    TextView bat_value = findViewById(R.id.battery_value);
                    bat_value.setText(String.format("%s %%", batteryPct));
                }
            }


        }
    }

    private void setButtonsState(boolean connected) {
        if (connected) {
            disconnect_btn.setEnabled(true);
            connect_btn.setEnabled(false);
        } else {
            disconnect_btn.setEnabled(false);
            connect_btn.setEnabled(true);
        }
    }
}

