package edu.wpi.first.wpilibj.command;

import java.util.Enumeration;
import java.util.Vector;

public class CommandGroup extends Command {

	Vector<Entry> d_commands = new Vector<>();
	Vector<Entry> d_children = new Vector<>();
	int d_currentCommandIndex = -1;
	
	public CommandGroup() {}
	
	public CommandGroup(String name) {
		super(name);
	}
	
	public synchronized final void addSequential(Command command) {
		validate("Cannot add new command to command group");
		if (command == null) {
			throw new IllegalArgumentException("Given null command");
		}
		
		command.setParent(this);
		
		d_commands.addElement(new Entry(command, Entry.IN_SEQUENCE));
		for (Enumeration<Subsystem> e = command.getRequirements(); e.hasMoreElements();) {
			requires(e.nextElement());
		}
	}
	
	public synchronized final void addSequential(Command command, double timeout) {
		validate("Cannot add new command to command group");
		if (command == null) {
			throw new IllegalArgumentException("Given null command");
		}
		if (timeout < 0) {
			throw new IllegalArgumentException("Cannot be given a negative timeout");
		}
		
		command.setParent(this);
		
		d_commands.addElement(new Entry(command, Entry.IN_SEQUENCE, timeout));
		for (Enumeration<Subsystem> e = command.getRequirements(); e.hasMoreElements();) {
			requires(e.nextElement());
		}
	}
	
	public synchronized final void addParallel (Command command) {
		validate("Cannot add new command to command group");
		if (command == null) {
			throw new IllegalArgumentException("Given null command");
		}
		
		command.setParent(this);
		
		d_commands.addElement(new Entry(command, Entry.BRANCH_CHILD));
		for (Enumeration<Subsystem> e = command.getRequirements(); e.hasMoreElements();) {
			requires(e.nextElement());
		}
	}
	
	public synchronized final void addParallel(Command command, double timeout) {
		validate("Cannot add new command to command group");
		if (command == null) {
			throw new IllegalArgumentException("Given null command");
		}
		if (timeout < 0) {
			throw new IllegalArgumentException("Cannot be given a negative timeout");
		}
		
		command.setParent(this);
		
		d_commands.addElement(new Entry(command, Entry.BRANCH_CHILD, timeout));
		for (Enumeration<Subsystem> e = command.getRequirements(); e.hasMoreElements();) {
			requires(e.nextElement());
		}
	}
	
	void _initialize() {
		d_currentCommandIndex = -1;
	}
	
	void _execute() {
		Entry entry = null;
		Command cmd = null;
		boolean firstRun = false;
		
		if (d_currentCommandIndex == -1) {
			firstRun = true;
			d_currentCommandIndex = 0;
		}
		
		while (d_currentCommandIndex < d_commands.size()) {
			if (cmd != null) {
				if (entry.isTimedOut()) {
					cmd._cancel();
				}
				
				if (cmd.run()) {
					break;
				}
				else {
					cmd.removed();
					d_currentCommandIndex++;
					firstRun = true;
					cmd = null;
					continue;
				}
			}
			
			entry = d_commands.elementAt(d_currentCommandIndex);
			cmd = null;
			
			switch (entry.state) {
				case Entry.IN_SEQUENCE:
					cmd = entry.command;
					if (firstRun) {
						cmd.startRunning();
						cancelConflicts(cmd);
					}
					firstRun = false;
					break;
				
				case Entry.BRANCH_PEER:
					d_currentCommandIndex++;
					entry.command.start();
					break;
					
				case Entry.BRANCH_CHILD:
					d_currentCommandIndex++;
					cancelConflicts(entry.command);
					entry.command.startRunning();
					d_children.addElement(entry);
					break;
			}
		}
		
		// Run children
		for (int i = 0; i < d_children.size(); i++) {
			entry = d_children.elementAt(i);
			Command child = entry.command;
			if (entry.isTimedOut()) {
				child._cancel();
			}
			if (!child.run()) {
				child.removed();
				d_children.removeElementAt(i--);
			}
		}
	}
	
	void _end() {
		if (d_currentCommandIndex != -1 && d_currentCommandIndex < d_commands.size()) {
			Command cmd = (d_commands.elementAt(d_currentCommandIndex)).command;
			cmd._cancel();
			cmd.removed();
		}
		
		Enumeration<Entry> children = d_children.elements();
		while (children.hasMoreElements()) {
			Command cmd = (children.nextElement()).command;
			cmd._cancel();
			cmd.removed();
		}
		d_children.removeAllElements();
	}
	
	void _interrupted() {
		_end();
	}
	
	protected boolean isFinished() {
		return d_currentCommandIndex >= d_commands.size() && d_children.isEmpty();
	}
	
	protected void initialize() {}
	protected void execute() {}
	protected void end() {}
	protected void interrupted() {}
	
	public synchronized boolean isInterruptible() {
		if (!super.isCanceled()) {
			return false;
		}
		
		if (d_currentCommandIndex != -1 && d_currentCommandIndex < d_commands.size()) {
			Command cmd = (d_commands.elementAt(d_currentCommandIndex)).command;
			if (!cmd.isInterruptible()) {
				return false;
			}
		}
		
		for (int i = 0; i < d_children.size(); i++) {
			if (!(d_children.elementAt(i)).command.isInterruptible()) {
				return false;
			}
		}
		return true;
	}
	
	private void cancelConflicts(Command command) {
		
	}
	
	private static class Entry {
		private static final int IN_SEQUENCE = 0;
		private static final int BRANCH_PEER = 1;
		private static final int BRANCH_CHILD = 2;
		
		Command command;
		int state;
		double timeout;
		
		Entry(Command command, int state) {
			this.command = command;
			this.state = state;
			this.timeout = -1;
		}
		
		Entry(Command command, int state, double timeout) {
			this.command = command;
			this.state = state;
			this.timeout = timeout;
		}
		
		boolean isTimedOut() {
			if (timeout == -1) {
				return false;
			}
			else {
				double time = command.timeSinceInitialized();
				return time == 0 ? false : time >= timeout;
			}
		}
	}

}
