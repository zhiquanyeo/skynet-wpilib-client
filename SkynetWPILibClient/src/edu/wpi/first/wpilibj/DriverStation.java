package edu.wpi.first.wpilibj;

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
    public static class Alliance {

        /** The integer value representing this enumeration. */
        public final int value;
        /** The Alliance name. */
        public final String name;
        public static final int kRed_val = 0;
        public static final int kBlue_val = 1;
        public static final int kInvalid_val = 2;
        /** alliance: Red */
        public static final Alliance kRed = new Alliance(kRed_val, "Red");
        /** alliance: Blue */
        public static final Alliance kBlue = new Alliance(kBlue_val, "Blue");
        /** alliance: Invalid */
        public static final Alliance kInvalid = new Alliance(kInvalid_val, "invalid");

        private Alliance(int value, String name) {
            this.value = value;
            this.name = name;
        }
    } /* Alliance */
    
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
    private final Object d_dataSem;
    private Thread d_thread;
    private boolean d_thread_keepalive = true;
    
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
