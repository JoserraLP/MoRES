package com.example.mqtt.client;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.example.mqtt.utils.MQTTConfiguration;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;

public class MQTTClient {

    private static final String TAG = MQTTClient.class.getSimpleName();

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
        client = new MqttAndroidClient(context, MQTTConfiguration.MQTT_BROKER_URL,
                clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "Connected");
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

    public void subscribe(String topic, int qos) {
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was received
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}