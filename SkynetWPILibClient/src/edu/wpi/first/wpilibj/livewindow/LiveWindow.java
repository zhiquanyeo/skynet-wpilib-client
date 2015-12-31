package edu.wpi.first.wpilibj.livewindow;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
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
	private static Vector<LiveWindowSendable> sensors = new Vector<>();
	private static Hashtable<LiveWindowSendable, LiveWindowComponent> components = new Hashtable<>();
	private static ITable livewindowTable;
	private static ITable statusTable;
	private static boolean liveWindowEnabled = false;
	private static boolean firstTime = true;
	
	private static void initializeLiveWindowComponents() {
		System.out.println("Initializing live window components for the first time");
		livewindowTable = NetworkTable.getTable("LiveWindow");
		statusTable = livewindowTable.getSubTable("~STATUS~");
		for (Enumeration<LiveWindowSendable> e = components.keys(); e.hasMoreElements();) {
			LiveWindowSendable component = e.nextElement();
			LiveWindowComponent c = components.get(component);
			String subsystem = c.getSubsystem();
			String name = c.getName();
			System.out.println("Initializing table for '" + subsystem + "' '" + name + "'");
			ITable table = livewindowTable.getSubTable(subsystem).getSubTable(name);
			table.putString("~TYPE~", component.getSmartDashboardType());
			table.putString("Name", name);
			table.putString("Subsystem", subsystem);
			component.initTable(table);
			if (c.isSensor()) {
				sensors.addElement(component);
			}
		}
	}
	
	public static void setEnabled(boolean enabled) {
		if (liveWindowEnabled != enabled) {
			if (enabled) {
				System.out.println("Starting live window mode.");
				if (firstTime) {
					initializeLiveWindowComponents();
					firstTime = false;
				}
				Scheduler.getInstance().disable();
				Scheduler.getInstance().removeAll();
				for (Enumeration<LiveWindowSendable> e = components.keys(); e.hasMoreElements();) {
					LiveWindowSendable component = e.nextElement();
					component.startLiveWindowMode();
				}
			}
			else {
				System.out.println("Stopping live window mode.");
				for (Enumeration<LiveWindowSendable> e = components.keys(); e.hasMoreElements();) {
					LiveWindowSendable component = e.nextElement();
					component.stopLiveWindowMode();
				}
				Scheduler.getInstance().enable();
			}
			liveWindowEnabled = enabled;
			statusTable.putBoolean("LW Enabled", enabled);
		}
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
		for (int i = 0; i < sensors.size(); i++) {
			LiveWindowSendable lws = sensors.elementAt(i);
			lws.updateTable();
		}
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
