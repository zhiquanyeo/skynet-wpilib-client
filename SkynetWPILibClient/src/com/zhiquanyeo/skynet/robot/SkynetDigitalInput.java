package com.zhiquanyeo.skynet.robot;

import com.zhiquanyeo.skynet.network.ChannelInUseException;
import com.zhiquanyeo.skynet.network.ISkynetMessageSubscriber;
import com.zhiquanyeo.skynet.network.SkynetProxy;

public class SkynetDigitalInput {
	private static final int kThrottleTime = 7;
	
	private boolean d_value;
	private long d_lastInputTime = 0;
	private int d_channel = -1;
	
	public SkynetDigitalInput(int channel) throws ChannelInUseException {
		d_channel = channel;
		boolean success = SkynetProxy.subscribeDigitalInput(channel, new ISkynetMessageSubscriber() {
			
			@Override
			public void onMessageReceived(String message) {
				long currTime = System.currentTimeMillis();
				if (currTime - d_lastInputTime > kThrottleTime) {
					d_lastInputTime = currTime;
					if (message.equals("1") || message.equals("true") && d_value != true) {
						d_value = true;
					}
					else if (d_value != false) {
						d_value = false;
					}
				}
			}
		});
		
		if (!success) {
			throw new ChannelInUseException("Digital Channel " + channel + " already in use");
		}
	}
	
	public boolean get() {
		return d_value;
	}
}
