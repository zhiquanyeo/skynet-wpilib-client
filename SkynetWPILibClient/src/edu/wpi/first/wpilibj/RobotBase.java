package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public abstract class RobotBase {
	public static final int ROBOT_TASK_PRIORITY = 101;
	
	protected final DriverStation d_ds;
	
	protected RobotBase() {
		System.out.println("Starting RobotBase");
		NetworkTable.setServerMode();
		d_ds = DriverStation.getInstance();
		NetworkTable.getTable("");
		NetworkTable.getTable("LiveWindow").getSubTable("~STATUS~").putBoolean("LW Enabled", false);
	}
	
	public void free() {
		
	}
	
	public static boolean isSimulation() {
		return true;
	}
	
	public static boolean isReal() {
		return false;
	}
	
	public boolean isDisabled() {
		return d_ds.isDisabled();
	}
	
	public boolean isEnabled() {
		return d_ds.isEnabled();
	}
	
	public boolean isAutonomous() {
		return d_ds.isAutonomous();
	}
	
	public boolean isTest() {
		return d_ds.isTest();
	}
	
	public boolean isOperatorControl() {
		return d_ds.isOperatorControl();
	}
	
	public boolean isNewDataAvailable() {
		return d_ds.isNewControlData();
	}
	
	public abstract void startCompetition();
	
	public static boolean getBooleanProperty(String name, boolean defaultValue) {
		String propVal = System.getProperty(name);
		if (propVal == null) {
			return defaultValue;
		}
		if (propVal.equalsIgnoreCase("false")) {
			return false;
		}
		else if (propVal.equalsIgnoreCase("true")) {
			return true;
		}
		else {
			throw new IllegalStateException(propVal);
		}
		
	}
	
}
