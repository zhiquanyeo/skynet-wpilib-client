package edu.wpi.first.wpilibj;

import com.zhiquanyeo.skynet.network.ISkynetMessageSubscriber;
import com.zhiquanyeo.skynet.network.SkynetProxy;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.util.AllocationException;
import edu.wpi.first.wpilibj.util.CheckedAllocationException;

public class AnalogInput extends SensorBase implements PIDSource, LiveWindowSendable, ISkynetMessageSubscriber {
	
	private static Resource channels = new Resource(kAnalogInputChannels);
	private int d_channel;
	private double d_voltage = 0.0;
	
	public AnalogInput(final int channel) {
		d_channel = channel;
		checkAnalogInputChannel(d_channel);
		
		try {
			channels.allocate(d_channel);
		}
		catch (CheckedAllocationException e) {
			throw new AllocationException("Analog input channel " + d_channel + "is already allocated");
		}
		
		SkynetProxy.subscribeAnalogInput(d_channel, this);
	}
	
	public void free() {
		channels.free(d_channel);
		SkynetProxy.unsubscribeAnalogInput(d_channel);
		d_channel = 0;
	}
	
	protected int voltageToValue(double voltage) {
		// 12 bit, 0 - 5V (0-4095)
		return (int)((voltage / 5.0) * 4095);
	}
	
	public int getValue() {
		return voltageToValue(d_voltage);
	}
	
	public int getAverageValue() {
		return getValue();
	}
	
	public double getVoltage() {
		return d_voltage;
	}
	
	public double getAverageVoltage() {
		return getVoltage();
	}
	
	public int getChannel() {
		return d_channel;
	}
	
	@Override
	public double pidGet() {
		return getAverageVoltage();
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
		return "Analog Input";
	}

	@Override
	public void updateTable() {
		if (d_table != null) {
			d_table.putNumber("Value", getAverageVoltage());
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

	@Override
	public void onMessageReceived(String message) {
		double temp = Double.parseDouble(message);
		if (!Double.isNaN(temp)) {
			d_voltage = temp;
		}
	}

}
