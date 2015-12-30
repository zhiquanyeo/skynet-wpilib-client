package edu.wpi.first.wpilibj.buttons;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

public abstract class Trigger implements Sendable {
	public abstract boolean get();
	
	public boolean grab() {
		// TODO Implement
		return false;
	}
	
	public void whenActive(final Command command) {
		new ButtonScheduler() {
			boolean pressedLast = grab();
			
			public void execute() {
				if (grab()) {
					if (!pressedLast) {
						pressedLast = true;
						command.start();
					}
					else {
						pressedLast = false;
					}
				}
			}
		}.start();
	}
	
	public void whileActive(final Command command) {
		new ButtonScheduler() {
			boolean pressedLast = grab();
			
			public void execute() {
				if (grab()) {
					pressedLast = true;
					command.start();
				}
				else {
					if (pressedLast) {
						pressedLast = false;
						command.cancel();
					}
				}
			}
		}.start();
	}
	
	public abstract class ButtonScheduler {
		public abstract void execute();
		protected void start() {
			Scheduler.getInstance().addButton(this);
		}
	}
}
