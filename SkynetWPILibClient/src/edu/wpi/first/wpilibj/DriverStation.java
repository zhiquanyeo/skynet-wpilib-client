package edu.wpi.first.wpilibj;

import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;

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
    		d_dataAvailableSem.acquire();
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
    	return 12.2;
    }
    
	@Override
	public boolean isDisabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOperatorControl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAutonomous() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isTest() {
		// TODO Auto-generated method stub
		return false;
	}

}
