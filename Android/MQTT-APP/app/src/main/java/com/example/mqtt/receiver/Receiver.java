package com.example.mqtt.receiver;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.mqtt.DrawerActivity;
import com.example.mqtt.R;
import com.example.mqtt.client.MQTTClient;
import com.example.mqtt.data.repository.AllowedPlacesRepository;
import com.example.mqtt.data.repository.AllowedPlacesTypeRepository;
import com.example.mqtt.data.repository.NewsRepository;
import com.example.mqtt.model.AllowedPlaces;
import com.example.mqtt.model.News;
import com.example.mqtt.service.ForegroundService;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Receiver extends BroadcastReceiver {

    private static final String TAG = Receiver.class.getSimpleName();

    // Current location of the user
    private Location curLocation;

    // Notification Channel name
    private static final String CHANNEL_ID = "channel_receiver";

    // Application for notifications
    private Application application;

    // MQTT client
    private MQTTClient mqttClient;

    // For intent creation
    private static final String PACKAGE_NAME = Receiver.class.getName();

    private static final String EXTRA_STARTED_FROM_NOTIFICATION = PACKAGE_NAME +
            ".started_from_notification";

    private Location lastNearbyLocation;


    public Receiver(Application application){
        this.application = application;
        mqttClient = MQTTClient.getInstance(application.getApplicationContext());
        mqttClient.startConnection();
    }

    /**
     * Receiver for broadcasts sent by {@link ForegroundService} and {@link MQTTClient}.
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        // Receive a new location
        Location location = intent.getParcelableExtra(ForegroundService.EXTRA_LOCATION);
        if (location != null) {
            Log.d(TAG, location.toString());
            if (curLocation != null) {
                if (curLocation.getLongitude() != location.getLongitude() && curLocation.getLatitude() != location.getLatitude()) {
                    curLocation = location;

                    if (lastNearbyLocation != null) {
                        if (curLocation.distanceTo(lastNearbyLocation) > 200) { // TODO load near places. Do this with the location from the first time for example.
                            lastNearbyLocation = curLocation;
                            Log.d(TAG, "Loading nearby locations by distance");
                            String locationString = curLocation.getLatitude() + "," + curLocation.getLongitude();
                            // Load all the allowed places
                            AllowedPlacesRepository.getInstance(Objects.requireNonNull(application)).loadAllowedPlacesByLocation(locationString);
                        }


                    } else {
                        Log.d(TAG, "Loading nearby locations");
                        lastNearbyLocation = curLocation;
                        String locationString = curLocation.getLatitude() + "," + curLocation.getLongitude();
                        // Load all the allowed places
                        AllowedPlacesRepository.getInstance(Objects.requireNonNull(application)).loadAllowedPlacesByLocation(locationString);
                    }


                    SharedPreferences pref = application.getSharedPreferences("MyPref", 0); // 0 - for private mode
                    String deviceID = pref.getString("DeviceID", null);
                    if (deviceID != null) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("_id", deviceID);
                            jsonObject.put("geo_lat", location.getLatitude());
                            jsonObject.put("geo_long", location.getLongitude());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mqttClient.publish("Location", jsonObject.toString());
                    }

                }
                else
                    return;
            }
            else {
                curLocation = location;
                if (lastNearbyLocation != null) {
                    if (curLocation.distanceTo(lastNearbyLocation) > 200) { // TODO load near places. Do this with the location from the first time for example.
                        lastNearbyLocation = curLocation;
                        Log.d(TAG, "Loading nearby locations by distance");
                        String locationString = curLocation.getLatitude() + "," + curLocation.getLongitude();
                        // Load all the allowed places
                        AllowedPlacesRepository.getInstance(Objects.requireNonNull(application)).loadAllowedPlacesByLocation(locationString);
                    }


                } else {
                    Log.d(TAG, "Loading nearby locations");
                    lastNearbyLocation = curLocation;
                    String locationString = curLocation.getLatitude() + "," + curLocation.getLongitude();
                    // Load all the allowed places
                    AllowedPlacesRepository.getInstance(Objects.requireNonNull(application)).loadAllowedPlacesByLocation(locationString);
                }
            }


        }

        // Receive a new news item
        String news = intent.getStringExtra(MQTTClient.EXTRA_NEWS);
        if (news != null && curLocation != null){
            Log.d(TAG, news);
            try {
                // Parse the news item to JSON
                JSONObject jsonObject = new JSONObject(news);

                // Create a geocoder instance
                Geocoder geocoder = new Geocoder(context);

                // Get the address of the current location
                List<Address> addresses = geocoder.getFromLocation(curLocation.getLatitude(), curLocation.getLongitude(), 1);

                boolean relevant = false;

                // Retrieve news item relevant fields in order to check if its relevant for the user.
                String newsLoc = jsonObject.getString("location");
                String newsExp = jsonObject.getString("expansion");
                int newsRel = jsonObject.getInt("relevance");

                for (Address address : addresses) {

                    if (newsRel == 0) {
                        if ((newsExp.equals("Locality") && address.getLocality().equals(newsLoc))
                                || (newsExp.equals("AdminArea") && address.getAdminArea().equals(newsLoc))
                                || (newsExp.equals("Country") && address.getCountryName().equals(newsLoc))) {
                            relevant = true;
                            break;
                        }
                    } else if (newsRel == 1) { // Urgent

                        // Create a push notification if the news is urgent
                        createNotification(context, jsonObject.getString("title"), jsonObject.getString("description"));

                        relevant = true;
                        break;
                    }
                }

                // If the news is relevant for the user
                if (relevant) {
                    News newsItem = processJSONNews(jsonObject);
                    NewsRepository.getInstance(this.application).insertNews(new MutableLiveData<>(newsItem));
                } else
                    Log.d(TAG, "The news item is no relevant for the user");

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        }

        // Receive an update from AllowedPlaces topic
        int allowedPlaces = intent.getIntExtra(MQTTClient.EXTRA_ALLOWED_PLACES_TYPES, 0);
        if (allowedPlaces == 1) {
            Log.d(TAG, "Loading new allowed places types");
            AllowedPlacesTypeRepository.getInstance(this.application).loadAllAllowedPlacesTypes();
        }
    }

    public News processJSONNews (JSONObject jsonObject) throws JSONException {
        News newsItem = new News();
        newsItem.setAuthor(jsonObject.getString("author"));
        newsItem.setTitle(jsonObject.getString("title"));
        newsItem.setDescription(jsonObject.getString("description"));
        newsItem.setDate(jsonObject.getString("date"));
        newsItem.setLocation(jsonObject.getString("location"));
        newsItem.setImage(jsonObject.getString("image"));
        newsItem.setRelevance(jsonObject.getInt("relevance"));
        newsItem.setExpansion(jsonObject.getString("expansion"));
        return newsItem;
    }

    public void createNotification (Context context, String title, String description) {

        Intent intent = new Intent(context, Receiver.class);

        // Extra to help us figure out if we arrived in onStartCommand via the notification or not.
        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION, true);

        // The PendingIntent to launch activity.
        PendingIntent activityPendingIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, DrawerActivity.class), 0);

        // Create the Notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .addAction(R.drawable.ic_launch, context.getString(R.string.launch_activity),
                        activityPendingIntent)
                .setSmallIcon(R.drawable.ic_launch)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Create the Notification Manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {

            // Android O requires a Notification Channel.
            CharSequence name = context.getString(R.string.app_name);

            // Set the Notification channel
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            notificationManager.createNotificationChannel(mChannel);

            Random random = new Random();

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(random.nextInt(), builder.build());
        }
    }
}

