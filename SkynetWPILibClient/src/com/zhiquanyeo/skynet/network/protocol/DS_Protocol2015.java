package com.zhiquanyeo.skynet.network.protocol;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.zhiquanyeo.skynet.network.protocol.DS_ProtocolBase.DS_CommStatus;

public class DS_Protocol2015 extends DS_ProtocolBase {

	private static enum ControlModes {
		pControlTest(0x05),
		pControlDisabled(0x00),
		pControlAutonomous(0x06),
		pControlTeleoperated(0x04),
		pControlEmergencyStop(0x80),
		pControlInvalid(0xFF);
		
		private final int d_value;
		ControlModes(int val) { d_value = val; }
		public int getValue() { return d_value; }
		
		private static final Map<Integer, ControlModes> intToTypeMap = 
				new HashMap<Integer, ControlModes>();
		static {
			for (ControlModes type : ControlModes.values()) {
				intToTypeMap.put(type.getValue(), type);
			}
		}
		
		public static ControlModes fromInt(int i) {
			ControlModes type = intToTypeMap.get(Integer.valueOf(i));
			if (type == null) return ControlModes.pControlInvalid;
			return type;
		}
	};
	
	private static enum Headers {
		pHeaderTime(0x0F),
		pHeaderGeneral(0x01),
		pHeaderJoystick(0x0C),
		pHeaderTimezone(0x10),
		pHeaderInvalid(0xFF);
		
		private final int d_value;
		Headers(int val) { d_value = val; }
		public int getValue() { return d_value; }
		
		private static final Map<Integer, Headers> intToTypeMap = 
				new HashMap<Integer, Headers>();
		static {
			for (Headers type : Headers.values()) {
				intToTypeMap.put(type.getValue(), type);
			}
		}
		
		public static Headers fromInt(int i) {
			Headers type = intToTypeMap.get(Integer.valueOf(i));
			if (type == null) return Headers.pHeaderInvalid;
			return type;
		}
	};
	
	//Commands from DS -> Robot
	private static enum ClientRequestBytes {
		pStatusNormal(0x10),
		pStatusInvalid(0x00),
		pStatusRebootRio(0x18),		// Reboot RIO
		pStatusRestartCode(0x14);	// Kill user code
		
		private final int d_value;
		ClientRequestBytes(int val) { d_value = val; }
		public int getValue() { return d_value; }
	};
	
	// Echo codes from the Robot
	private static enum ProgramStatus {
		pProgramTest(0x08),
		pProgramDisabled(0x01),
		pProgramAutonomous(0x04),
		pProgramCodePresent(0x20),
		pProgramTeleoperated(0x02),
		pProgramInvalid(0xFF);
		
		private final int d_value;
		ProgramStatus(int val) { d_value = val; }
		public int getValue() { return d_value; }
		
		private static final Map<Integer, ProgramStatus> intToTypeMap = 
				new HashMap<Integer, ProgramStatus>();
		static {
			for (ProgramStatus type : ProgramStatus.values()) {
				intToTypeMap.put(type.getValue(), type);
			}
		}
		
		public static ProgramStatus fromInt(int i) {
			ProgramStatus type = intToTypeMap.get(Integer.valueOf(i));
			if (type == null) return ProgramStatus.pProgramInvalid;
			return type;
		}
	};
	
	// Operations the robot wants the DS to do
	private static enum RobotRequestBytes {
		pRobotRequestTime(0x01);
		
		private final int d_value;
		RobotRequestBytes(int val) { d_value = val; }
		public int getValue() { return d_value; }
	};
	
	public static enum ClientPacketTypes {
		pTZ,
		pJoystick
	};
	
	public static class DS_TZData {
		public Date date;
		public String timezone;
	}
	
	public static class DS_ClientPacket2015 extends DS_ClientPacket {
		public ClientPacketTypes packetType;
		public DS_TZData tzData;
	}
	
	public DS_Protocol2015() {
		reset();
	}
	
	@Override
	public int getRobotPort() {
		return 1110;
	}

	@Override
	public int getClientPort() {
		return 1150;
	}

	@Override
	public void reboot() {
		setStatus(ClientRequestBytes.pStatusRebootRio.getValue());
	}

	@Override
	public void restartCode() {
		setStatus(ClientRequestBytes.pStatusRestartCode.getValue());
	}

	@Override
	public void resetProtocol() {
		setStatus(ClientRequestBytes.pStatusInvalid.getValue());
	}

	@Override
	public DS_ClientPacket readClientPacket(byte[] buffer) {
		System.out.println("Buf size: " + buffer.length);
		if (buffer.length < 6) {
			// bare minimum packet length
			System.err.println("Under minimum packet length");
			return null;
		}
		
		/**
		 * Buffer Layout
		 * [0] = packet number (MSB)
		 * [1] = packet number (LSB)
		 * [2] = Header
		 * [3] = Control Mode (operation)
		 * [4] = Status (Special instructions) one of the pStatusXYZ codes
		 * [5] = Alliance color and position
		 * [6] = ... Joystick data or TZ info
		 * [7] = Header (TZ or Joystick)
		 * TZ data starts with 0x0B, 2nd byte of Joystick data is the joystick header
		 */
		
		DS_ClientPacket2015 pkt = new DS_ClientPacket2015();
		pkt.packetNum = (int)(buffer[0] << 8) + (int)buffer[1];
		int packetHeader = buffer[2];
		int controlMode = buffer[3];
		int status = buffer[4];
		int alliance = buffer[5];
		
		if (buffer.length > 6) {
			int payloadSize = buffer[6];
			int payloadType = buffer[7];
			
			pkt.payload = Arrays.copyOfRange(buffer, 6, buffer.length - 1);
			
			if (payloadType == Headers.pHeaderTime.getValue()) {
				pkt.packetType = ClientPacketTypes.pTZ;
				pkt.tzData = bufferToTZData(pkt.payload);
			}
			else if (payloadType == Headers.pHeaderJoystick.getValue()) {
				pkt.packetType = ClientPacketTypes.pJoystick;
				pkt.joysticks = bufferToJoystick(pkt.payload);
			}
			else {
				// Invalid data
				System.err.println("Invalid Data");
				return null;
			}
		}
		
		return pkt;
	}

	@Override
	public DS_RobotPacket readRobotPacket(byte[] buffer) {
		// This is a packet that is obtained from the Robot
		if (buffer.length < 8) {
			return null;
		}
		
		if (getStatus() == ClientRequestBytes.pStatusInvalid.getValue()) {
			setStatus(ClientRequestBytes.pStatusNormal.getValue());
		}
		
		DS_RobotPacket pkt = new DS_RobotPacket();
		pkt.packetNum = ((int)buffer[0] << 8) + (int)buffer[1];
		pkt.state = buffer[4];
		pkt.status = buffer[4];
		pkt.voltage = Double.parseDouble("" + buffer[5] + "." + buffer[6]);
		pkt.request = buffer[7];
		
		return pkt;
	}
	
	private byte[] tzToBuffer(DS_TZData tzData) {
		
		return null;
	}
	
	private DS_TZData bufferToTZData(byte[] buffer) {
		// Buffer should be 13+ bytes long
		// 0 - 10 are time data
		// 11 and 12 are TZ data
		if (buffer.length < 13) {
			return null;
		}
		
		DS_TZData tzData = new DS_TZData();
		/**
		 * [0] 	= size
		 * [1] 	= header
		 * [2] 	= time.msec() / 255
		 * [3] 	= time.msec() - [2]
		 * [4] 	= time.second
		 * [5] 	= time.minute
		 * [6] 	= time.hour
		 * [7] 	= date.day
		 * [8] 	= date.month
		 * [9] 	= date.year - 1900
		 * [10] = sizeof(timezone)
		 * [11]	= tz header
		 * [12+]= TZ string
		 */
		int msec = ((int)buffer[2] * 255) + (int)buffer[3];
		
		Calendar cal = Calendar.getInstance();
		cal.set((buffer[9] + 1900), buffer[8], buffer[7], buffer[6], buffer[5], buffer[4]);
		cal.add(Calendar.MILLISECOND, msec);
		
		tzData.date = cal.getTime();
		byte[] tzBuf = Arrays.copyOfRange(buffer, 12, 12 + buffer[10]);
		tzData.timezone = new String(tzBuf);
		return tzData;
	}
	
	private int getJoystickBufferSize(DS_Joystick stick) {
		return 5
			   + (stick.numAxes > 0 ? stick.numAxes : 0)
			   + (stick.numButtons / 8)
			   + (stick.numButtons % 8 == 0 ? 0 : 1)
			   + (stick.numPovHats > 0 ? stick.numPovHats * 2 : 0);
	}

	@Override
	public byte[] joystickToBuffer(ArrayList<DS_Joystick> joysticks) {
		ByteBuffer buf = ByteBuffer.allocate(1024);
		
		for (int i = 0; i < joysticks.size(); i++) {
			DS_Joystick stick = joysticks.get(i);
			
			buf.put((byte)(getJoystickBufferSize(stick) - 1));
			buf.put((byte)Headers.pHeaderJoystick.getValue());
			
			// Axis Data
			buf.put((byte)stick.numAxes);
			for (int axis = 0; axis < stick.numAxes; axis++) {
				buf.put((byte)(stick.axes[axis] * 127));
			}
			
			// Button Data
			buf.put((byte)stick.numButtons);
			int buttons = 0;
			for (int button = 0; button < stick.numButtons; button++) {
				boolean pressed = stick.buttons[button];
				//buttons = (buttons << 1) + (pressed ? 1:0);
				buttons += pressed ? (int)Math.pow(2, button) : 0;
			}
			
			
			if (stick.numButtons > 8) {
				buf.put((byte)((buttons >> 8) & 0xFF));
			}
			if (stick.numButtons > 0) {
				buf.put((byte)(buttons & 0xFF));
			}
			
			// POV Hats
			buf.put((byte)stick.numPovHats);
			for (int hat = 0; hat < stick.numPovHats; hat++) {
				buf.put((byte)((stick.povHats[hat] >> 8) & 0xFF));
				buf.put((byte)(stick.povHats[hat] & 0xFF));
			}
		}
		
		buf.flip();
		byte[] buffer = new byte[buf.remaining()];
		buf.get(buffer);

		return buffer;
	}

	@Override
	public ArrayList<DS_Joystick> bufferToJoystick(byte[] buffer) {
		// We will need to run through the buffer and jump by whatever the size is
		ArrayList<DS_Joystick> joysticks = new ArrayList<DS_Joystick>();
		int currPtr = 0;
		int frameStart = 0;
		
		/**
		 * Joystick Buffer layout
		 * [0] 			= Frame Size
		 * [1] 			= Header
		 * [2] 			= Number of Axes (nAxis)
		 * [3] 			= Axis values
		 * [...]		= Axis Values
		 * [3+nAxis]	= Number of Buttons (nButtons)  - Call this index bStart
		 * [bStart+1]	= Button Data
		 * [bStart+2]	= Button Data (Maybe)
		 * [..]			= Number of POV Hats
		 * [..]			= 2 bytes per hat value
		 */
		while (currPtr < buffer.length) {
			int frameSize = buffer[currPtr];
			currPtr++;
			
			DS_Joystick stickData = new DS_Joystick();
			
			if (buffer[currPtr] != Headers.pHeaderJoystick.getValue()) {
				System.out.println("Invalid JS header. Skipping ahead");
				currPtr += frameSize;
			}
			else {
				currPtr++; // Advance the pointer
				
				stickData.numAxes = buffer[currPtr++];
				stickData.axes = new double[stickData.numAxes];
				
				//currPtr is now pointing to first axis record
				for (int i = 0; i < stickData.numAxes; i++) {
					stickData.axes[i] = (double)((byte)buffer[currPtr++] / 127.0);
				}
				
				//currPtr is now pointing to numButtons
				stickData.numButtons = buffer[currPtr++];
				stickData.buttons = new boolean[stickData.numButtons];
				int btnByteCount = stickData.numButtons/8 + ((stickData.numButtons%8 == 0) ? 0:1);
				
				//currPtr is now pointing to first byte of buttons, potentially
				int buttonVal = 0;
				if (btnByteCount == 1) {
					buttonVal = (buffer[currPtr++] & 0xFF);
				}
				else if (btnByteCount == 2) {
					buttonVal = ((buffer[currPtr++] << 8) & 0xFF00) + (buffer[currPtr++] & 0xFF);
				}
				
				for (int i = 0; i < stickData.numButtons; i++) {
					stickData.buttons[i] = (((buttonVal >> i) & 0x01) == 1);
				}
				
				//currPtr is now pointing at numPOVs
				stickData.numPovHats = buffer[currPtr++];
				stickData.povHats = new int[stickData.numPovHats];
				
				//currPtr is now pointing at the first of POV values
				for (int i = 0; i < stickData.numPovHats; i++) {
					stickData.povHats[i] = (buffer[currPtr++] << 8) + buffer[currPtr++];
				}
				
				joysticks.add(stickData);
			}
		}
		
		return joysticks;
	}

	@Override
	public byte[] createClientPacketBuffer(DS_ClientPacket packet) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] createRobotPacketBuffer(DS_RobotPacket packet) {
		byte[] buffer = new byte[8];
		buffer[0] = (byte)((packet.packetNum >> 8) & 0xFF);
		buffer[1] = (byte)(packet.packetNum & 0xFF);
		buffer[3] = packet.state;
		buffer[4] = packet.status;
		
		int vMajor, vMinor;
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		BigDecimal voltage = new BigDecimal(df.format(packet.voltage));
		vMajor = voltage.intValue();
		vMinor = voltage.subtract(new BigDecimal(voltage.intValue())).multiply(new BigDecimal(100)).intValue();
		
		buffer[5] = (byte)vMajor;
		buffer[6] = (byte)vMinor;
		buffer[7] = packet.request;
		
		return buffer;
	}
	
	
}
