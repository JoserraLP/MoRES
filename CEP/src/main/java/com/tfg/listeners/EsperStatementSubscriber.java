package com.tfg.listeners;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import com.tfg.utils.Constants;

public class EsperStatementSubscriber {

	private String topic = "observations";
	private MqttClient client;
	private int qos = 1;

	public EsperStatementSubscriber() throws MqttException {
		// Set MQTT host
		String host = String.format(Constants.MQTT_URL);
		// Set MQTT client values
		String username = "root";
		String password = "root";
		String clientId = "MQTT-TFG-Esper";

		// Define MQTT connection options
		MqttConnectOptions conOpt = new MqttConnectOptions();
		conOpt.setCleanSession(true);
		conOpt.setUserName(username);
		conOpt.setPassword(password.toCharArray());

		// Initilize MQTT connection
		this.client = new MqttClient(host, clientId, new MemoryPersistence());
		
		// Connect to MQTT
		this.client.connect(conOpt);
	}

	/**
	 * Publish the observation in the middeware
	 * @param obsName observation name
	 * @throws MqttPersistenceException
	 * @throws MqttException
	 */
	public void update(String obsName) throws MqttPersistenceException, MqttException {
		System.out.println(obsName);
		MqttMessage message = new MqttMessage(obsName.getBytes());
		message.setQos(qos);
		this.client.publish(this.topic, message); // Blocking publish
	}
}
