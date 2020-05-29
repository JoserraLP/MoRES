package com.example.mqtt.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mqtt.utils.Constants;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;

public class MQTTClient {

    private static final String TAG = MQTTClient.class.getSimpleName();

    private static final String PACKAGE_NAME = MQTTClient.class.getName();

    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";

    public static final String EXTRA_NEWS = PACKAGE_NAME + ".news";
    public static final String EXTRA_ALLOWED_PLACES_TYPES = PACKAGE_NAME + ".allowed_places_types";

    @SuppressLint("StaticFieldLeak")
    private static MQTTClient INSTANCE = null;

    private MqttAndroidClient client;
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private MQTTClient() {
    }

    public static MQTTClient getInstance(Context context) {
        if (INSTANCE == null) { //if there is no instance available... create new one
            INSTANCE = new MQTTClient();
            MQTTClient.context = context;
        }

        return INSTANCE;
    }

    public void startConnection() {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, Constants.MQTT_BROKER_URL,
                clientId);

        try {
            client.setCallback(new MqttCallbackExtended() {
                @Override
                public void connectComplete(boolean b, String s) {
                    Log.d(TAG, !b ? "Connection completed!" : "Connection failed!" );
                }

                @Override
                public void connectionLost(Throwable throwable) {
                    Log.d(TAG, "Connection lost");
                }

                @Override
                public void messageArrived(String topic, MqttMessage mqttMessage) {
                    Intent intent = new Intent(ACTION_BROADCAST);
                    if (topic.equals("News")) {
                        Log.d(TAG, "News: " + mqttMessage.toString());
                        // Notify anyone listening for broadcasts about the new location.
                        intent.putExtra(EXTRA_NEWS, mqttMessage.toString());

                    }
                    else if (topic.equals("AllowedPlacesTypes")) {
                        Log.d(TAG, mqttMessage.toString());
                        // Notify anyone listening for broadcasts about the allowed places types.
                        intent.putExtra(EXTRA_ALLOWED_PLACES_TYPES, Integer.valueOf(mqttMessage.toString()));

                    }
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "Connected");

                    //Subscribe to all relevant topics
                    subscribe("News", 0);
                    subscribe("AllowedPlacesTypes", 0);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "Failure");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            IMqttToken disconToken = client.disconnect();
            disconToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // we are now successfully disconnected
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // something went wrong, but probably we are disconnected anyway
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String payload) {
        byte[] encodedPayload;
        try {
            if (client != null) {
                Log.d(TAG, "Publishing");
                encodedPayload = payload.getBytes(StandardCharsets.UTF_8);
                MqttMessage message = new MqttMessage(encodedPayload);
                client.publish(topic, message);
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void subscribe(String topic, int qos) {
       try {
           if (client != null) {
               client.subscribe(topic, qos, null, new IMqttActionListener() {
                   @Override
                   public void onSuccess(IMqttToken asyncActionToken) {
                       Log.d(TAG, "Subscribed!");
                   }

                   @Override
                   public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d(TAG, "Subscribed fail!");
                   }
               });
           }
        } catch (MqttException e) {
            System.err.println("Exception whilst subscribing");
            e.printStackTrace();
        }
    }

}