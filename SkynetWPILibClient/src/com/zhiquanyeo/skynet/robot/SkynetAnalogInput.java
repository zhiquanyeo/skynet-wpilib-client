package com.zhiquanyeo.skynet.robot;

import com.zhiquanyeo.skynet.network.ChannelInUseException;
import com.zhiquanyeo.skynet.network.ISkynetMessageSubscriber;
import com.zhiquanyeo.skynet.network.SkynetProxy;

public class SkynetAnalogInput {
private static final int kThrottleTime = 7;
	
	private double d_value = 0.0;;
	private long d_lastInputTime = 0;
	private int d_channel = -1;
	
	public SkynetAnalogInput(int channel) throws ChannelInUseException {
		d_channel = channel;
		boolean success = SkynetProxy.subscribeAnalogInput(channel, new ISkynetMessageSubscriber() {
			
			@Override
			public void onMessageReceived(String message) {
				long currTime = System.currentTimeMillis();
				if (currTime - d_lastInputTime > kThrottleTime) {
					d_lastInputTime = currTime;
					double val = Double.parseDouble(message);
					if (!Double.isNaN(val)) {
						d_value = val;
					}
				}
			}
		});
		
		if (!success) {
			throw new ChannelInUseException("Digital Channel " + channel + " already in use");
		}
	}
	
	public double get() {
		return d_value;
	}
}
