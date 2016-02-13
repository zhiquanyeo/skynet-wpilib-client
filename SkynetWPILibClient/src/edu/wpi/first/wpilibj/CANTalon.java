package edu.wpi.first.wpilibj;

import com.zhiquanyeo.skynet.system.SimCANNode;

public class CANTalon extends SimCANNode implements MotorSafety, PIDOutput, SpeedController {
	
	private MotorSafetyHelper d_safetyHelper;
	
	public enum TalonControlMode {
		PercentVbus(0), Follower(5), Voltage(4), Position(1), Speed(2), Current(3), Disabled(15);
		
		public int value;
		public static TalonControlMode valueOf(int value) {
			for (TalonControlMode mode : values()) {
				if (mode.value == value) {
					return mode;
				}
			}
			return null;
		}
		
		private TalonControlMode(int value) {
			this.value = value;
		}
	}
	
	public enum FeedbackDevice {
		QuadEncoder(0), AnalogPot(2), AnalogEncoder(3), EncRising(4), EncFalling(5);
		
		public int value;
		
		public static FeedbackDevice valueOf(int value) {
			for (FeedbackDevice mode : values()) {
				if (mode.value == value) {
					return mode;
				}
			}
			return null;
		}
		
		private FeedbackDevice(int value) {
			this.value = value;
		}
	}
	
	public enum StatusFrameRate {
		General(0), Feedback(1), QuadEncoder(2), AnalogTempVbat(3);
		
		public int value;
		public static StatusFrameRate valueOf(int value) {
			for (StatusFrameRate mode : values()) {
				if (mode.value == value) {
					return mode;
				}
			}
			return null;
		}
		
		private StatusFrameRate(int value) {
			this.value = value;
		}
	}
	
	private TalonControlMode d_controlMode;
	private boolean d_controlEnabled;
	private int d_profile;
	private double d_setPoint;
	
	// ID of the Talon we are following
	private int d_followingId;
	
	public CANTalon(int id) {
		super(id);
	}

	@Override
	public double get() {
		return d_setPoint;
	}

	@Override
	public void set(double speed, byte syncGroup) {
		set(speed);
	}

	@Override
	public void set(double speed) {
		d_setPoint = speed;
		
		// Broadcast to the CAN Network
		// We will say that this is a message to ourselves
		// That way, we can listen out for follower messages
		this.sendBusMessage(d_id, "setSpeed", Double.toString(d_setPoint));
	}

	@Override
	public void disable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pidWrite(double output) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setExpiration(double timeout) {
		// TODO Auto-generated method stub

	}

	@Override
	public double getExpiration() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isAlive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void stopMotor() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setSafetyEnabled(boolean enabled) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isSafetyEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onBusMessage(int source, int dest, String topic, String data) {
		// Modify this slightly to listen out for messages from the talon we are following
		if ((dest == -1 || dest == d_id || (d_controlMode == TalonControlMode.Follower && d_followingId == dest)) && source != d_id) {
			onMessageReceived(source, topic, data);
		}
	}
	
	@Override
	protected void onMessageReceived(int source, String topic, String data) {
		if (topic.equals("setSpeed")) {
			this.set(Double.parseDouble(data));
		}
	}

}
