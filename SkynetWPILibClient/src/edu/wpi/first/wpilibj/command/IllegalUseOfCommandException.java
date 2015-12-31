package edu.wpi.first.wpilibj.command;

@SuppressWarnings("serial")
public class IllegalUseOfCommandException extends RuntimeException {
	public IllegalUseOfCommandException() {}
	
	public IllegalUseOfCommandException(String message) {
		super(message);
	}
}
