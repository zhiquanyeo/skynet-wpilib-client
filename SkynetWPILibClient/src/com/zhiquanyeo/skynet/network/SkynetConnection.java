package com.zhiquanyeo.skynet.network;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	// Regex Utils
	private final static Pattern SKYNET_ROBOT_MESSAGE_TYPE_REGEX = 
			Pattern.compile("skynet/robot/([a-zA-Z0-9]+)");
	private final static Matcher ROBOT_MESSAGE_TYPE_MATCHER = SKYNET_ROBOT_MESSAGE_TYPE_REGEX.matcher("");
	
	private final static Pattern SKYNET_SENSOR_REGEX = 
			Pattern.compile("skynet/robot/sensors/([a-z]+)/([0-9]+)$");
	private final static Matcher ROBOT_SENSOR_MATCHER = SKYNET_SENSOR_REGEX.matcher("");
	
	private final static Pattern SKYNET_STATUS_MESSAGE_REGEX = 
			Pattern.compile("skynet/robot/status/([a-zA-Z0-9]+)$");
	private final static Matcher ROBOT_STATUS_MESSAGE_MATCHER = SKYNET_STATUS_MESSAGE_REGEX.matcher("");
	
	private final static HashSet<String> LEGAL_MESSAGE_TYPES = new HashSet<String>();
	
	public SkynetConnection() {
		//Populate the legal message types
		LEGAL_MESSAGE_TYPES.add("sensors");
		LEGAL_MESSAGE_TYPES.add("status");
		LEGAL_MESSAGE_TYPES.add("message");
	}
	
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
					ROBOT_MESSAGE_TYPE_MATCHER.reset(topic);
					if (ROBOT_MESSAGE_TYPE_MATCHER.lookingAt() && 
						LEGAL_MESSAGE_TYPES.contains(ROBOT_MESSAGE_TYPE_MATCHER.group(1))) {
						
						String robotMessageType = ROBOT_MESSAGE_TYPE_MATCHER.group(1);
						
						switch(robotMessageType) {
							case "sensors": {
								broadcastSensorMessage(topic, message.getPayload());
							} break;
							
							case "status": {
								broadcastRobotStatusMessage(topic, message.getPayload());
							} break;
							
							case "message": {
								broadcastRobotGeneralMessage(message.getPayload());
							} break;
						}
					}
					else {
						LOGGER.warning("Dropping illegal message: " + topic);
					}
				}
				
			});
			
			d_mqttClient.connect();
			d_isConnected = true;
			
			// Subscribe to topics of interest
			// We are only interested in the activeClient message, and messages from the robot
			d_mqttClient.subscribe("skynet/robot/#");
			d_mqttClient.subscribe("skynet/clients/activeClient");
			
			//Register this client
			d_mqttClient.publish("skynet/clients/register", d_mqttIdentifier.getBytes(), 0, false);
			
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
	
	public synchronized boolean publish(String topic, byte[] payload) throws Exception {
		if (!d_isConnected) {
			LOGGER.warning("Cannot publish. Not connected");
			throw new Exception("Cannot publish. Not connected");
		}
		
		d_mqttClient.publish(topic, payload, 0, false);
		return true;
	}
	
	public synchronized void addSubscriber(ISkynetConnectionListener subscriber) {
		d_subscribers.add(subscriber);
	}
	
	public synchronized void removeSubscriber(ISkynetConnectionListener subscriber) {
		while (d_subscribers.contains(subscriber)) {
			d_subscribers.remove(subscriber);
		}
	}
	
	// === Interacting with the MQTT Endpoint
	public boolean sendDigitalOutput(int channel, boolean value) {
		if (!d_isConnected) {
			LOGGER.warning("Could not send digital output. Not connected");
			return false;
		}
		String strVal = value ? "1":"0";
		try {
			d_mqttClient.publish("skynet/control/digital/" + channel, strVal.getBytes(), 0, false);
		}
		catch (Exception e) {
			LOGGER.warning("Could not send digital output: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public boolean sendAnalogOutput(int channel, double value) { // 0 <= analog <= 1023
		if (!d_isConnected) {
			LOGGER.warning("Could not send analog output. Not connected");
			return false;
		}
		String strVal = Double.toString(value);
		try {
			d_mqttClient.publish("skynet/control/analog/" + channel, strVal.getBytes(), 0, false);
		}
		catch (Exception e) {
			LOGGER.warning("Could not send analog output: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public boolean sendPwmOutput(int channel, double value) { //-1 <= pwm <= 1
		if (!d_isConnected) {
			LOGGER.warning("Could not send pwm output. Not connected");
			return false;
		}
		
		// Clamp the value
		if (value < -1.0) {
			value = -1.0;
		}
		if (value > 1.0) {
			value = 1.0;
		}
		
		String strVal = Double.toString(value);
		try {
			d_mqttClient.publish("skynet/control/pwm/" + channel, strVal.getBytes(), 0, false);
		}
		catch (Exception e) {
			LOGGER.warning("Could not send pwm output: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	private boolean broadcastSensorMessage(String topic, byte[] payload) {
		ROBOT_SENSOR_MATCHER.reset(topic);
		String sensorType;
		if (ROBOT_SENSOR_MATCHER.matches()) {
			int channel = -1;
			try {
				channel = Integer.parseInt(ROBOT_SENSOR_MATCHER.group(2));
			}
			catch (NumberFormatException e) {
				LOGGER.warning("Invalid channel number: " + e.getMessage());
				return false;
			}
			
			sensorType = ROBOT_SENSOR_MATCHER.group(1);
			
			if (sensorType.equals("digital")) {
				boolean value = (new String(payload)).equals("1");
				
				for (int i = 0; i < d_subscribers.size(); i++) {
					d_subscribers.get(i).onRobotDigitalInputChanged(channel, value);
				}
			}
			else if (sensorType.equals("analog")) {
				double value = Double.NaN;
				try {
					value = Double.parseDouble(new String(payload));
				}
				catch (NumberFormatException e) {
					LOGGER.warning("Invalid value for analog signal: " + e.getMessage());
					return false;
				}
				for (int i = 0; i < d_subscribers.size(); i++) {
					d_subscribers.get(i).onRobotAnalogInputChanged(channel, value);
				}
			}
			else {
				LOGGER.warning("Invalid sensor type: " + sensorType);
			}
			
			return true;
		}
		return false;
	}
	
	private boolean broadcastRobotStatusMessage(String topic, byte[] payload) {
		ROBOT_STATUS_MESSAGE_MATCHER.reset(topic);
		String statusType;
		if (ROBOT_STATUS_MESSAGE_MATCHER.matches()) {
			statusType = ROBOT_STATUS_MESSAGE_MATCHER.group(1);
			String message = new String(payload);
			
			for (int i = 0; i < d_subscribers.size(); i++) {
				d_subscribers.get(i).onRobotStatusMessage(statusType, message);
			}
			return true;
		}
		return false;
	}
	
	private boolean broadcastRobotGeneralMessage(byte[] payload) {
		String message = new String(payload);
		for (int i = 0; i < d_subscribers.size(); i++) {
			d_subscribers.get(i).onRobotGeneralMessage(message);
		}
		return false;
	}
}
