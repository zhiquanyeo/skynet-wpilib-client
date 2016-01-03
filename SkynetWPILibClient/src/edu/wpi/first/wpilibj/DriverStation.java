package edu.wpi.first.wpilibj;

import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import edu.wpi.first.wpilibj.communication.HALAllianceStationID;
import edu.wpi.first.wpilibj.communication.HALControlWord;
import javafx.scene.shape.FillRule;

public class DriverStation implements RobotState.Interface {
	/**
     * Number of Joystick Ports
     */
    public static final int kJoystickPorts = 6;
    /**
     * Number of Joystick Axes
     */
    public static final int kJoystickAxes = 6;
    /**
     * Convert from raw values to volts
     */
    public static final double kDSAnalogInScaling = 5.0 / 1023.0;
    
    /**
     * The robot alliance that the robot is a part of
     */
    public enum Alliance {
    	Red, Blue, Invalid
    }
    
    private static final double JOYSTICK_UNPLUGGED_MESSAGE_INTERVAL = 1.0;
    private double d_nextMessageTime = 0.0;
    
    private class HALJoystickButtons {
    	public int buttons;
    	public byte count;
    }
    
    private static class DriverStationTask implements Runnable {
    	private DriverStation d_ds;
    	
    	DriverStationTask(DriverStation ds) {
    		d_ds = ds;
    	}
    	
    	public void run() {
    		d_ds.task();
    	}
    }
    
    private static DriverStation instance = new DriverStation();
    
    private short[][] d_joystickAxes =
    		new short[kJoystickPorts][FRCNetworkCommunicationsLibrary.kMaxJoystickAxes];
    private short[][] d_joystickPOVs =
    		new short[kJoystickPorts][FRCNetworkCommunicationsLibrary.kMaxJoystickPOVs];
    private HALJoystickButtons[] d_joystickButtons = new HALJoystickButtons[kJoystickPorts];
    private int[] d_joystickIsXbox = new int[kJoystickPorts];
    private int[] d_joystickType = new int[kJoystickPorts];
    private String[] d_joystickName = new String[kJoystickPorts];
    private int[][] d_joystickAxisType = new int[kJoystickPorts][FRCNetworkCommunicationsLibrary.kMaxJoystickAxes];
    
    private final Object d_dataSem;
    private Thread d_thread;
    private boolean d_thread_keepalive = true;
    
    private boolean d_userInDisabled = false;
    private boolean d_userInAutonomous = false;
    private boolean d_userInTeleop = false;
    private boolean d_userInTest = false;
    
    private boolean d_newControlData;
    private Semaphore d_dataAvailableSem = FRCNetworkCommunicationsLibrary.getNetworkSemaphore();
    
    public static DriverStation getInstance() {
    	return DriverStation.instance;
    }
    
    protected DriverStation() {
    	d_dataSem = new Object();
    	
    	for (int i = 0; i < kJoystickPorts; i++) {
    		d_joystickButtons[i] = new HALJoystickButtons();
    	}
    	
    	d_thread = new Thread(new DriverStationTask(this), "FRCDriverStation");
    	d_thread.setPriority((d_thread.NORM_PRIORITY + d_thread.MAX_PRIORITY) / 2);
    	
    	d_thread.start();
    }
    
    public void release() {
    	d_thread_keepalive = false;
    }
    
    private void task() {
    	int safetyCounter = 0;
    	while (d_thread_keepalive) {
    		d_dataAvailableSem.acquireUninterruptibly();
    		synchronized(this) {
    			getData();
    		}
    		synchronized(d_dataSem) {
    			d_dataSem.notifyAll();
    		}
    		if (++safetyCounter >= 4) {
    			//TODO MotorSafetyHelper.checkMotors();
    			safetyCounter = 0;
    		}
    		if (d_userInDisabled) {
    			FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramDisabled();
    		}
    		if (d_userInAutonomous) {
    			FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramAutonomous();
    		}
    		if (d_userInTeleop) {
    			FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramTeleop();
    		}
    		if (d_userInTest) {
    			FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramTest();
    		}
    	}
    }
    
    /**
     * Wait for new data from the driver station
     */
    public void waitForData() {
    	waitForData(0);
    }
    
    /**
     * Wait for new data or for timeout, whichever comes first. IF timeout is 0
     * wait for new data only
     * 
     * @param timeout The maximum time in milliseconds to wait
     */
    public void waitForData(long timeout) {
    	synchronized (d_dataSem) {
    		try {
    			d_dataSem.wait(timeout);
    		} catch (InterruptedException e) {}
		}
    }
    
    /**
     * Copy data from the DS task for the user. If no new data exists, it'll just be
     * returned. Otherwise, copied from DS
     */
    protected synchronized void getData() {
    	// Get status of all the joysticks
    	for (byte stick = 0; stick < kJoystickPorts; stick++) {
    		d_joystickAxes[stick] = FRCNetworkCommunicationsLibrary.HALGetJoystickAxes(stick);
    		d_joystickPOVs[stick] = FRCNetworkCommunicationsLibrary.HALGetJoystickPOVs(stick);
    		ByteBuffer countBuffer = ByteBuffer.allocateDirect(1);
    		d_joystickButtons[stick].buttons = 
    				FRCNetworkCommunicationsLibrary.HALGetJoystickButtons(stick, countBuffer);
    		d_joystickButtons[stick].count = countBuffer.get();
    	}
    	d_newControlData = true;
    }
    
    public double getBatteryVoltage() {
    	// TODO - Obtain from Skynet Endpoint
    	return 12.2;
    }
    
    private void reportJoystickUnpluggedError(String message) {
    	double currentTime = Timer.getFPGATimestamp();
    	if (currentTime > d_nextMessageTime) {
    		reportError(message, false);
    		d_nextMessageTime = currentTime + JOYSTICK_UNPLUGGED_MESSAGE_INTERVAL;
    	}
    }
    
    public synchronized double getStickAxis(int stick, int axis) {
    	if (stick < 0 || stick >= kJoystickPorts) {
    		throw new RuntimeException("Joystick index is out of range, should be 0-" + (kJoystickPorts-1));
    	}
    	if (axis < 0 || axis >= FRCNetworkCommunicationsLibrary.kMaxJoystickAxes) {
    		throw new RuntimeException("Joystick axis is out of range");
    	}
    	
    	if (axis >= d_joystickAxes[stick].length) {
    		reportJoystickUnpluggedError("WARNING: Joystick axis " + axis + " on port " + stick
    				+ " not available, checkif controller is plugged in\n");
    		return 0.0;
    	}
    	
    	byte value = (byte) d_joystickAxes[stick][axis];
    	if (value < 0) {
    		return value / 128.0;
    	}
    	else {
    		return value / 127.0;
    	}
    }
    
    public synchronized int getStickAxisCount(int stick) {
    	if (stick < 0 || stick >= kJoystickPorts) {
    		throw new RuntimeException("Joystick index is out of range, should be 0-" + (kJoystickPorts-1));
    	}
    	return d_joystickAxes[stick].length;
    }
    
    public synchronized int getStickPOV(int stick, int pov) {
    	if (stick < 0 || stick >= kJoystickPorts) {
    		throw new RuntimeException("Joystick index is out of range, should be 0-" + (kJoystickPorts-1));
    	}
    	if (pov < 0 || pov >= FRCNetworkCommunicationsLibrary.kMaxJoystickPOVs) {
    		throw new RuntimeException("Joystick POV is out of range");
    	}
    	if (pov >= d_joystickPOVs[stick].length){
    		reportJoystickUnpluggedError("WARNING: Joystick POV " + pov + " on port " + stick
    				+ " not available, check if controller is plugged in \n");
    		return -1;
    	}
    	return d_joystickPOVs[stick][pov];
    }
    
    public synchronized int getStickPOVCount(int stick) {
    	if (stick < 0 || stick >= kJoystickPorts) {
    		throw new RuntimeException("Joystick index is out of range, should be 0-" + (kJoystickPorts-1));
    	}
    	return d_joystickPOVs[stick].length;
    }
    
    public synchronized int getStickButtons(final int stick) {
    	if (stick < 0 || stick >= kJoystickPorts) {
    		throw new RuntimeException("Joystick index is out of range, should be 0-" + (kJoystickPorts-1));
    	}
    	
    	return d_joystickButtons[stick].buttons;
    }
    
    /**
     * The state of one joystick button. Button indices begin at 1
     * @param stick The joystick to read
     * @param button The button index, beginnign at 1
     * @return The state of the joystick button
     */
    public synchronized boolean getStickButton(final int stick, byte button) {
    	if (stick < 0 || stick >= kJoystickPorts) {
    		throw new RuntimeException("Joystick index is out of range, should be 0-" + (kJoystickPorts-1));
    	}
    	if (button > d_joystickButtons[stick].count){
    		reportJoystickUnpluggedError("WARNING: Joystick Button " + button + " on port " + stick
    				+ " not available, check if controller is plugged in\n");
    		return false;
    	}
    	if (button <= 0) {
    		reportJoystickUnpluggedError("ERROR: Button indices begin at 1 in WPILib for C++ and Java\n");
    		return false;
    	}
    	return ((0x1 << (button - 1)) & d_joystickButtons[stick].buttons) != 0;
    }
    
    public synchronized int getStickButtonCount(int stick) {
    	if (stick < 0 || stick >= kJoystickPorts) {
    		throw new RuntimeException("Joystick index is out of range, should be 0-" + (kJoystickPorts-1));
    	}
    	
    	return d_joystickButtons[stick].count;
    }
    
    public synchronized boolean getJoystickIsXbox(int stick) {
    	if (stick < 0 || stick >= kJoystickPorts) {
    		throw new RuntimeException("Joystick index is out of range, should be 0-" + (kJoystickPorts-1));
    	}
    	
    	if (1 > d_joystickButtons[stick].count && 1 > d_joystickAxes[stick].length) {
    		reportJoystickUnpluggedError("WARNING: Joystick on port " + stick 
    				+ " not avaialble, check if controller is plugged in\n");
    		return false;
    	}
    	boolean retVal = false;
    	if (FRCNetworkCommunicationsLibrary.HALGetJoystickIsXbox((byte)stick) == 1) {
    		retVal = true;
    	}
    	return retVal;
    }
    
    public synchronized int getJoystickType(int stick) {
    	if (stick < 0 || stick >= kJoystickPorts) {
    		throw new RuntimeException("Joystick index is out of range, should be 0-" + (kJoystickPorts-1));
    	}
    	
    	if (1 > d_joystickButtons[stick].count && 1 > d_joystickAxes[stick].length) {
    		reportJoystickUnpluggedError("WARNING: Joystick on port " + stick 
    				+ " not avaialble, check if controller is plugged in\n");
    		return -1;
    	}
    	return FRCNetworkCommunicationsLibrary.HALGetJoystickType((byte)stick);
    }
    
    public synchronized String getJoystickName(int stick) {
    	if (stick < 0 || stick >= kJoystickPorts) {
    		throw new RuntimeException("Joystick index is out of range, should be 0-" + (kJoystickPorts-1));
    	}
    	
    	if (1 > d_joystickButtons[stick].count && 1 > d_joystickAxes[stick].length) {
    		reportJoystickUnpluggedError("WARNING: Joystick on port " + stick 
    				+ " not avaialble, check if controller is plugged in\n");
    		return "";
    	}
    	return FRCNetworkCommunicationsLibrary.HALGetJoystickName((byte)stick);
    }
    
	@Override
	public boolean isDisabled() {
		return !isEnabled();
	}

	@Override
	public boolean isEnabled() {
		HALControlWord controlWord = FRCNetworkCommunicationsLibrary.HALGetControlWord();
		return controlWord.getEnabled() && controlWord.getDSAttached();
	}

	@Override
	public boolean isOperatorControl() {
		return !(isAutonomous() || isTest());
	}

	@Override
	public boolean isAutonomous() {
		HALControlWord controlWord = FRCNetworkCommunicationsLibrary.HALGetControlWord();
		return controlWord.getAutonomous();
	}

	@Override
	public boolean isTest() {
		HALControlWord controlWord = FRCNetworkCommunicationsLibrary.HALGetControlWord();
		return controlWord.getTest();
	}

	public double getMatchTime() {
		return FRCNetworkCommunicationsLibrary.HALGetMatchTime();
	}
	
	public boolean isSysActive() {
		return FRCNetworkCommunicationsLibrary.HALGetSystemActive();
	}
	
	public boolean isBrownedOut() {
		return FRCNetworkCommunicationsLibrary.HALGetBrownedOut();
	}
	
	/**
	 * Has a new control packet from the DS arrived since the last call
	 * to this function
	 * @return True if control data has been updated since last call
	 */
	public synchronized boolean isNewControlData() {
		boolean result = d_newControlData;
		d_newControlData = false;
		return result;
	}
	
	public Alliance getAlliance() {
		HALAllianceStationID allianceStationID =
				FRCNetworkCommunicationsLibrary.HALGetAllianceStation();
		if (allianceStationID == null) {
			return Alliance.Invalid;
		}
		
		switch (allianceStationID) {
			case Red1:
			case Red2:
			case Red3:
				return Alliance.Red;
				
			case Blue1:
			case Blue2:
			case Blue3:
				return Alliance.Blue;
				
			default:
				return Alliance.Invalid;
		}
	}
	
	/**
	 * Gets the location of the team's DS controls
	 * @return The location of the control (1, 2, 3)
	 */
	public int getLocation() {
		HALAllianceStationID allianceStationID =
				FRCNetworkCommunicationsLibrary.HALGetAllianceStation();
		if (allianceStationID == null) {
			return 0;
		}
		
		switch (allianceStationID) {
			case Red1:
			case Blue1:
				return 1;
				
			case Red2:
			case Blue2:
				return 2;
				
			case Red3:
			case Blue3:
				return 3;
				
			default:
				return 0;
		}
	}
	
	public boolean isFMSAttached() {
		HALControlWord controlWord = FRCNetworkCommunicationsLibrary.HALGetControlWord();
		return controlWord.getFMSAttached();
	}
	
	public boolean isDSAttached() {
		HALControlWord controlWord = FRCNetworkCommunicationsLibrary.HALGetControlWord();
		return controlWord.getDSAttached();
	}
	
	public static void reportError(String error, boolean printTrace) {
		String errorString = error;
		if (printTrace) {
			errorString += " at ";
			StackTraceElement[] traces = Thread.currentThread().getStackTrace();
			for (int i = 2; i < traces.length; i++) {
				errorString += traces[i].toString() + "\n";
			}
		}
		System.err.println(errorString);
		HALControlWord controlWord = FRCNetworkCommunicationsLibrary.HALGetControlWord();
		if (controlWord.getDSAttached()) {
			FRCNetworkCommunicationsLibrary.HALSetErrorData(errorString);
		}
	}
	
	// The following are only used to tell the DS what code we clain to be
	// running, for diagnostic purposes only
	
	public void InDisabled(boolean entering) {
		d_userInDisabled = entering;
	}
	
	public void InAutonomous(boolean entering) {
		d_userInAutonomous = entering;
	}
	
	public void InOperatorControl(boolean entering) {
		d_userInTeleop = entering;
	}
	
	public void InTest(boolean entering) {
		d_userInTest = entering;
	}
}
