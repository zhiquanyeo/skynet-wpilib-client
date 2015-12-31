package edu.wpi.first.wpilibj.command;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.tables.ITable;

public abstract class PIDCommand extends Command implements Sendable {
	
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
	
	public PIDCommand(String name, double p, double i, double d) {
		super(name);
		controller = new PIDController(p, i, d, source, output);
	}
	
	public PIDCommand(String name, double p, double i, double d, double period) {
		super(name);
		controller = new PIDController(p, i, d, source, output, period);
	}
	
	public PIDCommand(double p, double i, double d) {
		controller = new PIDController(p, i, d, source, output);
	}
	
	public PIDCommand(double p, double i, double d, double period) {
		controller = new PIDController(p, i, d, source, output, period);
	}
	
	protected PIDController getPIDController() {
		return controller;
	}
	
	void _initialize() {
		controller.enable();
	}
	
	void _end() {
		controller.disable();
	}
	
	void _interrupted() {
		_end();
	}
	
	public void setSetpointRelative(double deltaSetpoint) {
		setSetpoint(getSetpoint() + deltaSetpoint);
	}
	
	protected void setSetpoint(double setpoint) {
		controller.setSetpoint(setpoint);
	}
	
	protected double getSetpoint() {
		return controller.getSetpoint();
	}
	
	protected double getPosition() {
		return returnPIDInput();
	}
	
	protected void setInputRange(double minInput, double maxInput) {
		controller.setInputRange(minInput, maxInput);
	}
	
	protected abstract double returnPIDInput();
	
	protected abstract void usePIDOutput(double output);
	
	public String getSmartDashboardType() {
		return "PIDCommand";
	}
	
	public void initTable(ITable table) {
		controller.initTable(table);
		super.initTable(table);
	}
}
