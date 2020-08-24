package com.tfg.subscribers;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProviderManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class SubscriberPatrol implements MqttCallback {

	// QoS 
	private final int qos = 1;
	
	// Topic
	private String topic = "Patrol";
	
	// MQTT client
	private MqttClient client;
	
	// Runtime engine
	private EPRuntime runtimeEngine;
	
	// Data associated to the places coordinates
	private String file = "./data/places.json";
	private JSONObject placesObject;
	private JSONArray badajozCoordinates;

	public SubscriberPatrol(String uri) throws MqttException, URISyntaxException {
		this(new URI(uri));

	}

	public SubscriberPatrol(URI uri) throws MqttException {
		// Set MQTT host
		String host = String.format("tcp://%s:%d", uri.getHost(), uri.getPort());
		
		// Set MQTT client values
		String username = "root";
		String password = "root";
		String clientId = "MQTT-TFG-Patrol";
		
		// Get Runtime Engine
		runtimeEngine = EPServiceProviderManager.getDefaultProvider().getEPRuntime();
		
		// Retrieve topic
		if (!uri.getPath().isEmpty()) {
			this.topic = uri.getPath().substring(1);
		}

		// Define MQTT connection options
		MqttConnectOptions conOpt = new MqttConnectOptions();
		conOpt.setCleanSession(true);
		conOpt.setUserName(username);
		conOpt.setPassword(password.toCharArray());

		// Initilize MQTT connection
		this.client = new MqttClient(host, clientId, new MemoryPersistence());
		
		// Set MQTT callback
		this.client.setCallback(this);
		
		// Connect to MQTT
		this.client.connect(conOpt);

		// Subscribe to topic
		this.client.subscribe(this.topic, qos);
		
		// Read from places.json the data
		try {
			this.placesObject = new JSONObject(new String(Files.readAllBytes(Paths.get(file))));
			
			// Get places coordinates
			JSONObject places = (JSONObject) this.placesObject.get("places");
			
			// Get Badajoz coordinates
			badajozCoordinates = places.getJSONArray("Badajoz");
			
		} catch (JSONException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Send message to MQTT client
	 * @param payload String with the payload
	 * @throws MqttException
	 */
	public void sendMessage(String payload) throws MqttException {
		MqttMessage message = new MqttMessage(payload.getBytes());
		message.setQos(qos);
		this.client.publish(this.topic, message); // Blocking publish
	}

	/**
	 * @see MqttCallback#connectionLost(Throwable)
	 */
	public void connectionLost(Throwable cause) {
		System.out.println("Connection lost because: " + cause);
		System.exit(1);
	}

	/**
	 * @see MqttCallback#deliveryComplete(IMqttDeliveryToken)
	 */
	public void deliveryComplete(IMqttDeliveryToken token) {
	}

	/**
	 * @see MqttCallback#messageArrived(String, MqttMessage)
	 */
	public void messageArrived(String topic, MqttMessage message) throws MqttException {
		// Create event
		Object event[] = new Object[1];
		
		// Get payload
		String val = new String(message.getPayload());
		
		// Get patrol coordinates
		JSONObject patrolCoordinates = new JSONObject(val); 
		
		if (patrolCoordinates.getDouble("lat") >= badajozCoordinates.getDouble(0) && patrolCoordinates.getDouble("lat") <= badajozCoordinates.getDouble(1) &&
			patrolCoordinates.getDouble("lng") >= badajozCoordinates.getDouble(2) && patrolCoordinates.getDouble("lng") <= badajozCoordinates.getDouble(3)) {
			// Inside Badajoz
			
			System.out.println("Patrol Inside Badajoz");
			
			// Get current time millis
			long ts = System.currentTimeMillis(); 
			
			// Set the event
	     	event[0] = ts;
	     	
	     	// Send event to the runtime engine
			runtimeEngine.sendEvent(event,"patrolMovingEvent");
			
		}

	}

}