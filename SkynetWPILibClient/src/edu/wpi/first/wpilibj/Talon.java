package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

public class Talon extends SafePWM implements SpeedController {

	private void initTalon() {
		LiveWindow.addActuator("Talon", getChannel(), this);
        UsageReporting.report(tResourceType.kResourceType_Talon, getChannel());
	}
	
	public Talon(int channel) {
		super(channel);
		initTalon();
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
