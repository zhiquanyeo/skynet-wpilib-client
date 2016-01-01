package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tInstances;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.communication.UsageReporting;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

public class IterativeRobot extends RobotBase {
	private boolean d_disabledInitialized;
	private boolean d_autonomousInitialized;
	private boolean d_teleopInitialized;
	private boolean d_testInitialized;
	
	public IterativeRobot() {
		d_disabledInitialized = false;
		d_autonomousInitialized = false;
		d_teleopInitialized = false;
		d_testInitialized = false;
	}
	
	protected void prestart() {
		
	}
	
	@Override
	public void startCompetition() {
		UsageReporting.report(tResourceType.kResourceType_Framework, tInstances.kFramework_Iterative);

        robotInit();
        
     // We call this now (not in prestart like default) so that the robot
        // won't enable until the initialization has finished. This is useful
        // because otherwise it's sometimes possible to enable the robot
        // before the code is ready. 
        FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramStarting();

        // loop forever, calling the appropriate mode-dependent function
        LiveWindow.setEnabled(false);
        while (true) {
            // Call the appropriate function depending upon the current robot mode
            if (isDisabled()) {
                // call DisabledInit() if we are now just entering disabled mode from
                // either a different mode or from power-on
                if (!d_disabledInitialized) {
                    LiveWindow.setEnabled(false);
                    disabledInit();
                    d_disabledInitialized = true;
                    // reset the initialization flags for the other modes
                    d_autonomousInitialized = false;
                    d_teleopInitialized = false;
                    d_testInitialized = false;
                }
                if (nextPeriodReady()) {
                	FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramDisabled();
                    disabledPeriodic();
                }
            } else if (isTest()) {
                // call TestInit() if we are now just entering test mode from either
                // a different mode or from power-on
                if (!d_testInitialized) {
                    LiveWindow.setEnabled(true);
                    testInit();
                    d_testInitialized = true;
                    d_autonomousInitialized = false;
                    d_teleopInitialized = false;
                    d_disabledInitialized = false;
                }
                if (nextPeriodReady()) {
                	FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramTest();
                    testPeriodic();
                }
            } else if (isAutonomous()) {
                // call Autonomous_Init() if this is the first time
                // we've entered autonomous_mode
                if (!d_autonomousInitialized) {
                    LiveWindow.setEnabled(false);
                    // KBS NOTE: old code reset all PWMs and relays to "safe values"
                    // whenever entering autonomous mode, before calling
                    // "Autonomous_Init()"
                    autonomousInit();
                    d_autonomousInitialized = true;
                    d_testInitialized = false;
                    d_teleopInitialized = false;
                    d_disabledInitialized = false;
                }
                if (nextPeriodReady()) {
                    FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramAutonomous();
                    autonomousPeriodic();
                }
            } else {
                // call Teleop_Init() if this is the first time
                // we've entered teleop_mode
                if (!d_teleopInitialized) {
                    LiveWindow.setEnabled(false);
                    teleopInit();
                    d_teleopInitialized = true;
                    d_testInitialized = false;
                    d_autonomousInitialized = false;
                    d_disabledInitialized = false;
                }
                if (nextPeriodReady()) {
                    FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationObserveUserProgramTeleop();
                    teleopPeriodic();
                }
            }
            d_ds.waitForData();
        }
	}

	/**
     * Determine if the appropriate next periodic function should be called.
     * Call the periodic functions whenever a packet is received from the Driver Station, or about every 20ms.
     */
    private boolean nextPeriodReady() {
        return d_ds.isNewControlData();
    }

    /* ----------- Overridable initialization code -----------------*/

    /**
     * Robot-wide initialization code should go here.
     *
     * Users should override this method for default Robot-wide initialization which will
     * be called when the robot is first powered on.  It will be called exactly 1 time.
     */
    public void robotInit() {
        System.out.println("Default IterativeRobot.robotInit() method... Overload me!");
    }

    /**
     * Initialization code for disabled mode should go here.
     *
     * Users should override this method for initialization code which will be called each time
     * the robot enters disabled mode.
     */
    public void disabledInit() {
        System.out.println("Default IterativeRobot.disabledInit() method... Overload me!");
    }

    /**
     * Initialization code for autonomous mode should go here.
     *
     * Users should override this method for initialization code which will be called each time
     * the robot enters autonomous mode.
     */
    public void autonomousInit() {
        System.out.println("Default IterativeRobot.autonomousInit() method... Overload me!");
    }

    /**
     * Initialization code for teleop mode should go here.
     *
     * Users should override this method for initialization code which will be called each time
     * the robot enters teleop mode.
     */
    public void teleopInit() {
        System.out.println("Default IterativeRobot.teleopInit() method... Overload me!");
    }

    /**
     * Initialization code for test mode should go here.
     *
     * Users should override this method for initialization code which will be called each time
     * the robot enters test mode.
     */
    public void testInit() {
        System.out.println("Default IterativeRobot.testInit() method... Overload me!");
    }

    /* ----------- Overridable periodic code -----------------*/

    private boolean dpFirstRun = true;
    /**
     * Periodic code for disabled mode should go here.
     *
     * Users should override this method for code which will be called periodically at a regular
     * rate while the robot is in disabled mode.
     */
    public void disabledPeriodic() {
        if (dpFirstRun) {
            System.out.println("Default IterativeRobot.disabledPeriodic() method... Overload me!");
            dpFirstRun = false;
        }
        Timer.delay(0.001);
    }

    private boolean apFirstRun = true;

    /**
     * Periodic code for autonomous mode should go here.
     *
     * Users should override this method for code which will be called periodically at a regular
     * rate while the robot is in autonomous mode.
     */
    public void autonomousPeriodic() {
        if (apFirstRun) {
            System.out.println("Default IterativeRobot.autonomousPeriodic() method... Overload me!");
            apFirstRun = false;
        }
        Timer.delay(0.001);
    }

    private boolean tpFirstRun = true;

    /**
     * Periodic code for teleop mode should go here.
     *
     * Users should override this method for code which will be called periodically at a regular
     * rate while the robot is in teleop mode.
     */
    public void teleopPeriodic() {
        if (tpFirstRun) {
            System.out.println("Default IterativeRobot.teleopPeriodic() method... Overload me!");
            tpFirstRun = false;
        }
        Timer.delay(0.001);
    }

    private boolean tmpFirstRun = true;

    /**
     * Periodic code for test mode should go here
     *
     * Users should override this method for code which will be called periodically at a regular rate
     * while the robot is in test mode.
     */
    public void testPeriodic() {
        if (tmpFirstRun) {
            System.out.println("Default IterativeRobot.testPeriodic() method... Overload me!");
            tmpFirstRun = false;
        }
    }
}
