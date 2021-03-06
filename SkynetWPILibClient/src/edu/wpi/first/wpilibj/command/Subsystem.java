package edu.wpi.first.wpilibj.command;

import java.util.Enumeration;

import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.tables.ITable;

public abstract class Subsystem implements NamedSendable {
	private boolean initializedDefaultCommand = false;
	private Command currentCommand;
	private boolean currentCommandChanged;
	
	private Command defaultCommand;
	private String name;
	//private static Vector<Subsystem> allSubsystems = new Vector<>();
	
	public Subsystem(String name) {
		this.name = name;
		Scheduler.getInstance().registerSubsystem(this);
	}
	
	public Subsystem() {
		this.name = getClass().getName().substring(getClass().getName().lastIndexOf('.') + 1);
		Scheduler.getInstance().registerSubsystem(this);
		currentCommandChanged = true;
	}
	
	protected abstract void initDefaultCommand();
	
	protected void setDefaultCommand(Command command) {
		if (command == null) {
			defaultCommand = null;
		}
		else {
			boolean found = false;
			Enumeration<Subsystem> requirements = command.getRequirements();
			while(requirements.hasMoreElements()) {
				if (requirements.nextElement().equals(this)) {
					found = true;
				}
			}
			if (!found) {
				throw new IllegalUseOfCommandException("A default command must require the subsystem");
			}
			defaultCommand = command;
		}
		
		if (table != null) {
			if (defaultCommand != null) {
				table.putBoolean("hasDefault", true);
				table.putString("default", defaultCommand.getName());
			}
			else {
				table.putBoolean("hasDefault", false);
			}
		}
	}
	
	protected Command getDefaultCommand() {
		if (!initializedDefaultCommand) {
			initializedDefaultCommand = true;
			initDefaultCommand();
		}
		return defaultCommand;
	}
	
	void setCurrentCommand(Command command) {
		currentCommand = command;
		currentCommandChanged = true;
	}
	
	void confirmCommand() {
		if (currentCommandChanged) {
			if (table != null) {
				if (currentCommand != null) {
					table.putBoolean("hasCommand", true);
					table.putString("command", currentCommand.getName());
				}
				else {
					table.putBoolean("hasCommand", false);
				}
			}
			currentCommandChanged = false;
		}
	}
	
	public Command getCurrentCommand() {
		return currentCommand;
	}
	
	public String toString() {
		return getName();
	}
	
	public String getName() {
		return name;
	}
	
	public String getSmartDashboardType() {
		return "Subsystem";
	}
	
	private ITable table;
	
	public void initTable(ITable table) {
		this.table = table;
		if (table != null) {
			if (defaultCommand != null) {
				table.putBoolean("hasDefault", true);
				table.putString("default", defaultCommand.getName());
			}
			else {
				table.putBoolean("hasDefault", false);
			}
			
			if (currentCommand != null) {
				table.putBoolean("hasCommand", true);
				table.putString("command", currentCommand.getName());
			}
			else {
				table.putBoolean("hasCommand", false);
			}
		}
	}
	
	public ITable getTable() {
		return table;
	}
}
