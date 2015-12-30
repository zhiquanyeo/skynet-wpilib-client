package edu.wpi.first.wpilibj;

public abstract class GenericHID {
	
	/**
	 * Which hand the HID is associated with
	 */
	public static class Hand {
		/**
		 * Integer value representing this enumeration
		 */
		public final int value;
		static final int kLeft_val = 0;
		static final int kRight_val = 1;
		
		public static final Hand kLeft = new Hand(kLeft_val);
		public static final Hand kRight = new Hand(kRight_val);
		
		private Hand(int value) {
			this.value = value;
		}
	}
	
	public final double getX() {
		return getX(Hand.kRight);
	}
	
	public abstract double getX(Hand hand);
	
	public final double getY() {
		return getY(Hand.kRight);
	}
	
	public abstract double getY(Hand hand);
	
	public final double getZ() {
		return getZ(Hand.kRight);
	}
	
	public abstract double getZ(Hand hand);
	
	public abstract double getTwise();
	
	public abstract double getThrottle();
	
	public abstract double getRawAxis(int which);
	
	public final boolean getTrigger() {
		return getTrigger(Hand.kRight);
	}
	
	public abstract boolean getTrigger(Hand hand);
	
	public final boolean getTop() {
		return getTop(Hand.kRight);
	}
	
	public abstract boolean getTop(Hand hand);
	
	public final boolean getBumper() {
		return getBumper(Hand.kRight);
	}
	
	public abstract boolean getBumper(Hand hand);
	
	public abstract boolean getRawButton(int button);
	
	public abstract int getPOV(int pov);
	
	public int getPOV() {
		return getPOV(0);
	}
}
