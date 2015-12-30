package edu.wpi.first.wpilibj.internal;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Timer.Interface;

public class SkynetTimer implements Timer.StaticInterface {
	
	@Override
	public double getFPGATimestamp() {
		return System.currentTimeMillis() / 1e3;
	}

	@Override
	public double getMatchTime() {
		return DriverStation.getInstance().getMatchTime();
	}

	@Override
	public void delay(double seconds) {
		try {
			Thread.sleep((long)(seconds * 1e3));
		}
		catch (final InterruptedException e) {}
	}

	@Override
	public Interface newTimer() {
		return new TimerImpl();
	}
	
	class TimerImpl implements Timer.Interface {
		private long d_startTime;
		private double d_accumulatedTime;
		private boolean d_running;
		
		public TimerImpl() {
			reset();
		}
		
		private long getMsClock() {
			return System.currentTimeMillis();
		}
		
		/**
		 * Get the current time from the timer. If the clock is running,
		 * it is derived from the current system clock. Otherwise, it returns
		 * the time when it was last stopped
		 */
		@Override
		public synchronized double get() {
			if (d_running) {
				return ((double) ((getMsClock() - d_startTime) + d_accumulatedTime)) / 1000.0;
			}
			else {
				return d_accumulatedTime;
			}
		}
		
		/**
		 * Reset the timer by setting the time to 0
		 * Make startTime the current time to new requests
		 * will be relative
		 */
		@Override
		public synchronized void reset() {
			d_accumulatedTime = 0;
			d_startTime = getMsClock();
		}

		/**
		 * Start the timer
		 * Set the running flag to true, indicating that all time requests
		 * should be relative to system clock
		 */
		@Override
		public void start() {
			d_startTime = getMsClock();
			d_running = true;
		}
		
		/**
		 * Stop the timer
		 * This computes the time as of now and clears the running flag, causing all
		 * subsequent time requests to be read from the accumulated time rather than
		 * looking at the system clock
		 */
		@Override
		public synchronized void stop() {
			final double temp = get();
			d_accumulatedTime = temp;
			d_running = false;
		}

		@Override
		public boolean hasPeriodPassed(double period) {
			if (get() > period) {
				// Advance the start time by the period
				// Don't set it to the current time to avoid drift
				d_startTime += period;
				return true;
			}
			return false;
		}
		
	}
}
