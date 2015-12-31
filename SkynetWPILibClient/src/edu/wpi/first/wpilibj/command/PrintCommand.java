package edu.wpi.first.wpilibj.command;

public class PrintCommand extends Command {
	private String message;
	
	public PrintCommand(String message) {
		super("Print(\"" + message + "\")");
		this.message = message;
	}

	@Override
	protected void initialize() {
		System.out.println(message);
	}

	@Override
	protected void execute() {
		
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
		
	}

	@Override
	protected void interrupted() {
		
	}
	
}
