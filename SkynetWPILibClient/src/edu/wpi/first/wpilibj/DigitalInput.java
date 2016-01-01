package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.tables.ITable;

public class DigitalInput extends DigitalSource implements LiveWindowSendable {
	
	public DigitalInput(int channel) {
		initDigitalChannel(channel, true);
		LiveWindow.addSensor("DigitalInput", channel, this);
		UsageReporting.report(tResourceType.kResourceType_DigitalInput, channel);
	}
	
	public boolean get() {
		return getValue();
	}
	
	public int getChannel() {
		return d_channel;
	}
	
	private ITable d_table;
	
	@Override
	public void initTable(ITable subtable) {
		d_table = subtable;
		updateTable();
	}

	@Override
	public ITable getTable() {
		return d_table;
	}

	@Override
	public String getSmartDashboardType() {
		return "Digital Input";
	}

	@Override
	public void updateTable() {
		if (d_table != null) {
			d_table.putBoolean("Value", get());
		}
	}

	@Override
	public void startLiveWindowMode() {
	}

	@Override
	public void stopLiveWindowMode() {
	}

}
