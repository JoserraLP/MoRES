package com.unex.tfg.receiver;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;

import com.unex.tfg.R;
import com.unex.tfg.mqtt.MQTTClient;
import com.unex.tfg.data.repository.AllowedPlacesTypeRepository;
import com.unex.tfg.data.repository.NewsRepository;
import com.unex.tfg.model.News;
import com.unex.tfg.service.ForegroundService;
import com.unex.tfg.utils.Constants;
import com.unex.tfg.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Receiver extends BroadcastReceiver {

    // TAG for Log messaging
    private static final String TAG = Receiver.class.getSimpleName();

    // Notification Channel name
    private static final String CHANNEL_ID = "channel_receiver";

    // Current location of the user
    private Location curLocation;

    // Application for notifications
    private Application application;

    // MQTT client
    private MQTTClient mqttClient;

    // Load allowed places types flag
    private boolean loadAllowedPlacesTypes;

    /**
     * Receiver constructor
     * @param application Application
     */
    public Receiver(Application application){
        this.application = application;
        this.loadAllowedPlacesTypes = true;
        // Get MQTT Client instance
        mqttClient = MQTTClient.getInstance(application.getApplicationContext());

        // Create an array of topics to subscribe to
        ArrayList<String> topics = new ArrayList<>();
        topics.add(Constants.MQTT_TOPIC_NEWS); // News topic
        topics.add(Constants.MQTT_TOPIC_ALLOWED_PLACES_TYPES); // AllowedPlacesTypes topic

        // Start MQTT client connection with the topics
        mqttClient.startConnection(topics);
    }

    /**
     * Notification constructor
     * @param context Notification context
     * @param title Notification title
     * @param description Notification description
     */
    private void createNotification (Context context, String title, String description) {

        // Create the Notification builder
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launch)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Create the Notification Manager
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {

            // Notification channel name
            CharSequence name = context.getString(R.string.app_name);

            // Set the Notification channel
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            notificationManager.createNotificationChannel(mChannel);

            Random random = new Random();

            // notificationId is a unique int for each notification, in this case is generated randomly
            notificationManager.notify(random.nextInt(), builder.build());
        }
    }

    /**
     * Receiver for broadcasts sent by {@link ForegroundService} and {@link MQTTClient}.
     * @param context Receiver context
     * @param intent Receiver intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        Location location = intent.getParcelableExtra(ForegroundService.EXTRA_LOCATION);
        // Receive a new location
        if (location != null) {
            try {
                onLocationReceived(location, context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String news = intent.getStringExtra(MQTTClient.EXTRA_NEWS);
        // Receive a news item from MQTT
        if (news != null && curLocation != null){
            // If the current location is not null, because it is used for the relevance on the news for the final user
            onNewsItemReceived(news, context);
        }

        int allowedPlacesTypes = intent.getIntExtra(MQTTClient.EXTRA_ALLOWED_PLACES_TYPES, 0);
        // Receive an update notification from MQTT
        if (allowedPlacesTypes == 1) {
            Log.d(TAG, "Loading new allowed places types");

            String locality = getLocalityByLocation(context, curLocation);

            // Load the updated allowed places types
            AllowedPlacesTypeRepository.getInstance(this.application).loadAllAllowedPlacesTypes(locality);
        }
    }

    /**
     * Process the received location and publish it through MQTT
     * @param location Received location
     * @param context Receiver context
     */
    private void onLocationReceived(Location location, Context context) throws IOException {
        Log.d(TAG, location.toString());
        if (curLocation != null) {
            if (curLocation.getLongitude() != location.getLongitude() && curLocation.getLatitude() != location.getLatitude()) {
                // If the location is not the same as the previous one
                // update the current location
                curLocation = location;

                // Save the device ID on the preferences of the app
                SharedPreferences pref = application.getSharedPreferences(Constants.PREFERENCES_NAME, Constants.PREFERENCES_MODE);

                String deviceID = pref.getString(Constants.PREFERENCES_DEVICE_ID, null);
                if (deviceID != null) {
                    // If the device has an ID
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("_id", deviceID);
                        jsonObject.put("geo_lat", location.getLatitude());
                        jsonObject.put("geo_long", location.getLongitude());
                        mqttClient.publish(Constants.MQTT_TOPIC_LOCATION, jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else
            curLocation = location;

        if (loadAllowedPlacesTypes && curLocation != null){

            loadAllowedPlacesTypes = false;
            String locality = getLocalityByLocation(context, curLocation);

            // Load the updated allowed places types
            AllowedPlacesTypeRepository.getInstance(this.application).loadAllAllowedPlacesTypes(locality);
        }
    }

    /**
     * Process the received news item and store it to the database
     * @param news News object
     * @param context Receiver context
     */
    private void onNewsItemReceived(String news, Context context){
        Log.d(TAG, news);
        try {
            // Parse the news item to JSON
            JSONObject jsonObject = new JSONObject(news);

            // Create a Geocoder instance
            Geocoder geocoder = new Geocoder(context);

            // Get the address of the current location
            List<Address> addresses = geocoder.getFromLocation(curLocation.getLatitude(), curLocation.getLongitude(), 1);

            // Retrieve news item fields that specify its relevance in order to check if its relevant for the user.
            String newsLocation = jsonObject.getString("location");
            String newsExpansion = jsonObject.getString("expansion");
            int newsRelevance = jsonObject.getInt("relevance");

            // Flag to determine if a news item is relevant for the user
            boolean relevant = false;

            for (Address address : addresses) {
                if (newsRelevance == 0) {
                    // 0 means that the news item is not urgent or very important
                    if ((newsExpansion.equals("Locality") && address.getLocality().equals(newsLocation))
                            || (newsExpansion.equals("AdminArea") && address.getAdminArea().equals(newsLocation))
                            || (newsExpansion.equals("Country") && address.getCountryName().equals(newsLocation))) {
                        // If the news location matches with the current location the news item is relevant for the user
                        relevant = true;
                        break;
                    }
                } else if (newsRelevance == 1) {
                    // 1 means that the news is urgent or very important

                    // Create a push notification for the urgent news item
                    createNotification(context, jsonObject.getString("title"), jsonObject.getString("description"));

                    relevant = true;
                    break;
                }
            }

            // If the news item is relevant for the user
            if (relevant) {
                // Create the news item
                News newsItem = parseJSONtoNews(jsonObject);

                // Insert the news item in the database
                NewsRepository.getInstance(this.application).insertNews(new MutableLiveData<>(newsItem));
            } else
                Log.d(TAG, "The news item is no relevant for the user");

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Parse a JSON object to a News object
     * @param jsonObject News item represented in JSON
     * @return Parsed News object
     * @throws JSONException if the parse fails
     */
    private News parseJSONtoNews (JSONObject jsonObject) throws JSONException {
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

    /**
     * Get locality name by current location
     * @param context Application context
     * @param curLocation Current device location
     * @return String with the locality
     */
    public String getLocalityByLocation (Context context, Location curLocation){
        // Create a Geocoder instance
        Geocoder geocoder = new Geocoder(context);
        // Get the address of the current location
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(curLocation.getLatitude(), curLocation.getLongitude(), 1);
            return addresses.get(0).getLocality();
        } catch (IOException e) {
            Log.e(TAG, Objects.requireNonNull(e.getLocalizedMessage()));
        }

        return "";
    }
}

