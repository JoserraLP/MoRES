package com.tfg.main;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.espertech.esper.client.EPSubscriberException;
import com.espertech.esper.client.deploy.DeploymentException;
import com.espertech.esper.client.deploy.ParseException;
import com.tfg.subscribers.SubscriberDeviceLocation;
import com.tfg.subscribers.SubscriberPatrol;
import com.tfg.utils.Constants;
import com.tfg.utils.PatternDetector;

public class CEPMain {

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException, ParseException, DeploymentException,
			InterruptedException, ClassNotFoundException, EPSubscriberException, MqttException {
		CEPMain test = new CEPMain();

		// Initiliaze Pattern Detector
		PatternDetector pd = new PatternDetector(System.getProperty("user.dir") + "/resources/Statements.epl");
		// Start Pattern Detector
		pd.start();
		try {
			// Define subscribers
			SubscriberDeviceLocation subs_device_location = new SubscriberDeviceLocation(new URI(Constants.MQTT_URL));
			SubscriberPatrol subs_patrol = new SubscriberPatrol(new URI(Constants.MQTT_URL));
			
		} catch (MqttException | URISyntaxException e) {
			e.printStackTrace();
		}
		synchronized (test) {
			test.wait();
		}
	}
}