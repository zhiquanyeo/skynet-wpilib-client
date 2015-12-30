package edu.wpi.first.wpilibj.buttons;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.tables.ITable;

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
	
	public void whenInactive(final Command command) {
		new ButtonScheduler() {
			boolean pressedLast = grab();
			
			public void execute() {
				if (grab()) {
					pressedLast = true;
				}
				else {
					if (pressedLast) {
						pressedLast = false;
						command.start();
					}
				}
			}
		}.start();
	}
	
	public void toggleWhenActive(final Command command) {
		new ButtonScheduler() {
			boolean pressedLast = grab();
			
			public void execute() {
				if (grab()) {
					if (!pressedLast) {
						pressedLast = true;
						if (command.isRunning()) {
							command.cancel();
						}
						else {
							command.start();
						}
					}
				}
				else {
					pressedLast = false;
				}
			}
		}.start();
	}
	
	public void cancelWhenActive(final Command command) {
		new ButtonScheduler() {
			boolean pressedLast = grab();
			
			public void execute() {
				if (grab()) {
					if (!pressedLast) {
						pressedLast = true;
						command.cancel();
					}
					else {
						pressedLast = false;
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
	
	public String getSmartDashboardType() {
		return "Button";
	}
	
	private ITable table;
	
	public void initTable(ITable table) {
		this.table = table;
		// TODO Implement
	}
	
	public ITable getTable() {
		return table;
	}
}
