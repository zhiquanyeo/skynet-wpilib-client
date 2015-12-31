package edu.wpi.first.wpilibj.buttons;

public class InternalButton extends Button {
	
	boolean pressed;
	boolean inverted;
	
	public InternalButton() {
		this(false);
	}
	
	public InternalButton(boolean inverted) {
		this.pressed = this.inverted = inverted;
	}
	
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}
	
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}
	
	@Override
	public boolean get() {
		return pressed ^ inverted;
	}

}
