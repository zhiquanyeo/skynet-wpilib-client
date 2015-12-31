package edu.wpi.first.wpilibj.buttons;

import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class NetworkButton extends Button {
	
	NetworkTable table;
	String field;
	
	public NetworkButton(String table, String field) {
		this(NetworkTable.getTable(table), field);
	}
	
	public NetworkButton(NetworkTable table, String field) {
		this.table = table;
		this.field = field;
	}
	
	@Override
	public boolean get() {
		return table.isConnected() && table.getBoolean(field, false);
	}

}
