package edu.wpi.first.wpilibj;

public class SampleRobot extends RobotBase {
	
	private boolean m_robotMainOverridden;
	
	public SampleRobot() {
		super();
		m_robotMainOverridden = true;
	}
	
	/**
	 * Robot-wide initialization code should go here.
	 * 
	 * Users should override this method for default Robot-wide initialization which will
	 * be called when the robot is first powered on.
	 * 
	 * Called exactly ONE time when the competition starts
	 */
	protected void robotInit() {
		System.out.println("Default robotInit() method running, consider providing your own");
	}
	
	protected void disabled() {
		System.out.println("Default disabled() method running, consider providing your own");
	}
	
	protected void autonomous() {
		System.out.println("Default disabled() method running, consider providing your own");
	}
	
	protected void operatorControl() {
		System.out.println("Default operatorControl() method running, consider providing your own");
	}
	
	protected void test() {
		System.out.println("Default test() method running, consider providing your own");
	}
	
	/**
	 * Robot main program for free-form programs.
	 * 
	 * This should be overridden by user subclasses if the intent is not to use the autonomous() and
	 * operatorControl() methods. In that case, the program is responsible for sensing when to run
	 * the autonomous and operator control functions in their program.
	 * 
	 * This method will be called immediately after the constructor is called. If it has not been
	 * overridden by a user subclass (i.e. the default version runs), then the robotInit(), disabled(),
	 * autonomous() and operatorControl() methods will be called.
	 */
	public void robotMain() {
		m_robotMainOverridden = false;
	}
	
	@Override
	public void startCompetition() {
		// TODO Implement
	}

}
