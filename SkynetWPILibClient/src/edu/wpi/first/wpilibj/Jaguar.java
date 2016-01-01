package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

public class Jaguar extends SafePWM implements SpeedController {

	private void initJaguar() {
		UsageReporting.report(tResourceType.kResourceType_Jaguar, getChannel());
        LiveWindow.addActuator("Jaguar", getChannel(), this);
	}
	
	public Jaguar(int channel) {
		super(channel);
		initJaguar();
	}

	@Override
	public void pidWrite(double output) {
		set(output);
	}

	@Override
	public double get() {
		return getSpeed();
	}

	@Override
	public void set(double speed, byte syncGroup) {
		setSpeed(speed);
		Feed();
	}

	@Override
	public void set(double speed) {
		setSpeed(speed);
		Feed();
	}

}
