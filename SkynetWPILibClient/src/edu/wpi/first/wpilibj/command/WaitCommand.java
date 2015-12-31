package edu.wpi.first.wpilibj.command;

public class WaitCommand extends Command {
	
	public WaitCommand(double timeout) {
		this("(Wait(" + timeout + ")", timeout);
	}
	
	public WaitCommand(String name, double timeout) {
		super(name, timeout);
	}
	
	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void execute() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean isFinished() {
		return isTimedOut();
	}

	@Override
	protected void end() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void interrupted() {
		// TODO Auto-generated method stub
		
	}

}
