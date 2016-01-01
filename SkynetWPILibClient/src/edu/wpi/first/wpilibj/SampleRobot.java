package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tInstances;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;

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
		System.out.println("Default autonomous() method running, consider providing your own");
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
		UsageReporting.report(tResourceType.kResourceType_Framework, tInstances.kFramework_Sample);
		
		robotInit();
		
		// Tell the DS that the robot is ready to be enabled
		FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramStarting();
		
		robotMain();
		if (!m_robotMainOverridden) {
			// First and one-time init
			//LiveWindow.setEnabled(false);
			
			while (true) {
				if (isDisabled()) {
					d_ds.InDisabled(true);
					disabled();
					d_ds.InDisabled(false);
					while (isDisabled()) {
						Timer.delay(0.01);
					}
				}
				else if (isAutonomous()) {
					d_ds.InAutonomous(true);
					autonomous();
					d_ds.InAutonomous(false);
					while (isAutonomous() && !isDisabled()) {
						Timer.delay(0.01);
					}
				}
				else if (isTest()) {
					//LiveWindow.setEnabled(true);
					d_ds.InTest(true);
					test();
					d_ds.InTest(false);
					while (isTest() && isEnabled()) {
						Timer.delay(0.01);
					}
					//LiveWindow.setEnabled(false);
				}
				else {
					d_ds.InOperatorControl(true);
					operatorControl();
					d_ds.InOperatorControl(false);
					while (isOperatorControl() && !isDisabled()) {
						Timer.delay(0.01);
					}
				}
			}
		}
	}

}
