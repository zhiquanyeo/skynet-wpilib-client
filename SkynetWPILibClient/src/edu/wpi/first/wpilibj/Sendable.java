package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.tables.ITable;

public interface Sendable {
	public void initTable(ITable subtable);
	public ITable getTable();
	public String getSmartDashboardType();
}
