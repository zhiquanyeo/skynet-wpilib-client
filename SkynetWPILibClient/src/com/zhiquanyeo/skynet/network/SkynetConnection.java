package com.zhiquanyeo.skynet.network;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class SkynetConnection {
	private final static Logger LOGGER = Logger.getLogger(SkynetConnection.class.getName());
	
	private MqttClient d_mqttClient;
	
	private String d_mqttUrl;
	private String d_mqttIdentifier;
	
	private boolean d_isConnected = false;
	
	private ArrayList<ISkynetConnectionListener> d_subscribers = new ArrayList<ISkynetConnectionListener>();
	
	
	public synchronized boolean isConnected() {
		return this.d_isConnected;
	}
	
	public synchronized void connect(String url, String identifier) throws Exception {
		if (d_isConnected) {
			LOGGER.warning("Already connected");
			throw new Exception ("Already Connected");
		}
		
		d_mqttUrl = url;
		d_mqttIdentifier = identifier;
		
		try {
			LOGGER.info("Creating new MQTT Client and connecting to " + url + " with identifier " + identifier);
			d_mqttClient = new MqttClient(url, identifier);
			
			d_mqttClient.setCallback(new MqttCallback() {

				@Override
				public void connectionLost(Throwable cause) {
					d_isConnected = false;
					
					for (int i = 0; i < d_subscribers.size(); i++) {
						d_subscribers.get(i).onConnectionLost(cause.getMessage());
					}
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {
					// TODO Keep track of outstanding requests
				}

				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					// TODO Inform all subscribers that a message was received
					for (int i = 0; i < d_subscribers.size(); i++) {
						d_subscribers.get(i).onMessageReceived(topic, message.getPayload());
					}
				}
				
			});
			
			d_mqttClient.connect();
			
			d_isConnected = true;
			
			// Subscribe to topics of interest
			
			// Inform subscribers of connection
			for (int i = 0; i < d_subscribers.size(); i++) {
				d_subscribers.get(i).onConnected();
			}
		}
		catch (MqttException e) {
			LOGGER.severe("Error connecting: " + e.getMessage());
			throw e;
		}
	}
	
	public synchronized void disconnect() {
		if (!d_isConnected) {
			LOGGER.warning("Not connected");
			return;
		}
		
		try {
			d_mqttClient.disconnect();
			d_isConnected = false;
			d_mqttClient = null;
			d_mqttUrl = null;
			d_mqttIdentifier = null;
			
			for (int i = 0; i < d_subscribers.size(); i++) {
				d_subscribers.get(i).onDisconnected();
			}
		}
		catch (MqttException e) {
			LOGGER.severe("Error disconnecting: " + e.getMessage());
		}
	}
	
	public synchronized void addSubscriber(ISkynetConnectionListener subscriber) {
		d_subscribers.add(subscriber);
	}
	
	public synchronized void removeSubscriber(ISkynetConnectionListener subscriber) {
		while (d_subscribers.contains(subscriber)) {
			d_subscribers.remove(subscriber);
		}
	}
}
