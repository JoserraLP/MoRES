package com.unex.tfg.mqtt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.unex.tfg.utils.Constants;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MQTTClient {

    // TAG for Log messaging
    private static final String TAG = MQTTClient.class.getSimpleName();

    // Package to broadcast the data
    private static final String PACKAGE_NAME = MQTTClient.class.getName();

    // Variable representing the broadcast action
    public static final String ACTION_BROADCAST = PACKAGE_NAME + ".broadcast";

    // Values for the kinds of broadcast (news & allowed places types)
    public static final String EXTRA_NEWS = PACKAGE_NAME + ".news";
    public static final String EXTRA_ALLOWED_PLACES_TYPES = PACKAGE_NAME + ".allowed_places_types";

    // Singleton class instance
    @SuppressLint("StaticFieldLeak")
    private static MQTTClient INSTANCE = null;

    // Library MQTT Android Client
    private MqttAndroidClient client;

    // Context for the MQTT Client
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private MQTTClient() {
    }

    /**
     * Get the MQTT Client Singleton instance
     * @param context MQTT Client Context
     * @return MQTT Client Singleton instance
     */
    public static MQTTClient getInstance(Context context) {
        if (INSTANCE == null) { //if there is no instance available... create new one
            INSTANCE = new MQTTClient();
            MQTTClient.context = context;
        }

        return INSTANCE;
    }

    /**
     * Start MQTT connection, set the callback for arrived messages and connect
     * @param topics Topics to be subscribed
     */
    public void startConnection(ArrayList<String> topics) {
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
                    if (topic.equals(topics.get(0))) { // First topic is "News"
                        Log.d(TAG, "News: " + mqttMessage.toString());
                        // This topic is for incoming news item
                        intent.putExtra(EXTRA_NEWS, mqttMessage.toString());

                    }
                    else if (topic.equals(topics.get(1))) { // Second topic is "AllowedPlacesTypes"
                        Log.d(TAG, mqttMessage.toString());
                        // This topic is for allowed places types updates
                        intent.putExtra(EXTRA_ALLOWED_PLACES_TYPES, Integer.valueOf(mqttMessage.toString()));

                    }
                    // Broadcast the intent created before
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
                    subscribe(topics.get(0), Constants.MQTT_QOS); // "News"
                    subscribe(topics.get(1), Constants.MQTT_QOS); // "AllowedPlacesTypes"
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

    // TODO close connection when the app is closed
    /**
     * Disconnect the MQTT Client
     */
    @SuppressWarnings("unused")
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

    /**
     * Publish a message on a specific topic
     * @param topic Topic where the message is going to be published
     * @param payload Message to publish
     */
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

    /**
     * Subscribe to a topic with a QOS
     * @param topic Topic to subscribe to
     * @param qos QOS of the subscriber
     */
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