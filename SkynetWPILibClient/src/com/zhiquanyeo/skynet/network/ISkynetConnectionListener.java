package com.zhiquanyeo.skynet.network;

public interface ISkynetConnectionListener {
	void onConnected();
	void onDisconnected();
	void onConnectionLost(String cause);
	void onMessageReceived(String topic, byte[] payload);
	
	// TODO Potentially separate this out into more specific message types?
}
