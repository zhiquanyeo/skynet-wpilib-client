package edu.wpi.first.wpilibj;

public class SafePWM extends PWM implements MotorSafety {
	
	private MotorSafetyHelper d_safetyHelper;
	
	void initSafePWM() {
		d_safetyHelper = new MotorSafetyHelper(this);
		d_safetyHelper.setExpiration(0.0);
		d_safetyHelper.setSafetyEnabled(false);
	}
	
	public SafePWM(int channel) {
		super(channel);
		initSafePWM();
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
		disable();
	}

	@Override
	public void setSafetyEnabled(boolean enabled) {
		d_safetyHelper.setSafetyEnabled(enabled);
	}

	@Override
	public boolean isSafetyEnabled() {
		return d_safetyHelper.isSafetyEnabled();
	}
	
	public void Feed() {
		d_safetyHelper.feed();
	}
	
	public void disable() {
		setRaw(128);
	}

	@Override
	public String getDescription() {
		return "PWM" + getChannel();
	}

}
