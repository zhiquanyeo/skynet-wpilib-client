package edu.wpi.first.wpilibj.communication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import com.zhiquanyeo.skynet.network.protocol.DS_Protocol2015;
import com.zhiquanyeo.skynet.network.protocol.DS_Protocol2015.DS_ClientPacket2015;
import com.zhiquanyeo.skynet.network.protocol.DS_ProtocolBase;
import com.zhiquanyeo.skynet.network.protocol.DS_ProtocolBase.DS_Alliance;
import com.zhiquanyeo.skynet.network.protocol.DS_ProtocolBase.DS_ClientPacket;
import com.zhiquanyeo.skynet.network.protocol.DS_ProtocolBase.DS_Joystick;

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
	
	public static void setNewDataSem(long mutexId) {
		
	}
	
	// These all inform the DS of program state
	public static void FRCNetworkCommunicationObserveUserProgramStarting() {
		// Send the DS a message that we are ready
	}
	
	public static void FRCNetworkCommunicationObserveUserProgramDisabled() {
		
	}
	
	public static void FRCNetworkCommunicationObserveUserProgramAutonomous() {
		
	}
	
	public static void FRCNetworkCommunicationObserveUserProgramTeleop() {
		
	}
	
	public static void FRCNetworkCommunicationObserveUserProgramTest() {
		
	}
	
	public static void FRCNetworkCommunicationReserve() {
		// Initialize the HAL Library (see HALAthena.cpp)
	}
	
	public static HALControlWord HALGetControlWord() {
		int word = 0; // TODO Get the word
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
		DS_Joystick stick = s_instance.getJoystick(joystickNum);
		if (stick != null) {
			short[] ret = new short[stick.numAxes];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = (short)(stick.axes[i] * 127);
			}
			return ret;
		}
		return null;
	}
	
	public static short[] HALGetJoystickPOVs(byte joystickNum) {
		DS_Joystick stick = s_instance.getJoystick(joystickNum);
		if (stick != null) {
			short[] ret = new short[stick.numPovHats];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = (short)stick.povHats[i];
			}
			return ret;
		}
		return null;
	}
	
	public static int HALGetJoystickButtons(byte joystickNum, ByteBuffer count) {
		// return an int representing the bitmap of the buttons, and 
		//count is the number of buttons
		return 0;
	}
	
	// Used to trigger stuff to happen on the DS joysticks
	public static int HALSetJoystickOutputs(byte joystickNum, int outputs, short leftRumble, short rightRumble) {
		return 0;
	}
	
	public static int HALGetJoystickIsXbox(byte joystickNum) {
		return 0;
	}
	
	public static int HALGetJoystickType(byte joystickNum) {
		return 0;
	}
	
	public static String HALGetJoystickName(byte joystickNum) {
		return "";
	}
	
	public static int HALGetJoystickAxisType(byte joystickNum, byte axis) {
		return 0;
	}
	
	public static float HALGetMatchTime() {
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
			if (joystickNum < 0 || joystickNum >= joysticks.size()) {
				return null;
			}
			return joysticks.get(joystickNum);
		}
		return null;
	}
	
	protected DSNetworkThread d_networkThread;
	protected IDSNetworkThreadListener d_networkListener = new IDSNetworkThreadListener() {

		@Override
		public void onClientPacketReceived(DS_ClientPacket packet) {
			if (s_protocol instanceof DS_Protocol2015) {
				DS_ClientPacket2015 thePacket = (DS_ClientPacket2015)packet;
				if (thePacket.packetType == DS_Protocol2015.ClientPacketTypes.pJoystick) {
					System.out.println("Received packet with joystick data");
				}
				else if (thePacket.packetType == DS_Protocol2015.ClientPacketTypes.pTZ) {
					System.out.println("Received packet with TZ data");
				}
				else {
					System.err.println("Unknown Packet Type");
				}
			}
			
			// Doesn't matter, we just need the joystick packet data
			
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
				byte[] buf = new byte[512];
				
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				DS_ClientPacket clientPacket = 
						s_protocol.readClientPacket(Arrays.copyOf(buf, packet.getLength()));
				if (clientPacket != null && d_listener != null) {
					d_listener.onClientPacketReceived(clientPacket);
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
		void onClientPacketReceived(DS_ClientPacket packet);
	}
}
