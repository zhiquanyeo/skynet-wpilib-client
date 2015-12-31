package com.zhiquanyeo.skynet.network;

import java.util.HashMap;
import java.util.Map;

/** 
 * Class that allows communication with a skynet endpoint
 * @author zyeo8
 *
 */
public class SkynetProxy implements ISkynetConnectionListener {
	
	// === Static Accessors
	public static void setSkynetConnection(SkynetConnection conn) {
		s_instance.setConnection(conn);
	}
	
	public static boolean subscribeDigitalInput(int channel, ISkynetMessageSubscriber subscriber) {
		String topic = "skynet/robot/sensors/digital/" + channel;
		return s_instance.subscribe(topic, subscriber);
	}
	
	public static boolean subscribeAnalogInput(int channel, ISkynetMessageSubscriber subscriber) {
		String topic = "skynet/robot/sensors/analog/" + channel;
		return s_instance.subscribe(topic, subscriber);
	}
	
	private static SkynetProxy s_instance = new SkynetProxy();
		
	
	// Class Definition
	private final Map<String, ISkynetMessageSubscriber> d_subscriptions = new HashMap<>();
	private SkynetConnection d_connection;
	
	protected SkynetProxy() {}
	
	protected synchronized void setConnection(SkynetConnection conn) {
		if (conn == null) {
			throw new NullPointerException("Cannot assign null SkynetConnection");
		}
		
		if (d_connection != null) {
			d_connection.removeSubscriber(this);
		}
		
		d_connection = conn;
		d_connection.addSubscriber(this);
	}
	
	protected boolean hasConnection() {
		return d_connection != null;
	}
	
	protected boolean subscribe(String topic, ISkynetMessageSubscriber subscriber) {
		if (d_subscriptions.containsKey(topic)) {
			System.err.println("Already have a subscription for " + topic);
			return false;
		}
		
		d_subscriptions.put(topic, subscriber);
		return true;
	}
	
	protected boolean publish(String topic, byte[] payload) {
		if (d_connection == null) {
			System.err.println("No SkynetConnection set! Can't publish");
			return false;
		}
		
		boolean success = false;
		try {
			success = d_connection.publish(topic, payload);
		}
		catch (Exception e) {
			success = false;
		}
		return success;
	}

	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionLost(String cause) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRobotDigitalInputChanged(int channel, boolean value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRobotAnalogInputChanged(int channel, double value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRobotStatusMessage(String statusType, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRobotGeneralMessage(String message) {
		// TODO Auto-generated method stub
		
	}
	
}
