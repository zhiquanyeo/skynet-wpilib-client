package edu.wpi.first.wpilibj.livewindow;

import java.util.Hashtable;
import java.util.Vector;

import edu.wpi.first.wpilibj.tables.ITable;

class LiveWindowComponent {
	String d_subsystem;
	String d_name;
	boolean d_isSensor;
	
	public LiveWindowComponent(String subsystem, String name, boolean isSensor) {
		d_subsystem = subsystem;
		d_name = name;
		d_isSensor = isSensor;
	}
	
	public String getName() {
		return d_name;
	}
	
	public String getSubsystem() {
		return d_subsystem;
	}
	
	public boolean isSensor() {
		return d_isSensor;
	}
}

public class LiveWindow {
	private static Vector sensors = new Vector();
	private static Hashtable components = new Hashtable();
	private static ITable livewindowTable;
	private static ITable statusTable;
	private static boolean liveWindowEnabled = false;
	private static boolean firstTime = true;
	
	private static void initializeLiveWindowComponents() {
		System.out.println("Initializing live window components for the first time");
		// TODO Implement
	}
	
	public static void setEnabled(boolean enabled) {
		// TODO Implement
	}
	
	public static void run() {
		updateValues();
	}
	
	public static void addSensor(String subsystem, String name, LiveWindowSendable component) {
		components.put(component, new LiveWindowComponent(subsystem, name, true));
	}
	
	public static void addActuator(String subsystem, String name, LiveWindowSendable component) {
		components.put(component, new LiveWindowComponent(subsystem, name, false));
	}
	
	private static void updateValues() {
		// TODO Implement
	}
	
	public static void addSensor(String moduleType, int channel, LiveWindowSendable component) {
		addSensor("Ungrouped", moduleType + "[" + channel + "]", component);
		if (sensors.contains(component)) {
			sensors.removeElement(component);
		}
		sensors.addElement(component);
	}
	
	public static void addActuator(String moduleType, int channel, LiveWindowSendable component) {
		addActuator("Ungrouped", moduleType + "[" + channel + "]", component);
	}
	
	public static void addActuator(String moduleType, int moduleNumber, int channel, LiveWindowSendable component) {
		addActuator("Ungrouped", moduleType + "[" + moduleNumber + "," + channel + "]", component);
	}
}
