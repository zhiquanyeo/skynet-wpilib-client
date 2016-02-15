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
		QuadEncoder(0), AnalogPot(2), AnalogEncoder(3), EncRising(4), EncFalling(5), CtreMagEncoder_Relative(6), CtreMagEncoder_Absolute(7), PulseWidth(8);;
		
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
		d_safetyHelper = new MotorSafetyHelper(this);
		d_controlEnabled = true;
		d_profile = 0;
		d_setPoint = 0;
		
	    setProfile(d_profile);
	    applyControlMode(TalonControlMode.PercentVbus);
	}

	@Override
	public double get() {
		switch (d_controlMode) {
			case Voltage:
				return getOutputVoltage();
			case Current:
				return 0.0;
			case Speed:
				return d_setPoint;
			case Position:
				return 0.0;
			case PercentVbus:
			default:
				return d_setPoint;
		}
	}

	@Override
	public void set(double speed, byte syncGroup) {
		set(speed);
	}

	@Override
	public void set(double outputValue) {
		d_safetyHelper.feed();
		if (d_controlEnabled) {
			switch (d_controlMode) {
				case PercentVbus:
					setSpeed(outputValue);
					break;
				case Follower:
					d_followingId = (int)outputValue;
					break;
				case Voltage:
					setSpeed(outputValue / 12.0);
					break;
				case Speed:
					setSpeed(outputValue);
					break;
				case Position:
					// TBD Implement
					break;
			}
		}
	}
	
	protected void setSpeed(double speed) {
		d_setPoint = speed;
		
		// Broadcast to the CAN Network
		// We will say that this is a message to ourselves
		// That way, we can listen out for follower messages
		this.sendBusMessage(d_id, "setSpeed", Double.toString(d_setPoint));
	}
	
	public void reverseSensor(boolean flip) {
		// Does nothing
	}
	
	public void reverseOutput(boolean flip) {
		// Does nothing
	}
	
	public int getEncPosition() {
		return 0;
	}
	
	public int getEncVelocity() {
		return 0;
	}
	
	public int getNumberOfQuadIdxRises() {
		return 0;
	}
	
	public int getPinStateQuadA() {
		return 0;
	}
	
	public int getPinStateQuadB() {
		return 0;
	}
	
	public int getPinStateQuadIdx() {
		return 0;
	}
	
	public int getAnalogInPosition() {
		return 0;
	}
	
	public int getAnalogInRaw() {
		return 0;
	}
	
	public int getAnalogInVelocity() {
		return 0;
	}
	
	public int getClosedLoopError() {
		return 0;
	}
	
	public boolean isFwdLimitSwitchClosed() {
		return false;
	}
	
	public boolean isRevLimitSwitchClosed() {
		return false;
	}
	
	public boolean getBrakeEnableDuringNeutral() {
		return true;
	}
	
	public double getTemp() {
		return 20.0;
	}
	
	public double getOutputCurrent() {
		return 1.0;
	}
	
	public double getOutputVoltage() {
		return d_setPoint * 12.0;
	}
	
	public double getBusVoltage() {
		return 12.0;
	}
	
	public double getPosition() {
		return 0.0;
	}
	
	public void setPosition(double pos) {
		// Do nothing
	}
	
	public double getSpeed() {
		return d_setPoint;
	}
	
	public TalonControlMode getControlMode() {
		return d_controlMode;
	}
	
	public void configNominalOutputVoltage(double forwardVoltage, double reverseVoltage) {}
	
	public void configPeakOutputVoltage(double forwardVoltage, double reverseVoltage) {}
	
	private void applyControlMode(TalonControlMode mode) {
		d_controlMode = mode;
		if (mode == TalonControlMode.Disabled) {
			d_controlEnabled = false;
		}
		this.sendBusMessage(d_id, "disabled", "true");
	}
	
	public void changeControlMode(TalonControlMode mode) {
		if (d_controlMode == mode) {
			
		}
		else {
			applyControlMode(mode);
		}
	}
	
	public void setFeedbackDevice(FeedbackDevice device) {}
	
	public void setStatusFrameRateMs(StatusFrameRate stateFrame, int periodMs) {}
	
	public void enableControl() {
		changeControlMode(d_controlMode);
		this.sendBusMessage(d_id, "disabled", "false");
		d_controlEnabled = true;
	}
	
	public void disableControl() {
		this.sendBusMessage(d_id, "disabled", "true");
		d_controlEnabled = false;
	}
	
	public boolean isControlEnabled() {
		return d_controlEnabled;
	}
	
	public double getP() {
		return 0.0;
	}
	
	public double getI() {
		return 0.0;
	}
	
	public double getD() {
		return 0.0;
	}
	
	public double getF() {
		return 0.0;
	}
	
	public double getIZone() {
		return 0.0;
	}
	
	public double getCloseLoopRampRate() {
		return 1.0;
	}
	
	public long getFirmwareVersion() {
		return 2;
	}
	
	public long getIaccum() {
		return 0;
	}
	
	public void clearIaccum() {}
	
	public void setP(double p) {}
	
	public void setI(double i) {}
	
	public void setD(double d) {}
	
	public void setF(double f) {}
	
	public void setIZone(int zone) {}
	
	public void setCloseLoopRampRate(double rate) {}
	
	public void setVoltageRampRate(double rate) {}
	
	public void setPID(double p, double i, double d, double f, int izone, double closeLoopRampRate, int profile) {}
	
	public void setPID(double p, double i, double d) {}
	
	public double getSetpoint() {
		return d_setPoint;
	}
	
	public void setProfile(int profile) {}

	@Override
	public void disable() {
		disableControl();
	}
	
	public int getDeviceID() {
		return d_id;
	}
	
	public void setForwardSoftLimit(int forwardLimit) {}
	
	public void enableForwardSoftLimit(boolean enable) {}
	
	public void setReverseSoftLimit(int reverseLimit) {}
	
	public void enableReverseSoftLimit(boolean enable) {}
	
	public void clearStickyFaults() {}
	
	public void enableLimitSwitch(boolean forward, boolean reverse) {}
	
	public void ConfigFwdLimitSwitchNormallyOpen(boolean normallyOpen) {}
	
	public void ConfigRevLimitSwitchNormallyOpen(boolean normallyOpen) {}
	
	public void enableBrakeMode(boolean brake) {}
	
	public int getFaultOverTemp() {
		return 0;
	}
	
	public int getFaultUnderVoltage() {
		return 0;
	}
	
	public int getFaultForLim() {
		return 0;
	}
	
	public int getFaultRevLim() {
		return 0;
	}
	
	public int getFaultHardwareFailure() {
		return 0;
	}
	
	public int getFaultForSoftLim() {
		return 0;
	}
	
	public int getFaultRevSoftLim() {
		return 0;
	}
	
	public int getStickyFaultOverTemp() {
		return 0;
	}
	
	public int getStickyFaultUnderVoltage() {
		return 0;
	}
	
	public int getStickyFaultForLim() {
		return 0;
	}
	
	public int getStickyFaultRevLim() {
		return 0;
	}
	
	public int getStickyFaultForSoftLim() {
		return 0;
	}
	
	public int getStickyFaultRevSoftLim() {
		return 0;
	}

	@Override
	public void pidWrite(double output) {
		if (d_controlMode == TalonControlMode.PercentVbus) {
			set(output);
		}
		else {
			throw new IllegalStateException("PID only supported in PercentVbus mode");
		}
	}

	@Override
	public void setExpiration(double timeout) {
		d_safetyHelper.setExpiration(timeout);
	}

	@Override
	public double getExpiration() {
		return d_safetyHelper.getExpiration();
	}

	@Override
	public boolean isAlive() {
		return d_safetyHelper.isAlive();
	}

	@Override
	public void stopMotor() {
		disableControl();
	}

	@Override
	public void setSafetyEnabled(boolean enabled) {
		d_safetyHelper.setSafetyEnabled(enabled);
	}

	@Override
	public boolean isSafetyEnabled() {
		return d_safetyHelper.isSafetyEnabled();
	}

	@Override
	public String getDescription() {
		return "CAN TalonSRX ID " + d_id;
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
			// If we're getting this message, it's because we are in follower mode
			this.setSpeed(Double.parseDouble(data));
		}
		else if (topic.equals("disabled")) {
			if (data == "true") {
				this.disableControl();
			}
			else {
				this.enableControl();
			}
		}
	}

}
