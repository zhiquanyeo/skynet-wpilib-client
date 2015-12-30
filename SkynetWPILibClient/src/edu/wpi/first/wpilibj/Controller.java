package edu.wpi.first.wpilibj;

/**
 * An interface for controllers. Controllers run control loops, the most common
 * are PID controllers and their variants, but this includes anything that is
 * controlling an actuator in a separate thread
 * @author zyeo8
 *
 */
public interface Controller {
	/**
	 * Allows the control loop to run
	 */
	public void enable();
	
	/**
	 * Stops the control loop from running until explicitly renabled by calling
	 * enable()
	 */
	public void disable();
}
