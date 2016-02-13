package edu.wpi.first.wpilibj.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Semaphore;

import com.zhiquanyeo.skynet.network.SkynetProxy;
import com.zhiquanyeo.skynet.network.protocol.DS_Protocol2015;
import com.zhiquanyeo.skynet.network.protocol.DS_Protocol2015.DS_ClientPacket2015;
import com.zhiquanyeo.skynet.network.protocol.DS_Protocol2015.ProgramStatus;
import com.zhiquanyeo.skynet.network.protocol.DS_ProtocolBase;
import com.zhiquanyeo.skynet.network.protocol.DS_ProtocolBase.DS_Alliance;
import com.zhiquanyeo.skynet.network.protocol.DS_ProtocolBase.DS_ClientPacket;
import com.zhiquanyeo.skynet.network.protocol.DS_ProtocolBase.DS_ControlMode;
import com.zhiquanyeo.skynet.network.protocol.DS_ProtocolBase.DS_Joystick;
import com.zhiquanyeo.skynet.network.protocol.DS_ProtocolBase.DS_RobotPacket;

import edu.wpi.first.wpilibj.hal.PowerJNI;

/**
 * Replacement for using FRCNetworkCommunicationLibrary on a local skynet instance
 * @author zyeo8
 *
 */
public class FRCNetworkCommunicationsLibrary {
	public static interface tResourceType {
	    public static final int kResourceType_Controller = 0;
	    public static final int kResourceType_Module = 1;
	    public static final int kResourceType_Language = 2;
	    public static final int kResourceType_CANPlugin = 3;
	    public static final int kResourceType_Accelerometer = 4;
	    public static final int kResourceType_ADXL345 = 5;
	    public static final int kResourceType_AnalogChannel = 6;
	    public static final int kResourceType_AnalogTrigger = 7;
	    public static final int kResourceType_AnalogTriggerOutput = 8;
	    public static final int kResourceType_CANJaguar = 9;
	    public static final int kResourceType_Compressor = 10;
	    public static final int kResourceType_Counter = 11;
	    public static final int kResourceType_Dashboard = 12;
	    public static final int kResourceType_DigitalInput = 13;
	    public static final int kResourceType_DigitalOutput = 14;
	    public static final int kResourceType_DriverStationCIO = 15;
	    public static final int kResourceType_DriverStationEIO = 16;
	    public static final int kResourceType_DriverStationLCD = 17;
	    public static final int kResourceType_Encoder = 18;
	    public static final int kResourceType_GearTooth = 19;
	    public static final int kResourceType_Gyro = 20;
	    public static final int kResourceType_I2C = 21;
	    public static final int kResourceType_Framework = 22;
	    public static final int kResourceType_Jaguar = 23;
	    public static final int kResourceType_Joystick = 24;
	    public static final int kResourceType_Kinect = 25;
	    public static final int kResourceType_KinectStick = 26;
	    public static final int kResourceType_PIDController = 27;
	    public static final int kResourceType_Preferences = 28;
	    public static final int kResourceType_PWM = 29;
	    public static final int kResourceType_Relay = 30;
	    public static final int kResourceType_RobotDrive = 31;
	    public static final int kResourceType_SerialPort = 32;
	    public static final int kResourceType_Servo = 33;
	    public static final int kResourceType_Solenoid = 34;
	    public static final int kResourceType_SPI = 35;
	    public static final int kResourceType_Task = 36;
	    public static final int kResourceType_Ultrasonic = 37;
	    public static final int kResourceType_Victor = 38;
	    public static final int kResourceType_Button = 39;
	    public static final int kResourceType_Command = 40;
	    public static final int kResourceType_AxisCamera = 41;
	    public static final int kResourceType_PCVideoServer = 42;
	    public static final int kResourceType_SmartDashboard = 43;
	    public static final int kResourceType_Talon = 44;
	    public static final int kResourceType_HiTechnicColorSensor = 45;
	    public static final int kResourceType_HiTechnicAccel = 46;
	    public static final int kResourceType_HiTechnicCompass = 47;
	    public static final int kResourceType_SRF08 = 48;
	    public static final int kResourceType_AnalogOutput = 49;
	    public static final int kResourceType_VictorSP = 50;
	    public static final int kResourceType_TalonSRX = 51;
	    public static final int kResourceType_CANTalonSRX = 52;
	    public static final int kResourceType_ADXL362 = 53;
	    public static final int kResourceType_ADXRS450 = 54;
	    public static final int kResourceType_RevSPARK = 55;
	    public static final int kResourceType_MindsensorsSD540 = 56;
	    public static final int kResourceType_DigitalFilter = 57;
	};
	
	public static interface tInstances {
	    public static final int kLanguage_LabVIEW = 1;
	    public static final int kLanguage_CPlusPlus = 2;
	    public static final int kLanguage_Java = 3;
	    public static final int kLanguage_Python = 4;

	    public static final int kCANPlugin_BlackJagBridge = 1;
	    public static final int kCANPlugin_2CAN = 2;

	    public static final int kFramework_Iterative = 1;
	    public static final int kFramework_Sample = 2;
	    public static final int kFramework_CommandControl = 3;

	    public static final int kRobotDrive_ArcadeStandard = 1;
	    public static final int kRobotDrive_ArcadeButtonSpin = 2;
	    public static final int kRobotDrive_ArcadeRatioCurve = 3;
	    public static final int kRobotDrive_Tank = 4;
	    public static final int kRobotDrive_MecanumPolar = 5;
	    public static final int kRobotDrive_MecanumCartesian = 6;

	    public static final int kDriverStationCIO_Analog = 1;
	    public static final int kDriverStationCIO_DigitalIn = 2;
	    public static final int kDriverStationCIO_DigitalOut = 3;

	    public static final int kDriverStationEIO_Acceleration = 1;
	    public static final int kDriverStationEIO_AnalogIn = 2;
	    public static final int kDriverStationEIO_AnalogOut = 3;
	    public static final int kDriverStationEIO_Button = 4;
	    public static final int kDriverStationEIO_LED = 5;
	    public static final int kDriverStationEIO_DigitalIn = 6;
	    public static final int kDriverStationEIO_DigitalOut = 7;
	    public static final int kDriverStationEIO_FixedDigitalOut = 8;
	    public static final int kDriverStationEIO_PWM = 9;
	    public static final int kDriverStationEIO_Encoder = 10;
	    public static final int kDriverStationEIO_TouchSlider = 11;

	    public static final int kADXL345_SPI = 1;
	    public static final int kADXL345_I2C = 2;

	    public static final int kCommand_Scheduler = 1;

	    public static final int kSmartDashboard_Instance = 1;
	};
	
	
	/**
	 * Report the usage of a resource of interest
	 * @param resource one of the values in tResourceType above
	 * @param instanceNumber an index that identifies the resource instance
	 * @param context an optional context number for some cases. Set to 0 to omit
	 * @param feature a string to be included describing features in use on a 
	 * 		  specific resource. Setting the same resource more than once allows
	 * 		  you to change the feature string
	 * @return
	 */
	public static int FRCNetworkCommunicationsUsageReportingReport(byte resource,
			byte instanceNumber, byte context, String feature) {
		// TODO Implement
		return 0;
	}
	
	// These all inform the DS of program state
	public static void FRCNetworkCommunicationObserveUserProgramStarting() {
		// Send the DS a message that we are ready
		// We send state on every ping anyway, so just update the state
		s_instance.setProgramStatus(ProgramStatus.pProgramCodePresent);
	}
	
	public static void FRCNetworkCommunicationObserveUserProgramDisabled() {
		s_instance.setProgramStatus(ProgramStatus.pProgramDisabled);
	}
	
	public static void FRCNetworkCommunicationObserveUserProgramAutonomous() {
		s_instance.setProgramStatus(ProgramStatus.pProgramAutonomous);
	}
	
	public static void FRCNetworkCommunicationObserveUserProgramTeleop() {
		s_instance.setProgramStatus(ProgramStatus.pProgramTeleoperated);
	}
	
	public static void FRCNetworkCommunicationObserveUserProgramTest() {
		s_instance.setProgramStatus(ProgramStatus.pProgramTest);
	}
	
	public static void FRCNetworkCommunicationReserve() {
		// Initialize the HAL Library (see HALAthena.cpp)
		// TODO We could also potentially start the connection here
	}
	
	public static HALControlWord HALGetControlWord() {
		int word = s_instance.makeControlWord();
		return new HALControlWord((word & 1) != 0, ((word >> 1) & 1) != 0, ((word >> 2) & 1) != 0,
		        ((word >> 3) & 1) != 0, ((word >> 4) & 1) != 0, ((word >> 5) & 1) != 0);
	}
	
	public static HALAllianceStationID HALGetAllianceStation() {
		// TODO Get the station ID from DS
		//return HALAllianceStationID.Red1;
		switch (s_instance.getAlliance()) {
			case kAllianceRed1: 
				return HALAllianceStationID.Red1;
			case kAllianceRed2: 
				return HALAllianceStationID.Red2;
			case kAllianceRed3: 
				return HALAllianceStationID.Red3;
			case kAllianceBlue1: 
				return HALAllianceStationID.Blue1;
			case kAllianceBlue2: 
				return HALAllianceStationID.Blue2;
			case kAllianceBlue3: 
				return HALAllianceStationID.Blue3;
			default:
				System.out.println("Invalid Alliance. Returning default");
				return HALAllianceStationID.Red1; 
		}
	}
	
	public static int kMaxJoystickAxes = 12;
	public static int kMaxJoystickPOVs = 12;
	
	public static short[] HALGetJoystickAxes(byte joystickNum) {
		short[] ret = new short[kMaxJoystickAxes];
		for (int i = 0; i < kMaxJoystickAxes; i++) {
			ret[i] = 0;
		}
		DS_Joystick stick = s_instance.getJoystick(joystickNum);
		if (stick != null) {
			for (int i = 0; i < stick.numAxes; i++) {
				ret[i] = (short)(stick.axes[i] * 127);
			}
		}
		return ret;
	}
	
	public static short[] HALGetJoystickPOVs(byte joystickNum) {
		short[] ret = new short[kMaxJoystickPOVs];
		for (int i = 0; i < kMaxJoystickPOVs; i++) {
			ret[i] = -1;
		}
		
		DS_Joystick stick = s_instance.getJoystick(joystickNum);
		if (stick != null) {
			for (int i = 0; i < stick.numPovHats; i++) {
				ret[i] = (short)stick.povHats[i];
			}
		}
		return ret;
	}
	
	public static int HALGetJoystickButtons(byte joystickNum, ByteBuffer count) {
		// return an int representing the bitmap of the buttons, and 
		//count is the number of buttons
		DS_Joystick stick = s_instance.getJoystick(joystickNum);
		if (stick != null) {
			int val = 0;
			if (stick.buttons != null) {
				for (int i = 0; i < stick.buttons.length; i++) {
					val += (stick.buttons[i] ? (int)Math.pow(2, i) : 0); 
				}
				count.put((byte)stick.buttons.length);
				count.rewind();
				return val;
			}
		}
		return 0;
	}
	
	// Used to trigger stuff to happen on the DS joysticks
	public static int HALSetJoystickOutputs(byte joystickNum, int outputs, short leftRumble, short rightRumble) {
		return 0;
	}
	
	public static int HALGetJoystickIsXbox(byte joystickNum) {
		// TBD - Implement
		return 0;
	}
	
	public static int HALGetJoystickType(byte joystickNum) {
		// TBD - Implement
		return 0;
	}
	
	public static String HALGetJoystickName(byte joystickNum) {
		// TBD - Implement
		return "Joystick";
	}
	
	public static int HALGetJoystickAxisType(byte joystickNum, byte axis) {
		// TBD - No idea what this does
		return 0;
	}
	
	public static float HALGetMatchTime() {
		// When the mode switches, we want to set a time
		// TBD - Implement
		return 0.0f;
	}
	
	public static boolean HALGetSystemActive() {
		return s_instance.isSystemActive();
	}
	
	public static boolean HALGetBrownedOut() {
		return false;
	}
	
	public static int HALSetErrorData(String error) {
		return 0;
	}
	
	
	// Semaphore for throttling
	private static Semaphore s_semaphore = new Semaphore(0);
	public static Semaphore getNetworkSemaphore() {
		return s_semaphore;
	}
	
	//Use the default 2015 protocol
	private static DS_ProtocolBase s_protocol = new DS_Protocol2015();
	
	public static void setProtocol(DS_ProtocolBase newProto) {
		s_protocol.reset();
		s_protocol = newProto;
		
		// Signal the instance to restart the network thread
		s_instance.resetNetwork();
	}
	
	private static FRCNetworkCommunicationsLibrary s_instance = new FRCNetworkCommunicationsLibrary();
	
	public static FRCNetworkCommunicationsLibrary getInstance() {
		return s_instance;
	}
	
	// Properties to keep track of state
	private DS_ClientPacket d_lastPacket;
	private ProgramStatus d_programStatus = ProgramStatus.pProgramDisabled;
	private DS_ControlMode d_currentRobotMode = DS_ControlMode.kControlDisabled;
	
	private DatagramSocket d_outboundSocket;
	
	// Accessors/Setters
	public synchronized boolean isSystemActive() {
		if (d_lastPacket != null && 
				(d_lastPacket.controlMode != DS_ProtocolBase.DS_ControlMode.kControlEmergencyStop)) {
			return true;
		}
		return false;
	}
	
	public synchronized DS_Alliance getAlliance() {
		if (d_lastPacket != null) {
			return d_lastPacket.alliance;
		}
		return DS_Alliance.kAllianceInvalid;
	}
	
	public synchronized DS_Joystick getJoystick(int joystickNum) {
		if (d_lastPacket != null) {
			ArrayList<DS_Joystick> joysticks = d_lastPacket.joysticks;
			if (joysticks == null || joystickNum < 0 || joystickNum >= joysticks.size()) {
				return null;
			}
			return joysticks.get(joystickNum);
		}
		return null;
	}
	
	public synchronized DS_ControlMode getRobotMode() {
		return d_currentRobotMode;
	}
	
	public synchronized void setRobotMode(DS_ControlMode mode) {
		d_currentRobotMode = mode;
	}
	
	public synchronized ProgramStatus getProgramStatus() {
		return d_programStatus;
	}
	
	public synchronized void setProgramStatus(ProgramStatus status) {
		d_programStatus = status;
	}
	
	public synchronized int makeControlWord() {
		// Bits
		// 0 - Enabled //This means teleop
		// 1 - Autonomous
		// 2 - Test
		// 3 - EStop
		// 4 - FMS (false)
		// 5 - DS
		int cWord = 0;
		switch (d_currentRobotMode) {
			case kControlDisabled:
				cWord = 0; //All control bits 0
				break;
			case kControlTeleoperated:
				cWord = 1; //Enabled
				break;
			case kControlAutonomous:
				cWord = 3; // Enabled + Auto
				break;
			case kControlTest:
				cWord = 5; // Enabled + Test
				break;
			case kControlEmergencyStop:
				cWord = 9; // Enabled + ESTOP
				break;
		}
		
		if (d_dsAttached) {
			cWord += 32;
		}
		return cWord;
	}
	
	protected boolean d_dsAttached = false;
	
	protected DSNetworkThread d_networkThread;
	protected IDSNetworkThreadListener d_networkListener = new IDSNetworkThreadListener() {

		@Override
		public void onClientPacketReceived(DS_ClientPacket packet, InetAddress address) {
			
			d_dsAttached = true;
			
			if (s_protocol instanceof DS_Protocol2015) {
				DS_ClientPacket2015 thePacket = (DS_ClientPacket2015)packet;
				if (thePacket.packetType == DS_Protocol2015.ClientPacketTypes.pJoystick) {
					//System.out.println("Received packet with joystick data");
				}
				else if (thePacket.packetType == DS_Protocol2015.ClientPacketTypes.pTZ) {
					//System.out.println("Received packet with TZ data");
				}
				else if (thePacket.packetType == DS_Protocol2015.ClientPacketTypes.pGeneral) {
					//System.out.println("Received general packet");
				}
				else {
					System.err.println("Unknown Packet Type: ");
				}
			}
			
			// Doesn't matter, we just need the joystick packet data
			// TBD - Implement
			// We should probably also feed a watchdog
			
			if (d_currentRobotMode != packet.controlMode) {
				System.out.println("Switching control mode to: " + packet.controlMode.toString());
				// Update!
				d_currentRobotMode = packet.controlMode;
				// TODO If we are switching modes to disabled or estop,we might wanna do something smart
				if (packet.controlMode == DS_ControlMode.kControlDisabled ||
					packet.controlMode == DS_ControlMode.kControlEmergencyStop) {
					// We need to force the endpoint to stop
					SkynetProxy.publishDisable();
				}
			}
			
			// Save the packet
			d_lastPacket = packet;
			
			// Only release a permit if we currently have none
			if (s_semaphore.availablePermits() == 0) {
				s_semaphore.release();
			}
			
			// generate the response packet
			DS_RobotPacket responsePkt = new DS_RobotPacket();
			responsePkt.packetNum = packet.packetNum;
			responsePkt.controlMode = (byte)packet.controlMode.ordinal(); //Echo the control byte
			responsePkt.programStatus = (byte)(d_programStatus.getValue() | ProgramStatus.pProgramCodePresent.getValue()); // Current program state
			responsePkt.voltage = PowerJNI.getVinVoltage(IntBuffer.allocate(4));
			
			byte[] sendBuf = s_protocol.createRobotPacketBuffer(responsePkt);
			DatagramPacket outPkt = new DatagramPacket(sendBuf, sendBuf.length, address, s_protocol.getClientPort());
			try {
				d_outboundSocket.send(outPkt);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	};
	
	// Spawn a thread to listen for DS packets
	protected FRCNetworkCommunicationsLibrary() {
		try {
			d_networkThread = new DSNetworkThread(s_protocol, d_networkListener);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			d_outboundSocket = new DatagramSocket();
		}
		catch (SocketException e) {
			e.printStackTrace();
		}
		
		d_networkThread.start();
	}
	
	protected void resetNetwork() {
		if (d_networkThread != null && d_networkThread.isAlive()) {
			d_networkThread.shutdown();
		}
		
		try {
			d_networkThread = new DSNetworkThread(s_protocol, d_networkListener);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		d_networkThread.start();
	}
	
	
	// === NETWORK THREAD
	private static class DSNetworkThread extends Thread {
		protected DatagramSocket d_socket = null;
		protected DS_ProtocolBase d_proto = null;
		protected boolean d_shouldListen = true;
		protected IDSNetworkThreadListener d_listener = null;
		
		public DSNetworkThread(DS_ProtocolBase protocol, IDSNetworkThreadListener listener) throws IOException {
			super("DSNetworkThread");
			d_proto = protocol;
			d_listener = listener;
			d_socket = new DatagramSocket(d_proto.getRobotPort());
		}
		
		public void run() {
			System.out.println("DS Network Thread starting");
			while (d_shouldListen) {
				try {
					byte[] buf = new byte[512];
					
					DatagramPacket packet = new DatagramPacket(buf, buf.length);
					d_socket.receive(packet);
					DS_ClientPacket clientPacket = 
							s_protocol.readClientPacket(Arrays.copyOf(packet.getData(), packet.getLength()));
					if (clientPacket != null && d_listener != null) {
						d_listener.onClientPacketReceived(clientPacket, packet.getAddress());
					}
				}
				catch (IOException e) {
					e.printStackTrace();
					d_shouldListen = false;
				}
			}
			d_socket.close();
			System.out.println("DS Network Thread shutdown");
		}
		
		public void shutdown() {
			if (!d_socket.isClosed()) {
				d_socket.close();
			}
			d_shouldListen = false;
		}
		
	}
	
	private static interface IDSNetworkThreadListener {
		void onClientPacketReceived(DS_ClientPacket packet, InetAddress hostAddress);
	}
}
