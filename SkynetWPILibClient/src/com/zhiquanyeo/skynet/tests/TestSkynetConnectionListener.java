package com.zhiquanyeo.skynet.tests;

import java.util.logging.Logger;

import com.zhiquanyeo.skynet.network.ISkynetConnectionListener;

public class TestSkynetConnectionListener implements ISkynetConnectionListener {
	private final static Logger LOGGER = Logger.getLogger(TestSkynetConnectionListener.class.getName());
	
	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
		LOGGER.info("Skynet Connection Established");
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		LOGGER.info("Skynet Connection was disconnected");
	}

	@Override
	public void onConnectionLost(String cause) {
		// TODO Auto-generated method stub
		LOGGER.info("Skynet Connection was lost unexpectedly");
	}

	@Override
	public void onRobotDigitalInputChanged(int channel, boolean value) {
		// TODO Auto-generated method stub
		LOGGER.info("Digital Channel " + channel + " is now reading " + value);
	}

	@Override
	public void onRobotAnalogInputChanged(int channel, double value) {
		// TODO Auto-generated method stub
		LOGGER.info("Analog Channel " + channel + " is now reading " + value);
	}

	@Override
	public void onRobotStatusMessage(String statusType, String message) {
		// TODO Auto-generated method stub
		LOGGER.info("Received a status message of type " + statusType + ": " + message);
	}

	@Override
	public void onRobotGeneralMessage(String message) {
		// TODO Auto-generated method stub
		LOGGER.info("Received a general message: " + message);
	}

}
