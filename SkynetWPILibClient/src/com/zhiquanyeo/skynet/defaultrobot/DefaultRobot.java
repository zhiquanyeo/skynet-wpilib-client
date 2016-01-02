package com.zhiquanyeo.skynet.defaultrobot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SampleRobot;

public class DefaultRobot extends SampleRobot {
	private Joystick d_joystick;
	private RobotDrive d_drivetrain;
	private AnalogInput d_frontRangefinder;
	private AnalogInput d_rearRangefinder;
	private DigitalInput d_frontSwitch;
	private DigitalInput d_rearSwitch;
	
	private DigitalOutput d_outputLED;
	
	@Override
	protected void robotInit() {
		System.out.println("Default SkynetRobot starting up");
		
		d_joystick = new Joystick(1);
		d_drivetrain = new RobotDrive(0, 1);
		
		d_frontRangefinder = new AnalogInput(0);
		d_rearRangefinder = new AnalogInput(1);
		
		d_frontSwitch = new DigitalInput(0);
		d_rearSwitch = new DigitalInput(1);
		
		d_outputLED = new DigitalOutput(2);
	}
	
	@Override
	protected void operatorControl() {
		while (isEnabled() && isOperatorControl()) {
			d_drivetrain.arcadeDrive(d_joystick);
			
			d_outputLED.set(d_joystick.getRawButton(1));
		}
	}
}
