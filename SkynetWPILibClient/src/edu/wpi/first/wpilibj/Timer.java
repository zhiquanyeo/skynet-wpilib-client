package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.util.BaseSystemNotInitializedException;

public class Timer {
	private static StaticInterface impl;
	
	public static void SetImplementation(StaticInterface ti) {
		impl = ti;
	}
	
	/**
	 * Return the system clock time in seconds.
	 * @return Robot running time in seconds
	 */
	public static double getFPGATimestamp() {
		if (impl != null) {
			return impl.getFPGATimestamp();
		}
		else {
			throw new BaseSystemNotInitializedException(StaticInterface.class, Timer.class);
		}
	}
	
	/**
	 * Return the approximate match time. The returns the
	 * time since the enable signal sent from the DS. At the beginning
	 * of autonomous, the time is reset to 0.0 seconds. At the beginning
	 * of teleop, the time is reset to +15.0 seconds. If the robot is disabled,
	 * this return 0.0 seconds. 
	 * @return Match time in seconds since the beginning of autonomous
	 */
	public static double getMatchTime() {
		if (impl != null) {
			return impl.getMatchTime();
		}
		else {
			throw new BaseSystemNotInitializedException(StaticInterface.class, Timer.class);
		}
	}
	
	/**
	 * Pause the thread for a specified time. Motors will continue to run
	 * at their last assigned values, and sensors will continue to update.
	 * @param seconds Length of time to pause
	 */
	public static void delay(final double seconds) {
		if (impl != null) {
			impl.delay(seconds);
		}
		else {
			throw new BaseSystemNotInitializedException(StaticInterface.class, Timer.class);
		}
	}
	
	public interface StaticInterface {
		double getFPGATimestamp();
		double getMatchTime();
		void delay(final double seconds);
		Interface newTimer();
	}
	
	private final Interface timer;
	
	public Timer() {
		if (impl != null) {
			timer = impl.newTimer();
		}
		else {
			throw new BaseSystemNotInitializedException(StaticInterface.class, Timer.class);
		}
	}
	
	public double get() {
		return timer.get();
	}
	
	public void reset() {
		timer.reset();
	}
	
	public void start() {
		timer.start();
	}
	
	public void stop() {
		timer.stop();
	}
	
	public boolean hasPeriodPassed (double period) {
		return timer.hasPeriodPassed(period);
	}
	
	public interface Interface {
		public double get();
		public void reset();
		public void start();
		public void stop();
		public boolean hasPeriodPassed(double period);
	}
}
