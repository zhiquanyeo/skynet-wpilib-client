package edu.wpi.first.wpilibj.command;

import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.tables.ITable;

public class Scheduler implements NamedSendable {

	private static Scheduler instance;
	
	public synchronized static Scheduler getInstance() {
		return instance == null ? instance = new Scheduler() : instance;
	}
	
	@Override
	public void initTable(ITable subtable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ITable getTable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSmartDashboardType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
