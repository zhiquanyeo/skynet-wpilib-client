package edu.wpi.first.wpilibj;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Manifest;
import java.util.logging.Logger;

import com.zhiquanyeo.skynet.defaultrobot.DefaultRobot;
import com.zhiquanyeo.skynet.network.SkynetConnection;
import com.zhiquanyeo.skynet.network.SkynetProxy;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import edu.wpi.first.wpilibj.internal.SkynetTimer;

public class RobotBaseRunner implements Runnable {
	private final static Logger LOGGER = Logger.getLogger(RobotBaseRunner.class.getName());
	
	private SkynetConnection d_connection;
	
	public RobotBaseRunner(SkynetConnection conn) {
		d_connection = conn;
	}
	
	public static void initializeHardwareConfiguration() {
		FRCNetworkCommunicationsLibrary.FRCNetworkCommunicationReserve();
		
		Timer.SetImplementation(new SkynetTimer());
		//HLUsageReporting.SetImplementation(new HardwareHLUsageReporting());
		RobotState.SetImplementation(DriverStation.getInstance());
	}
	
	@Override
	public void run() {
		//This takes the place of 'main' in RobotBase
		LOGGER.info("=== RobotBaseRunner START ===");
		LOGGER.info("Initializing Hardware");
		
		initializeHardwareConfiguration();
		
		// Initialize the skynet connection
		// TODO Potentially make this static?
		SkynetProxy.setSkynetConnection(d_connection);
		
		boolean errorOnExit = false;
		
		String robotName = "";
		Enumeration<URL> resources = null;
		try {
			resources = RobotBase.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("resources = |" + resources + "|");
		
		while (resources != null && resources.hasMoreElements()) {
			try {
				Manifest manifest = new Manifest(resources.nextElement().openStream());
				if (manifest.getMainAttributes().getValue("Robot-Class") != null) {
					robotName = manifest.getMainAttributes().getValue("Robot-Class");
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		RobotBase robot;
		try {
			robot = (RobotBase) Class.forName(robotName).newInstance();
		}
		catch (InstantiationException|IllegalAccessException|ClassNotFoundException e) {
			System.err.println("Unable to load Robot from manifest.");
			System.err.println("It is possible that there is no manifest file. Loading default robot");
			robot = new DefaultRobot();
		}
		
		try {
			robot.startCompetition();
		}
		catch (Throwable t) {
			t.printStackTrace();
			errorOnExit = true;
		}
		finally {
			// startCompetition never returns unless something bad happened
			System.err.println("WARNING: Robots don't quit");
			if (errorOnExit) {
				System.err.println("---> The startCompeition(); method (or methods called by it) should have handled the exception above.");
			}
			else {
				System.err.println("---> Unexpected return from startCompetition(); method.");
			}
		}
	}

}
