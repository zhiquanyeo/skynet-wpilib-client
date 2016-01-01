package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;

/**
 * Class implements the PWM "generation" by sending a message to the endpoint
 *
 * The values supplied as arguments for PWM outputs range from -1.0 to 1.0. 
 * Changes are sent via Skynet protocol to the endpoint
 */
public class PWM extends SensorBase implements LiveWindowSendable {
	
	private int d_channel;
	private double d_rawVal = 0.0;
	private int d_rawValInt = 0;
	
	private void initPWM(final int channel) {
		checkPWMChannel(channel);
		d_channel = channel;
		
		UsageReporting.report(tResourceType.kResourceType_PWM, channel);
	}
	
	public PWM(final int channel) {
		initPWM(channel);
	}
	
	public void free() {
		
	}
	
	public int getChannel() {
		return d_channel;
	}
	
	/**
	 * Set the PWM value based on a position.
	 *
	 * This is intended to be used by servos.
	 *
	 * @param pos The position to set the servo between 0.0 and 1.0.
	 */
	public void setPosition(double pos) {
		if (pos < 0.0) {
			pos = 0.0;
		} else if (pos > 1.0) {
			pos = 1.0;
		}

		int rawValue = (int)(255 * (pos / 1.0));
		setRaw(rawValue);
	}
	
	/**
	 * Get the PWM value in terms of a position.
	 *
	 * This is intended to be used by servos.
	 *
	 * @pre SetMaxPositivePwm() called.
	 * @pre SetMinNegativePwm() called.
	 *
	 * @return The position the servo is set to between 0.0 and 1.0.
	 */
	public double getPosition() {
		int value = getRaw();
		return (value / 255.0);
	}

	/**
	 * Set the PWM value based on a speed.
	 *
	 * This is intended to be used by speed controllers.
	 *
	 * @pre SetMaxPositivePwm() called.
	 * @pre SetMinPositivePwm() called.
	 * @pre SetCenterPwm() called.
	 * @pre SetMaxNegativePwm() called.
	 * @pre SetMinNegativePwm() called.
	 *
	 * @param speed The speed to set the speed controller between -1.0 and 1.0.
	 */
	final void setSpeed(double speed) {
		// clamp speed to be in the range 1.0 >= speed >= -1.0
		if (speed < -1.0) {
			speed = -1.0;
		} else if (speed > 1.0) {
			speed = 1.0;
		}
		
		// Raw Value is 0 - 255
		int rawValue = (int)(((speed + 1.0) / 2.0) * 255);
		setRaw(rawValue);
	}

	/**
	 * Get the PWM value in terms of speed.
	 *
	 * This is intended to be used by speed controllers.
	 *
	 * @pre SetMaxPositivePwm() called.
	 * @pre SetMinPositivePwm() called.
	 * @pre SetMaxNegativePwm() called.
	 * @pre SetMinNegativePwm() called.
	 *
	 * @return The most recently set speed between -1.0 and 1.0.
	 */
	public double getSpeed() {
		int value = getRaw();
		
		double speed = (((double)(value / 255.0)) * 2.0) - 1.0;
		return speed;
	}

	/**
	 * Set the PWM value directly to the hardware.
	 *
	 * Write a raw value to a PWM channel.
	 *
	 * @param value Raw PWM value.  Range 0 - 255.
	 */
	public void setRaw(int value) {
		d_rawValInt = value;
	}

	/**
	 * Get the PWM value directly from the hardware.
	 *
	 * Read a raw value from a PWM channel.
	 *
	 * @return Raw PWM control value.  Range: 0 - 255.
	 */
	public int getRaw() {
		return d_rawValInt;
	}
	
	/*
	 * Live Window code, only does anything if live window is activated.
	 */
	public String getSmartDashboardType() {
		return "Speed Controller";
	}
	private ITable d_table;
	private ITableListener d_tableListener;

	/**
	 * {@inheritDoc}
	 */
	public void initTable(ITable subtable) {
		d_table = subtable;
		updateTable();
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateTable() {
		if (d_table != null) {
			d_table.putNumber("Value", getSpeed());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ITable getTable() {
		return d_table;
	}

	/**
	 * {@inheritDoc}
	 */
	public void startLiveWindowMode() {
		setSpeed(0); // Stop for safety
		d_tableListener = new ITableListener() {
			public void valueChanged(ITable itable, String key, Object value, boolean bln) {
				setSpeed(((Double) value).doubleValue());
			}
		};
		d_table.addTableListener("Value", d_tableListener, true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void stopLiveWindowMode() {
		setSpeed(0); // Stop for safety
		// TODO: Broken, should only remove the listener from "Value" only.
		d_table.removeTableListener(d_tableListener);
	}
}
