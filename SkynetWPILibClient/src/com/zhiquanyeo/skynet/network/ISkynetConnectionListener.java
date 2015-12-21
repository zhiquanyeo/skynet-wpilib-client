package com.zhiquanyeo.skynet.network;

public interface ISkynetConnectionListener {
	void onConnected();
	void onDisconnected();
	void onConnectionLost(String cause);
	
	void onRobotDigitalInputChanged(int channel, boolean value);
	void onRobotAnalogInputChanged(int channel, double value);
	void onRobotStatusMessage(String statusType, String message);
	void onRobotGeneralMessage(String message);
}
