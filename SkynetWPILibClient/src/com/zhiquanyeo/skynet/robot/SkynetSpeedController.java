package com.zhiquanyeo.skynet.robot;

import com.zhiquanyeo.skynet.network.SkynetProxy;

public class SkynetSpeedController {
	private double d_speed;
	private int d_channel;
	
	public SkynetSpeedController(int channel) {
		d_channel = channel;
	}
	
	public void set(double speed, byte syncGroup) {
		set(speed);
	}
	
	public void set(double speed) {
		d_speed = speed;
		SkynetProxy.publishPwmValue(d_channel, speed);
	}
	
	public double get() {
		return d_speed;
	}
	
	public void disable() {
		set(0);
	}
	
	public void pidWrite(double output) {
		set(output);
	}
}
