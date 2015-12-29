package edu.wpi.first.wpilibj.util;

public class BaseSystemNotInitializedException extends RuntimeException {
	public BaseSystemNotInitializedException(String message) {
		super(message);
	}
	
	/**
	 * Create a new BaseSystemNotInitializedException using the offending class
	 * that was not set and the class that was affected.
	 *$
	 * @param offender The class or interface that was not properly initialized.
	 * @param affected The class that was was affected by this missing
	 *        initialization.
	 */
	public BaseSystemNotInitializedException(Class<?> offender, Class<?> affected) {
		super("The " + offender.getSimpleName() + " for the " + affected.getSimpleName()
			+ " was never set.");
	}
}
