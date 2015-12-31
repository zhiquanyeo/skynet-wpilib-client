package edu.wpi.first.wpilibj.command;

public class StartCommand extends Command {

	private Command d_commandToFork;
	
	public StartCommand(Command commandToStart) {
		super("Start(" + commandToStart + ")");
		d_commandToFork = commandToStart;
	}
	
	@Override
	protected void initialize() {
		d_commandToFork.start();
	}

	@Override
	protected void execute() {
		// TODO Auto-generated method stub

	}

	@Override
	protected boolean isFinished() {
		// TODO Auto-generated method stub
		return true;
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
