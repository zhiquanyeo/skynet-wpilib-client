package edu.wpi.first.wpilibj.hal;

import java.nio.IntBuffer;

import com.zhiquanyeo.skynet.system.SharedSystem;

public class PowerJNI {
	public static float getVinVoltage(IntBuffer status) {
		status.rewind();
		status.put(0);
		return (float)SharedSystem.getValue("PowerVinVoltage", 12.0f);
	}
	
	public static float getVinCurrent(IntBuffer status) {
		status.rewind();
		status.put(0);
		return (float)SharedSystem.getValue("PowerVinCurrent", 0.0f);
	}
	
	public static float getUserVoltage6V(IntBuffer status){
		status.rewind();
		status.put(0);
		return 6.0f;
	}
	
	public static float getUserCurrent6V(IntBuffer status) {
		status.rewind();
		status.put(0);
		return 0.0f;
	}
	
	public static boolean getUserActive6V(IntBuffer status) {
		status.rewind();
		status.put(0);
		return true;
	}
	
	public static int getUserCurrentFaults6V(IntBuffer status) {
		status.rewind();
		status.put(0);
		return 0;
	}
	
	public static float getUserVoltage5V(IntBuffer status) {
		status.rewind();
		status.put(0);
		return 5.0f;
	}
	
	public static float getUserCurrent5V(IntBuffer status) {
		status.rewind();
		status.put(0);
		return 0.0f;
	}
	
	public static boolean getUserActive5V(IntBuffer status) {
		status.rewind();
		status.put(0);
		return true;
	}
	
	public static int getUserCurrentFaults5V(IntBuffer status) {
		status.rewind();
		status.put(0);
		return 0;
	}
	
	public static float getUserVoltage3V3(IntBuffer status) {
		status.rewind();
		status.put(0);
		return 3.3f;
	}
	
	public static float getUserCurrent3V3(IntBuffer status) {
		status.rewind();
		status.put(0);
		return 0.0f;
	}
	
	public static boolean getUserActive3V3(IntBuffer status) {
		status.rewind();
		status.put(0);
		return true;
	}
	
	public static int getUserCurrentFaults3V3(IntBuffer status) {
		status.rewind();
		status.put(0);
		return 0;
	}
}
