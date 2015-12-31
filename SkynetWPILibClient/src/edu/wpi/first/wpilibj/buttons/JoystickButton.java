package edu.wpi.first.wpilibj.buttons;

import edu.wpi.first.wpilibj.GenericHID;

public class JoystickButton extends Button {

	GenericHID d_joystick;
	int d_buttonNumber;
	
	public JoystickButton(GenericHID joystick, int buttonNumber) {
		d_joystick = joystick;
		d_buttonNumber = buttonNumber;
	}
	
	@Override
	public boolean get() {
		return d_joystick.getRawButton(d_buttonNumber);
	}

}
