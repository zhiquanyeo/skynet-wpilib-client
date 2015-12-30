package edu.wpi.first.wpilibj;

public class MotorSafetyHelper {
	double d_expiration;
	boolean d_enabled;
	double d_stopTime;
	MotorSafety d_safeObject;
	MotorSafetyHelper d_nextHelper;
	static MotorSafetyHelper d_headHelper = null;
	
	/**
	 * Constructor for a MotorSafetyHelper object. The helper object is
	 * constructed for every object that wants to implement the MotorSafety
	 * protocol. The helper object has the code to actually do the timing and 
	 * call the motors Stop() method when the timeout expires. The motor 
	 * object is expected to call the Feed() method whenever the value is updated
	 * 
	 * @param safeObject A pointer to the motor object implementing MotorSafety
	 */
	public MotorSafetyHelper(MotorSafety safeObject) {
		d_safeObject = safeObject;
		d_enabled = false;
		d_expiration = MotorSafety.DEFAULT_SAFETY_EXPIRATION;
		d_stopTime = Timer.getFPGATimestamp();
		d_nextHelper = d_headHelper;
		d_headHelper = this;
	}
	
	public void feed() {
		d_stopTime = Timer.getFPGATimestamp() + d_expiration;
	}
	
	public void setExpiration(double expirationTime) {
		d_expiration = expirationTime;
	}
	
	public double getExpiration() {
		return d_expiration;
	}
	
	public boolean isAlive() {
		return !d_enabled || d_stopTime > Timer.getFPGATimestamp();
	}
	
	public void check() {
		if (!d_enabled || RobotState.isDisabled() || RobotState.isTest()) return;
		if (d_stopTime < Timer.getFPGATimestamp()) {
			System.err.println(d_safeObject.getDescription() + "... Output not updated often enough.");
			d_safeObject.stopMotor();
		}
	}
	
	public void setSafetyEnabled(boolean enabled) {
		d_enabled = enabled;
	}
	
	public boolean isSafetyEnabled() {
		return d_enabled;
	}
	
	public static void checkMotors() {
		for (MotorSafetyHelper msh = d_headHelper; msh != null; msh = msh.d_nextHelper) {
			msh.check();
		}
	}
}
