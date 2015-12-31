package edu.wpi.first.wpilibj.command;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import edu.wpi.first.wpilibj.HLUsageReporting;
import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.buttons.Trigger.ButtonScheduler;
import edu.wpi.first.wpilibj.networktables2.type.NumberArray;
import edu.wpi.first.wpilibj.networktables2.type.StringArray;
import edu.wpi.first.wpilibj.tables.ITable;

public class Scheduler implements NamedSendable {

	private static Scheduler instance;
	
	public synchronized static Scheduler getInstance() {
		return instance == null ? instance = new Scheduler() : instance;
	}
	
	private Hashtable<Command, LinkedListElement> commandTable = new Hashtable<>();
	private Set<Subsystem> subsystems = new Set<>();
	private LinkedListElement firstCommand;
	private LinkedListElement lastCommand;
	
	private boolean adding = false;
	private boolean disabled = false;
	
	private Vector<Command> additions = new Vector<>();
	private ITable d_table;
	
	private Vector<ButtonScheduler> buttons;
	private boolean d_runningCommandsChanged;
	
	private Scheduler() {
		HLUsageReporting.reportScheduler();
	}
	
	public void add(Command command) {
		if (command != null) {
			additions.addElement(command);
		}
	}
	
	public void addButton(ButtonScheduler button) {
		if (buttons == null) {
			buttons = new Vector<>();
		}
		buttons.addElement(button);
	}
	
	public void _add(Command command) {
		if (command == null) {
			return;
		}
		
		if (adding) {
			System.out.println("WARNING: Cannot start command grom cancel method. Ignoring");
			return;
		}
		
		if (!commandTable.containsKey(command)) {
			Enumeration<Subsystem> requirements = command.getRequirements();
			while (requirements.hasMoreElements()) {
				Subsystem lock = requirements.nextElement();
				if (lock.getCurrentCommand() != null && !lock.getCurrentCommand().isInterruptible()) {
					return;
				}
			}
			
			// Give it the requirements
			adding = true;
			requirements = command.getRequirements();
			while (requirements.hasMoreElements()) {
				Subsystem lock = (Subsystem)requirements.nextElement();
				if (lock.getCurrentCommand() != null) {
					lock.getCurrentCommand().cancel();
					remove(lock.getCurrentCommand());
				}
				lock.setCurrentCommand(command);
			}
			adding = false;
			
			// Add to the list
			LinkedListElement element = new LinkedListElement();
			element.setData(command);
			if (firstCommand == null) {
				firstCommand = lastCommand = element;
			}
			else {
				lastCommand.add(element);
				lastCommand = element;
			}
			
			commandTable.put(command, element);
			
			d_runningCommandsChanged = true;
			
			command.startRunning();
		}
	}
	
	public void run() {
		d_runningCommandsChanged = false;
		
		if (disabled) {
			return;
		}
		
		if (buttons != null) {
			for (int i = buttons.size() - 1 ; i >= 0; i--) {
				(buttons.elementAt(i)).execute();
			}
		}
		
		LinkedListElement e = firstCommand;
		while (e != null) {
			Command c = e.getData();
			e = e.getNext();
			if (!c.run()) {
				remove(c);
				d_runningCommandsChanged = true;
			}
		}
		
		for (int i = 0; i < additions.size(); i++) {
			_add((Command) additions.elementAt(i));
		}
		additions.removeAllElements();
		
		Enumeration<Subsystem> locks = subsystems.getElements();
		while (locks.hasMoreElements()) {
			Subsystem lock = locks.nextElement();
			if (lock.getCurrentCommand() == null) {
				_add(lock.getDefaultCommand());
			}
			lock.confirmCommand();
		}
		
		updateTable();
	}
	
	void registerSubsystem(Subsystem system) {
		if (system != null) {
			subsystems.add(system);
		}
	}
	
	void remove(Command command) {
		if (command == null || !commandTable.containsKey(command)) {
			return;
		}
		
		LinkedListElement e = commandTable.get(command);
		commandTable.remove(command);
		
		if (e.equals(lastCommand)) {
			lastCommand = e.getPrevious();
		}
		if (e.equals(firstCommand)) {
			firstCommand = e.getNext();
		}
		e.remove();
		
		Enumeration<Subsystem> requirements = command.getRequirements();
		while (requirements.hasMoreElements()) {
			requirements.nextElement().setCurrentCommand(null);
		}
		
		command.removed();
	}
	
	public void removeAll() {
		while (firstCommand != null) {
			remove(firstCommand.getData());
		}
	}
	
	public void disable() {
		disabled = true;
	}
	
	public void enable() {
		disabled = false;
	}
	
	private StringArray commands;
	private NumberArray ids, toCancel;
	
	@Override
	public void initTable(ITable subtable) {
		d_table = subtable;
		commands = new StringArray();
		ids = new NumberArray();
		toCancel = new NumberArray();
		
		d_table.putValue("Names", commands);
		d_table.putValue("Ids", ids);
		d_table.putValue("Cancel", toCancel);
	}

	@Override
	public ITable getTable() {
		return d_table;
	}

	@Override
	public String getSmartDashboardType() {
		return "Scheduler";
	}

	@Override
	public String getName() {
		return "Scheduler";
	}
	
	public String getType() {
		return "Scheduler";
	}
	
	private void updateTable() {
		if (d_table != null) {
			d_table.retrieveValue("Cancel", toCancel);
			if (toCancel.size() > 0) {
				for (LinkedListElement e = firstCommand; e != null; e = e.getNext()) {
					for (int i = 0; i < toCancel.size(); i++) {
						if (e.getData().hashCode() == toCancel.get(i)) {
							e.getData().cancel();
						}
					}
				}
				toCancel.setSize(0);
				d_table.putValue("Cancel", toCancel);
			}
			
			if (d_runningCommandsChanged) {
				commands.setSize(0);
				ids.setSize(0);
				for (LinkedListElement e = firstCommand; e != null; e = e.getNext()) {
					commands.add(e.getData().getName());
					ids.add(e.getData().hashCode());
				}
				d_table.putValue("Names", commands);
				d_table.putValue("Ids", ids);
			}
		}
	}
}
