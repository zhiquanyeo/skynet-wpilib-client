package com.zhiquanyeo.skynet.system;

public abstract class SimCANNode {
	
	protected int d_id;
	
	public SimCANNode(int id) {
		d_id = id;
	}
	
	public void onBusMessage(int source, int dest, String topic, String data) {
		if ((dest == -1 || dest == d_id) && source != d_id) {
			onMessageReceived(source, topic, data);
		}
	}
	
	protected final void sendBusMessage(int dest, String topic, String data) {
		SimCAN.broadcast(d_id, dest, topic, data);
	}
	
	protected abstract void onMessageReceived(int source, String topic, String data);
}
