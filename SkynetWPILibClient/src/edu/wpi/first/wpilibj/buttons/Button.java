package edu.wpi.first.wpilibj.buttons;

import edu.wpi.first.wpilibj.command.Command;

public abstract class Button extends Trigger {
	
	public void whenPressed(final Command command) {
		whenActive(command);
	}
	
	public void whileHeld(final Command command) {
		whileActive(command);
	}
	
	public void whenReleased(final Command command) {
		whenInactive(command);
	}
	
	public void toggleWhenPressed(final Command command) {
		toggleWhenActive(command);
	}
	
	public void cancelWhenPressed(final Command command) {
		cancelWhenActive(command);
	}

}
