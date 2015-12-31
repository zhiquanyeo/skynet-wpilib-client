package edu.wpi.first.wpilibj.command;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.tables.ITable;

public abstract class PIDSubsystem extends Subsystem implements Sendable {
	private PIDController controller;
	private PIDOutput output = new PIDOutput() {
		public void pidWrite(double output) {
			usePIDOutput(output);
		}
	};
	
	private PIDSource source = new PIDSource() {
		public double pidGet() {
			return returnPIDInput();
		}
	};
	
	public PIDSubsystem(String name, double p, double i, double d) {
		super(name);
		controller = new PIDController(p, i, d, source, output);
	}
	
	public PIDSubsystem(String name, double p, double i, double d, double f) {
		super(name);
		controller = new PIDController(p, i, d, f, source, output);
	}
	
	public PIDSubsystem(String name, double p, double i, double d, double f, double period) {
		super(name);
		controller = new PIDController(p, i, d, f, source, output, period);
	}
	
	public PIDSubsystem(double p, double i, double d) {
		controller = new PIDController(p, i, d, source, output);
	}
	
	public PIDSubsystem(double p, double i, double d, double period, double f) {
		controller = new PIDController(p, i, d, f, source, output, period);
	}
	
	public PIDSubsystem(double p, double i, double d, double period) {
		controller = new PIDController(p, i, d, source, output, period);
	}
	
	public PIDController getPIDController() {
		return controller;
	}
	
	public void setSetpointRelative(double deltaSetpoint) {
		setSetpoint(getPosition() + deltaSetpoint);
	}
	
	public void setSetpoint(double setpoint) {
		controller.setSetpoint(setpoint);
	}
	
	public double getSetpoint() {
		return controller.getSetpoint();
	}
	
	public double getPosition() {
		return returnPIDInput();
	}
	
	public void setInputRange(double minimumInput, double maximumInput) {
		controller.setInputRange(minimumInput, maximumInput);
	}
	
	public void setOutputRange(double minimumOutput, double maximumOutput) {
		controller.setOutputRange(minimumOutput, maximumOutput);
	}
	
	public void setAbsoluteTolerance(double t) {
		controller.setAbsoluteTolerance(t);
	}
	
	public void setPercentTolerance(double p) {
		controller.setPercentTolerance(p);
	}
	
	public boolean onTarget() {
		return controller.onTarget();
	}
	
	protected abstract double returnPIDInput();
	
	protected abstract void usePIDOutput(double output);
	
	public void enable() {
		controller.enable();
	}
	
	public void disable() {
		controller.disable();
	}
	
	public String getSmartDashboardType() {
		return "PIDSubsystem";
	}
	
	public void initTable(ITable table) {
		controller.initTable(table);
		super.initTable(table);
	}
}
