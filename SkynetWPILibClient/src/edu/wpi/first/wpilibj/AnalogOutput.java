package edu.wpi.first.wpilibj;

import com.zhiquanyeo.skynet.network.SkynetProxy;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.util.AllocationException;
import edu.wpi.first.wpilibj.util.CheckedAllocationException;

public class AnalogOutput extends SensorBase implements LiveWindowSendable {
	private static Resource channels = new Resource(kAnalogOutputChannels);
	private int d_channel;
	
	private double d_voltage = 0.0;
	
	public AnalogOutput(final int channel) {
		d_channel = channel;
		checkAnalogOutputChannel(d_channel);
		
		try {
			channels.allocate(channel);
		}
		catch (CheckedAllocationException e) {
			throw new AllocationException("Analog output channel " + d_channel + " is already allocated");
		}
		
		LiveWindow.addSensor("AnalogOutput", channel, this);
		UsageReporting.report(tResourceType.kResourceType_AnalogChannel, channel, 1);
	}
	
	public void free() {
		channels.free(d_channel);
		d_channel = 0;
	}
	
	public void setVoltage(double voltage) {
		d_voltage = voltage;
		SkynetProxy.publishAnalogValue(d_channel, d_voltage);
	}
	
	public double getVoltage() {
		return d_voltage;
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
		return "Analog Output";
	}

	@Override
	public void updateTable() {
		if (d_table != null) {
			d_table.putNumber("Value", getVoltage());
		}
	}

	@Override
	public void startLiveWindowMode() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopLiveWindowMode() {
		// TODO Auto-generated method stub
		
	}

}
