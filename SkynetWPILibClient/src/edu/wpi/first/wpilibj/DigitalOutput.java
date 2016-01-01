package edu.wpi.first.wpilibj;

import com.zhiquanyeo.skynet.network.SkynetProxy;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

public class DigitalOutput extends DigitalSource implements LiveWindowSendable {
	
	public DigitalOutput(int channel) {
		initDigitalChannel(channel, false);
		UsageReporting.report(tResourceType.kResourceType_DigitalOutput, channel);
	}
	
	public void free() {
		super.free();
	}
	
	public void set(boolean value) {
		// Nothing to see here, be on your way
		SkynetProxy.publishDigitalValue(d_channel, value);
	}
	
	public void pulse(final int channel, final float pulseLength) {
		// TODO Implement
	}
	
	public boolean isPulsing() {
		return false;
	}
	
	
	public int getChannel() {
		return d_channel;
	}
	
	private ITable d_table;
	private ITableListener d_tableListener;
	
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
		return "Digital Output";
	}

	@Override
	public void updateTable() {
	}

	@Override
	public void startLiveWindowMode() {
		d_tableListener = new ITableListener() {

			@Override
			public void valueChanged(ITable itable, String key, Object value, boolean bln) {
				// TODO Auto-generated method stub
				set(((Boolean) value).booleanValue());
			}
			
		};
		
		d_table.addTableListener("Value", d_tableListener, true);
	}

	@Override
	public void stopLiveWindowMode() {
		d_table.removeTableListener(d_tableListener);
	}

}
