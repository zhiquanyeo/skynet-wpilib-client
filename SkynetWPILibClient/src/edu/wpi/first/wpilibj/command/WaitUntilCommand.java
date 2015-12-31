package edu.wpi.first.wpilibj.command;

import edu.wpi.first.wpilibj.Timer;

public class WaitUntilCommand extends Command {
	
	private double d_time;
	
	public WaitUntilCommand(double time) {
		super("WaitUntil(" + time + ")");
		d_time = time;
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
		return Timer.getMatchTime() >= d_time;
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
