package com.zhiquanyeo.skynet.system;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

public class SimCAN {
	private static Dictionary<Integer, SimCANNode> s_CANIds = new Hashtable<>();
	
	public static void registerCANListener(int id, SimCANNode listener) throws CANIdInUseException {
		if (s_CANIds.get(id) != null) {
			throw new CANIdInUseException("CAN ID " + id + " is already in use");
		}
		s_CANIds.put(id, listener);
	}
	
	public static void unregisterCANListener(int id) {
		s_CANIds.remove(id);
	}
	
	/**
	 * Simulate a broadcast on the CAN bus using a topic and data
	 * @param sourceId Source node ID
	 * @param destId Destination node ID, -1 for broadcast
	 * @param topic Topic of the broadcast
	 * @param data Data of the broadcast
	 */
	public static void broadcast(int sourceId, int destId, String topic, String data) {
		Enumeration<SimCANNode> e = s_CANIds.elements();
		while (e.hasMoreElements()) {
			SimCANNode listener = e.nextElement();
			listener.onBusMessage(sourceId, destId, topic, data);
		}
	}
}
