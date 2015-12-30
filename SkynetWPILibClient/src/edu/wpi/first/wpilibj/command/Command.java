package edu.wpi.first.wpilibj.command;

import java.util.Enumeration;
import java.util.NoSuchElementException;

import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.tables.ITable;

public abstract class Command implements NamedSendable {
	private String d_name;
	private double d_startTime = -1;
	private double d_timeout = -1;
	private boolean d_initialized = false;
	private Set d_requirements;
	private boolean d_running = false;
	private boolean d_interruptible = true;
	private boolean d_canceled = false;
	private boolean d_locked = false;
	private boolean d_runWhenDisabled = false;
	
	private CommandGroup d_parent;
	
	public Command() {
		d_name = getClass().getName();
		d_name = d_name.substring(d_name.lastIndexOf(',') + 1);
	}
	
	public Command(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Name must not be null.");
		}
		d_name = name;
	}
	
	public Command(double timeout) {
		this();
		if (timeout < 0) {
			throw new IllegalArgumentException("Timeout must not be negative. Given: " + timeout);
		}
		d_timeout = timeout;
	}
	
	public Command(String name, double timeout) {
		this(name);
		if (timeout < 0) {
			throw new IllegalArgumentException("Timeout must not be negative. Given: " + timeout);
		}
		d_timeout = timeout;
	}
	
	public String getName() {
		return d_name;
	}
	
	protected synchronized final void setTimeout(double seconds) {
		if (seconds < 0) {
			throw new IllegalArgumentException("Seconds must be positive. Given: " + seconds);
		}
		d_timeout = seconds;
	}
	
	public synchronized final double timeSinceInitialized() {
		return d_startTime < 0 ? 0 : Timer.getFPGATimestamp() - d_startTime;
	}
	
	protected synchronized void requires(Subsystem subsystem) {
		validate("Cannot add new requirement to command");
		if (subsystem != null) {
			if (d_requirements == null) {
				d_requirements = new Set();
			}
			d_requirements.add(subsystem);
		}
		else {
			throw new IllegalArgumentException("Subsystem must not be null");
		}
	}
	
	synchronized void removed() {
		if (d_initialized) {
			if (isCanceled()) {
				interrupted();
				_interrupted();
			}
			else {
				end();
				_end();
			}
		}
		d_initialized = false;
		d_canceled = false;
		d_running = false;
		if (table != null) {
			//TODO Implement
		}
	}
	
	synchronized boolean run() {
		if (!d_runWhenDisabled && d_parent == null && RobotState.isDisabled()) {
			cancel();
		}
		if (isCanceled()) {
			return false;
		}
		if (!d_initialized) {
			d_initialized = true;
			startTiming();
			_initialize();
			initialize();
		}
		_execute();
		execute();
		return !isFinished();
	}
	
	protected abstract void initialize();
	void _initialize() {}
	
	protected abstract void execute();
	void _execute() {}
	
	protected abstract boolean isFinished();
	
	protected abstract void end();
	void _end() {}
	
	protected abstract void interrupted();
	void _interrupted() {}
	
	private void startTiming() {
		d_startTime = Timer.getFPGATimestamp();
	}
	
	protected synchronized boolean isTimedOut() {
		return d_timeout != -1 && timeSinceInitialized() >= d_timeout;
	}
	
	synchronized Enumeration getRequirements() {
		return d_requirements == null ? emptyEnumeration : d_requirements.getElements();
	}
	
	synchronized void lockChanges() {
		d_locked = true;
	}
	
	synchronized void validate(String message) {
		if (d_locked) {
			throw new IllegalUseOfCommandException(message + " after being started or being added to a command group");
		}
	}
	
	synchronized void setParent(CommandGroup parent) {
		if (this.d_parent != null) {
			throw new IllegalUseOfCommandException("Cannot give command to a command group after already being put in a command group");
		}
		lockChanges();
		this.d_parent = parent;
		if (table != null) {
			//TODO Implement
		}
	}
	
	public synchronized void start() {
		lockChanges();
		if (d_parent != null) {
			throw new IllegalUseOfCommandException("Cannot start a command that is a part of a command group");
		}
		Scheduler.getInstance().add(this);
	}
	
	synchronized void startRunning() {
		d_running = true;
		d_startTime = -1;
		if (table != null) {
			// TODO Implement
		}
	}
	
	public synchronized boolean isRunning() {
		return d_running;
	}
	
	public synchronized void cancel() {
		if (d_parent != null) {
			throw new IllegalUseOfCommandException("Cannot manually cancel a command in a command group");
		}
		_cancel();
	}
	
	synchronized void _cancel() {
		if (isRunning()) {
			d_canceled = true;
		}
	}
	
	public synchronized boolean isCanceled() {
		return d_canceled;
	}
	
	public synchronized boolean isInterruptible() {
		return d_interruptible;
	}
	
	protected synchronized void setInterruptible(boolean interruptible) {
		d_interruptible = interruptible;
	}
	
	public synchronized boolean doesRequire(Subsystem system) {
		return d_requirements != null && d_requirements.contains(system);
	}
	
	public synchronized CommandGroup getGroup() {
		return d_parent;
	}
	
	public void setRunWhenDisabled(boolean run) {
		d_runWhenDisabled = run;
	}
	
	public boolean willRunWhenDisabled() {
		return d_runWhenDisabled;
	}
	
	private static Enumeration emptyEnumeration = new Enumeration() {
		public boolean hasMoreElements() {
			return false;
		}
		
		public Object nextElement() {
			throw new NoSuchElementException();
		}
	};
	
	public String toString() {
		return getName();
	}
	
	public String getSmartDashboardType() {
		return "Command";
	}
	
	private ITable table;
	
	// TODO Implement the rest
}
