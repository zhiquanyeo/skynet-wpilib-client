package edu.wpi.first.wpilibj;

public interface MotorSafety {
	public static final double DEFAULT_SAFETY_EXPIRATION = 0.1;
	
	void setExpiration(double timeout);
	double getExpiration();
	boolean isAlive();
	void stopMotor();
	void setSafetyEnabled(boolean enabled);
	boolean isSafetyEnabled();
	String getDescription();
}
