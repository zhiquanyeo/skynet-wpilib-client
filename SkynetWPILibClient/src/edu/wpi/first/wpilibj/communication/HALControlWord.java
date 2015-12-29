package edu.wpi.first.wpilibj.communication;

public class HALControlWord {
	private boolean d_enabled;
	private boolean d_autonomous;
	private boolean d_test;
	private boolean d_eStop;
	private boolean d_fmsAttached;
	private boolean d_dsAttached;
	
	protected HALControlWord(boolean enabled, boolean autonomous, boolean test, boolean eStop,
		      boolean fmsAttached, boolean dsAttached) {
	    d_enabled = enabled;
	    d_autonomous = autonomous;
	    d_test = test;
	    d_eStop = eStop;
	    d_fmsAttached = fmsAttached;
	    d_dsAttached = dsAttached;
	}
	
	public boolean getEnabled() {
		return d_enabled;
	}
	
	public boolean getAutonomous() {
		return d_autonomous;
	}
	
	public boolean getTest() {
		return d_test;
	}
	
	public boolean getEStop() {
		return d_eStop;
	}
	
	public boolean getFMSAttached() {
		return d_fmsAttached;
	}
	
	public boolean getDSAttached() {
		return d_dsAttached;
	}
}
