package edu.wpi.first.wpilibj;

public abstract class RobotBase {
	public static final int ROBOT_TASK_PRIORITY = 101;
	
	protected RobotBase() {
		System.out.println("Starting RobotBase");
	}
	
	public void free() {
		
	}
	
	public static boolean isReal() {
		return false;
	}
	
	public boolean isDisabled() {
		return false; //TODO Implement
	}
	
	public boolean isEnabled() {
		return true; //TODO Implement
	}
	
	public boolean isAutonomous() {
		return false;
	}
	
	public boolean isTest() {
		return false;
	}
	
	public boolean isOperatorControl() {
		return false;
	}
	
	public boolean isNewDataAvailable() {
		return false;
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
