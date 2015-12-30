package edu.wpi.first.wpilibj;

import java.util.LinkedList;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.util.BoundaryException;

public class PIDController implements PIDInterface, LiveWindowSendable, Controller {
	
	public static final double kDefaultPeriod = 0.05;
	private static int instances = 0;
	private double d_P;	// Factor for proportional control
	private double d_I;	// Factor for integral control
	private double d_D; // Factor for derivative control
	private double d_F;	// Factor for feedForward
	private double d_maximumOutput = 1.0;
	private double d_minimumOutput = -1.0;
	private double d_maximumInput = 0.0;
	private double d_minimumInput = 0.0;
	private boolean d_continuous = false;
	
	private boolean d_enabled = false;
	private double d_prevInput = 0.0;
	private double d_totalError = 0.0;
	private Tolerance d_tolerance;
	
	private int d_bufLength = 0;
	private LinkedList<Double> d_buf;
	private double d_bufTotal = 0.0;
	private double d_setpoint = 0.0;
	private double d_error = 0.0;
	private double d_result = 0.0;
	private double d_period = kDefaultPeriod;
	protected PIDSource d_pidInput;
	protected PIDOutput d_pidOutput;
	java.util.Timer d_controlLoop;
	private boolean d_freed = false;
	private boolean d_usingPercentTolerance;
	
	public interface Tolerance {
		public boolean onTarget();
	}
	
	public class NullTolerance implements Tolerance {
		@Override
		public boolean onTarget() {
			throw new RuntimeException("No tolerance value set when calling onTarget().");
		}
	}
	
	public class PercentageTolerance implements Tolerance {
		double percentage;
		
		public PercentageTolerance(double value) {
			percentage = value;
		}
		
		@Override
		public boolean onTarget() {
			return (Math.abs(getAvgError()) < percentage / 100 * (d_maximumInput - d_minimumInput));
		}
	}
	
	public class AbsoluteTolerance implements Tolerance {
		double value;
		
		public AbsoluteTolerance(double value) {
			this.value = value;
		}
		
		@Override
		public boolean onTarget() {
			return Math.abs(getAvgError()) < value;
		}
	}
	
	private class PIDTask extends TimerTask {
		private PIDController d_controller;
		public PIDTask(PIDController controller) {
			if (controller == null) {
				throw new NullPointerException("Given PIDController was null");
			}
			d_controller = controller;
		}
		
		@Override
		public void run() {
			d_controller.calculate();
		}
	}
	
	public PIDController(double kP, double kI, double kD, double kF, PIDSource source, 
			PIDOutput output, double period) {
		if (source == null) {
			throw new NullPointerException("Null PIDSource was given");
		}
		if (output == null) {
			throw new NullPointerException("Null PIDOutput was given");
		}
		
		d_controlLoop = new java.util.Timer();
		
		d_P = kP;
		d_I = kI;
		d_D = kD;
		d_F = kF;
		
		d_pidInput = source;
		d_pidOutput = output;
		d_period = period;
		
		d_controlLoop.schedule(new PIDTask(this), 0L, (long)(d_period * 1000));
		
		instances++;
		HLUsageReporting.reportPIDController(instances);
		d_tolerance = new NullTolerance();
		d_buf = new LinkedList<Double>();
	}
	
	public PIDController(double kP, double kI, double kD, PIDSource source, PIDOutput output, 
			double period) {
		this(kP, kI, kD, 0.0, source, output, period);
	}
	
	public PIDController(double kP, double kI, double kD, PIDSource source, PIDOutput output) {
		this(kP, kI, kD, source, output, kDefaultPeriod);
	}
	
	public PIDController(double kP, double kI, double kD, double kF, PIDSource source, PIDOutput output) {
		this(kP, kI, kD, kF, source, output, kDefaultPeriod);
	}
	
	public void free() {
		d_controlLoop.cancel();
		synchronized(this) {
			d_freed = true;
			d_pidOutput = null;
			d_pidInput = null;
			d_controlLoop = null;
		}
		if (this.table != null) {
			// TODO Implement
			//table.removeTableListener(listener);
		}
	}
	
	protected void calculate() {
		boolean enabled;
		PIDSource pidInput;
		
		synchronized (this) {
			if (d_pidInput == null) {
				return;
			}
			if (d_pidOutput == null) {
				return;
			}
			enabled = d_enabled;
			pidInput = d_pidInput;
		}
		
		if (enabled) {
			double input;
			double result;
			PIDOutput pidOutput = null;
			synchronized (this) {
				input = pidInput.pidGet();
			}
			synchronized (this) {
				d_error = d_setpoint - input;
				if (d_continuous) {
					if (Math.abs(d_error) > (d_maximumInput - d_minimumInput) / 2) {
						if (d_error > 0) {
							d_error = d_error - d_maximumInput + d_minimumInput;
						}
						else {
							d_error = d_error + d_maximumInput - d_minimumInput;
						}
					}
				}
				
				if (d_pidInput.getPIDSourceType().equals(PIDSourceType.kRate)) {
					if (d_P != 0) {
						double potentialPGain = (d_totalError + d_error) * d_P;
						if (potentialPGain < d_maximumOutput) {
							if (potentialPGain > d_minimumOutput) {
								d_totalError += d_error;
							}
							else {
								d_totalError = d_minimumOutput / d_P;
							}
						}
						else {
							d_totalError = d_maximumOutput / d_P;
						}
						
						d_result = d_P * d_totalError + d_D * d_error + d_setpoint * d_F;
					}
				}
				else {
					if (d_I != 0) {
						double potentialIGain = (d_totalError + d_error) * d_I;
						if (potentialIGain < d_maximumOutput) {
							if (potentialIGain > d_minimumOutput) {
								d_totalError += d_error;
							}
							else {
								d_totalError = d_minimumOutput / d_I;
							}
						}
						else {
							d_totalError = d_maximumOutput / d_I;
						}
						
						d_result = d_P * d_error + d_I * d_totalError + d_D * (d_prevInput - input) + d_setpoint * d_F;
					}
				}
				d_prevInput = input;
				
				if (d_result > d_maximumOutput) {
					d_result = d_maximumOutput;
				}
				else if (d_result < d_minimumOutput) {
					d_result = d_minimumOutput;
				}
				
				pidOutput = d_pidOutput;
				result = d_result;
				
				d_buf.push(d_error);
				d_bufTotal += d_error;
				
				if (d_buf.size() > d_bufLength) {
					d_bufTotal -= d_buf.pop();
				}
			}
			
			pidOutput.pidWrite(result);
		}
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
		return "PIDController";
	}

	@Override
	public void updateTable() {
		// TODO Auto-generated method stub
		
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
	public synchronized void setPID(double p, double i, double d) {
		d_P = p;
		d_I = i;
		d_D = d;
		
		if (table != null) {
			//TODO Implement
		}
	}
	
	public synchronized void setPID(double p, double i, double d, double f) {
		d_P = p;
		d_I = i;
		d_D = d;
		d_F = f;
		
		if (table != null) {
			//TODO Implement
		}
	}

	@Override
	public synchronized double getP() {
		return d_P;
	}

	@Override
	public synchronized double getI() {
		return d_I;
	}

	@Override
	public synchronized double getD() {
		return d_D;
	}
	
	public synchronized double getF() {
		return d_F;
	}
	
	public synchronized double get() {
		return d_result;
	}
	
	public synchronized void setContinuous(boolean continuous) {
		d_continuous = continuous;
	}
	
	public synchronized void setContinuous() {
		this.setContinuous(true);
	}
	
	public synchronized void setInputRange(double minInput, double maxInput) {
		if (minInput > maxInput) {
			throw new BoundaryException("Lower bound is greater than upper bound");
		}
		d_minimumInput = minInput;
		d_maximumInput = maxInput;
		setSetpoint(d_setpoint);
	}
	
	public synchronized void setOutputRange(double minOutput, double maxOutput) {
		if (minOutput > maxOutput) {
			throw new BoundaryException("Lower bound is greater than upper bound");
		}
		d_minimumOutput = minOutput;
		d_maximumOutput = maxOutput;
	}

	@Override
	public synchronized void setSetpoint(double setpoint) {
		if (d_maximumInput > d_minimumInput) {
			if (setpoint > d_maximumInput) {
				d_setpoint = d_maximumInput;
			}
			else if (setpoint < d_minimumInput) {
				d_setpoint = d_minimumInput;
			}
			else {
				d_setpoint = setpoint;
			}
		}
		else {
			d_setpoint = setpoint;
		}
		
		d_buf.clear();
		
		if (table != null) {
			//TODO Implement
		}
	}

	@Override
	public synchronized double getSetpoint() {
		return d_setpoint;
	}

	@Override
	public synchronized double getError() {
		return getSetpoint() - d_pidInput.pidGet();
	}
	
	void setPIDSourceType(PIDSourceType pidSource) {
		d_pidInput.setPIDSourceType(pidSource);
	}
	
	PIDSourceType getPIDSourceType() {
		return d_pidInput.getPIDSourceType();
	}
	
	public synchronized double getAvgError() {
		double avgError = 0;
		if (d_buf.size() != 0) avgError = d_bufTotal / d_buf.size();
		return avgError;
	}

	public synchronized void setTolerance(Tolerance tolerance) {
		d_tolerance = tolerance;
	}
	
	public synchronized void setAbsoluteTolerance(double absValue) {
		d_tolerance = new AbsoluteTolerance(absValue);
	}
	
	public synchronized void setPercentTolerance(double percentage) {
		d_tolerance = new PercentageTolerance(percentage);
	}
	
	public synchronized void setToleranceBuffer(int bufLength) {
		d_bufLength = bufLength;
		while (d_buf.size() > bufLength) {
			d_bufTotal -= d_buf.pop();
		}
	}
	
	public synchronized boolean onTarget() {
		return d_tolerance.onTarget();
	}
	
	@Override
	public synchronized void enable() {
		d_enabled = true;
		
		if (table != null) {
			// TODO implement
		}
	}

	@Override
	public synchronized void disable() {
		d_pidOutput.pidWrite(0);
		d_enabled = false;
		
		if (table != null) {
			// TODO Implement
		}
	}

	@Override
	public boolean isEnabled() {
		return d_enabled;
	}

	@Override
	public synchronized void reset() {
		disable();
		d_prevInput = 0;
		d_totalError = 0;
		d_result = 0;
	}
	
	private ITable table;
	
	// TODO Implement the rest of the NetworkTable stuff
}
