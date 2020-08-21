package com.unex.tfg.utils;

import android.content.Context;
import android.graphics.Color;

public class Constants {

    /** MQTT **/
    public static String MQTT_BROKER_URL = "tcp://90.169.70.108:1883";

    public static int MQTT_QOS = 0;

    public static String MQTT_TOPIC_NEWS = "News";

    public static String MQTT_TOPIC_ALLOWED_PLACES_TYPES = "AllowedPlacesTypes";

    public static String MQTT_TOPIC_LOCATION = "Location";

    /** Server API **/
    public static String API_SERVER_URL = "http://90.169.70.108:8080/";

    public static double NEARBY_DEVICES_RADIUS = 100;

    public static int NEARBY_DEVICES_MINS = 5;

    public static int NEARBY_ALLOWED_PLACES_MIN_DISTANCE = 200;

    /** Map Fragment **/
    public static float ZOOM = 18.0f;

    /** Heat map **/
    public static int [] COLORS = {
            Color.rgb(102, 225, 0), // green
            Color.rgb(255, 165, 0)  // red
    };

    public static float[] STARTPOINTS = {
            0.2f, 1f
    };

    public static int OVERLAY_RADIUS = 15;

    public static double OVERLAY_OPACITY = 0.4;

    /** Places API **/
    public static String PLACES_API_SERVER_URL = "https://places.demo.api.here.com/places/v1/discover/";

    public static String PLACES_API_ID = "DemoAppId01082013GAL";

    public static String PLACES_API_CODE = "AJKnXv84fjrb0KIHawS0Tg";

    public static int SIZE_PLACES_API_REQUEST = 40;

    /** Preferences **/
    public static String PREFERENCES_NAME = "settings";

    public static int PREFERENCES_MODE = Context.MODE_PRIVATE;

    public static String PREFERENCES_DEVICE_ID = "DeviceID";

    public static String PREFERENCES_LANGUAGE = "Language";

    public static String PREFERENCES_LANGUAGE_DEF_VALUE = "es";

    /** Location Request **/
    public static long UPPER_UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    public static long UPPER_FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPPER_UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    public static long MID_UPDATE_INTERVAL_IN_MILLISECONDS = 1000 * 60;

    public static long MID_FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = MID_UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    public static long LOWER_UPDATE_INTERVAL_IN_MILLISECONDS = 1000 * 60 * 2;

    public static long LOWER_FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = LOWER_UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    /** Battery **/
    public static double BATTERY_UPPER_BOUND = 70.0;

    public static double BATTERY_LOWER_BOUND = 30.0;

    /** Allowed Places Types Location **/
    public static String LOCATION_TYPE = "locality";
}
