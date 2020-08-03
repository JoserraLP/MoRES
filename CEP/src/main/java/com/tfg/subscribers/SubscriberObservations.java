package com.tfg.subscribers;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.net.URI;
import java.net.URISyntaxException;

public class SubscriberObservations implements MqttCallback {

	// QoS 
	private final int qos = 1;
	
	// Topic
	private String topic = "Observation";
	
	// Topic
	private MqttClient client;

	public SubscriberObservations(String uri) throws MqttException, URISyntaxException {
		this(new URI(uri));

	}

	public SubscriberObservations(URI uri) throws MqttException {
		// Set MQTT host
		String host = String.format("tcp://%s:%d", uri.getHost(), uri.getPort());
		
		// Set MQTT client values
		String username = "root";
		String password = "root";
		String clientId = "MQTT-TFG-Observations";
		
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
		// Get observation received
		String messageReceived = new String(message.getPayload());
	
		// Send to middleware the observations
		switch (messageReceived) {
		case "o1":
			this.sendMessage("People Moving HIGH");
			break;
		case "o2":
			this.sendMessage("People Moving HIGH NIGHT");
			break;
		case "o3":
			this.sendMessage("Patrol Moving HIGH");
			break;
		case "o4":
			this.sendMessage("Patrol Moving LOW");
			break;
		default:
			break;
		}

	}

}