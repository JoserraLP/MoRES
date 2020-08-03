package com.tfg.utils;

import java.io.File;
import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttException;

import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPSubscriberException;
import com.espertech.esper.client.deploy.DeploymentException;
import com.espertech.esper.client.deploy.DeploymentOptions;
import com.espertech.esper.client.deploy.EPDeploymentAdmin;
import com.espertech.esper.client.deploy.Module;
import com.espertech.esper.client.deploy.ParseException;
import com.tfg.listeners.EsperStatementSubscriber;

public class PatternDetector {
	private EPServiceProvider engine;
	private Module module;
	private String modulePath;
	EPDeploymentAdmin deployAdmin;
	private boolean started = false;

	public PatternDetector(String modulePath) {
		this.modulePath = modulePath;
		engine = EPServiceProviderManager.getDefaultProvider();
	}

	public void start() throws IOException, ParseException, DeploymentException, InterruptedException,
			EPSubscriberException, MqttException {
		if (!started) {
			deployAdmin = engine.getEPAdministrator().getDeploymentAdmin();
			module = deployAdmin.read(new File(this.modulePath));
			deployAdmin.deploy(module, new DeploymentOptions());
			setSubsForCEPObservations();
			started = true;
		}

	}

	public void stop() {
		if (started) {
			engine.initialize();
			started = false;
		}

	}

	private void setSubsForCEPObservations() throws EPSubscriberException, MqttException {
		EPAdministrator epAdmin = engine.getEPAdministrator();
		String[] statements = epAdmin.getStatementNames();

		if (statements != null) {
			for (int i = 0; i < statements.length; ++i) {
				if (epAdmin.getStatement(statements[i]).getName().equals("Observations")) {
					epAdmin.getStatement(statements[i]).setSubscriber(new EsperStatementSubscriber());
				}
			}
		}

	}

}
