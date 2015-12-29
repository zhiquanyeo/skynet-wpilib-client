package com.zhiquanyeo.skynet.network.protocol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class DS_ProtocolBase {
	// ===== Enums and supporting classes =====
	public static enum DS_Alliance {
		kAllianceRed1(0x00),
		kAllianceRed2(0x01),
		kAllianceRed3(0x02),
		kAllianceBlue1(0x03),
		kAllianceBlue2(0x04),
		kAllianceBlue3(0x05),
		kAllianceInvalid(0xFF);
		
		private final int d_value;
		DS_Alliance(int value) { d_value = value; }
		public int getValue() { return d_value; }
		
		private static final Map<Integer, DS_Alliance> intToTypeMap = 
				new HashMap<Integer, DS_Alliance>();
		static {
			for (DS_Alliance type : DS_Alliance.values()) {
				intToTypeMap.put(type.getValue(), type);
			}
		}
		
		public static DS_Alliance fromInt(int i) {
			DS_Alliance type = intToTypeMap.get(Integer.valueOf(i));
			if (type == null) return DS_Alliance.kAllianceInvalid;
			return type;
		}
	};
	
	public static enum DS_ControlMode {
		kControlTest,
		kControlDisabled,
		kControlAutonomous,
		kControlTeleoperated,
		kControlEmergencyStop
	};
	
	public static enum DS_CommStatus {
		kFull(0x00),
		kPartial(0x01),	// Pings, but does not respond to DS
		kFailing(0x02);	// 
		
		private final int d_value;
		DS_CommStatus(int value) { d_value = value; }
		public int getValue() { return d_value; }
		
		private static final Map<Integer, DS_CommStatus> intToTypeMap = 
				new HashMap<Integer, DS_CommStatus>();
		static {
			for (DS_CommStatus type : DS_CommStatus.values()) {
				intToTypeMap.put(type.getValue(), type);
			}
		}
		
		public static DS_CommStatus fromInt(int i) {
			DS_CommStatus type = intToTypeMap.get(Integer.valueOf(i));
			if (type == null) return DS_CommStatus.kFailing;
			return type;
		}
	};
	
	public static class DS_Joystick {
		public int numAxes;
		public int numButtons;
		public int numPovHats;
		
		public boolean[] buttons;
		public int[] povHats;
		public double[] axes;
		
		public boolean equals(DS_Joystick target) {
			if (numAxes != target.numAxes) return false;
			if (numButtons != target.numButtons) return false;
			if (numPovHats != target.numPovHats) return false;
			
			if ((numButtons > 0 && buttons == null) || 
				(target.numButtons > 0 && target.buttons == null)) {
				if (buttons.length != target.buttons.length) return false;
			}
			
			for (int i = 0; i < numButtons; i++) {
				if (buttons[i] != target.buttons[i]) return false;
			}
			
			if ((numAxes > 0 && axes == null) || 
				(target.numAxes > 0 && target.axes == null)) {
				if (axes.length != target.axes.length) return false;
			}
			
			for (int i = 0; i < numAxes; i++) {
				if (axes[i] != target.axes[i]) return false;
			}
			
			if ((numPovHats > 0 && povHats == null) || 
				(target.numPovHats > 0 && target.povHats == null)) {
				if (povHats.length != target.povHats.length) return false;
			}
			
			for (int i = 0; i < numPovHats; i++) {
				if (povHats[i] != target.povHats[i]) return false;
			}
			
			return true;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("=== Joystick ===\n");
			sb.append("\tnumAxes: " + numAxes + "\n");
			sb.append("\tnumButtons: " + numButtons + "\n");
			sb.append("\tnumPOV: " + numPovHats + "\n\n");
			sb.append("\tAxes:\n");
			if (axes == null || axes.length == 0) {
				sb.append("\tNULL\n\n");
			}
			else {
				for (int i = 0; i < axes.length; i++) {
					sb.append("\t[" + i + "] " + axes[i] + "\n");
				}
				sb.append("\n");
			}
			
			sb.append("\tButtons:\n");
			if (buttons == null || buttons.length == 0) {
				sb.append("\tNULL\n\n");
			}
			else {
				for (int i = 0; i < buttons.length; i++) {
					sb.append("\t[" + i + "] " + buttons[i] + "\n");
				}
				sb.append("\n");
			}
			
			sb.append("\tPOV Hats:\n");
			if (povHats == null || povHats.length == 0) {
				sb.append("\tNULL\n\n");
			}
			else {
				for (int i = 0; i < povHats.length; i++) {
					sb.append("\t[" + i + "] " + povHats[i] + "\n");
				}
				sb.append("\n");
			}
			return sb.toString();
		}
	};
	
	public static class DS_RobotPacket {
		public int packetNum;
		public byte state; 	//TODO What's the difference?
		public byte status;	//TODO What's the difference?
		public double voltage;
		public byte request;
	}
	
	public static class DS_ClientPacket {
		public int packetNum;
		public DS_ControlMode controlMode;
		public int status;
		public DS_Alliance alliance;
		
		public byte[] payload;
		public ArrayList<DS_Joystick> joysticks;
	}
	
	// ===== Class Implementation =====
	
	private int d_status;
	private int d_sentPackets;
	
	/**
	 * Alliance that this robot is set to
	 */
	private DS_Alliance d_alliance;
	
	/**
	 * Current control mode of the robot
	 */
	private DS_ControlMode d_controlMode;
	
	private ArrayList<DS_Joystick> d_joysticks;
	
	private ArrayList<IDS_ProtocolListener> d_listeners;
	
	// ===== Methods =====
	public DS_ProtocolBase() {
		d_status = 0;
		d_sentPackets = 0;
		d_controlMode = DS_ControlMode.kControlDisabled;
		d_alliance = DS_Alliance.kAllianceRed1;
		
		d_joysticks = new ArrayList<DS_Joystick>();
		d_listeners = new ArrayList<IDS_ProtocolListener>();
	}
	
	public int getStatus() { return d_status; }
	public void setStatus(int val) { d_status = val; }
	
	public int getNumSentPackets() { return d_sentPackets; }
	
	public DS_Alliance getAlliance() { return d_alliance; }
	public void setAlliance(DS_Alliance val) { d_alliance = val; }
	
	public DS_ControlMode getControlMode() { return d_controlMode; }
	public void setControlMode(DS_ControlMode val) { d_controlMode = val; }
	
	public ArrayList<DS_Joystick> getJoysticks() { return d_joysticks; }
	public void setJoysticks(ArrayList<DS_Joystick> val) { d_joysticks = val; }
	
	public void reset() {
		resetProtocol();
	}
	
	public void addListener(IDS_ProtocolListener listener) {
		d_listeners.add(listener);
	}
	
	public void removeListener(IDS_ProtocolListener listener) {
		while (d_listeners.contains(listener)) {
			d_listeners.remove(listener);
		}
	}
	
	// ===== Protocol Specific Implementations =====
	public abstract int getRobotPort();
	public abstract int getClientPort();
	
	public abstract void reboot();
	public abstract void restartCode();
	public abstract void resetProtocol();
	
	public abstract DS_ClientPacket readClientPacket(byte[] buffer);
	public abstract DS_RobotPacket readRobotPacket(byte[] buffer);
	public abstract byte[] createClientPacketBuffer(DS_ClientPacket packet);
	public abstract byte[] createRobotPacketBuffer(DS_RobotPacket packet);
	public abstract byte[] joystickToBuffer(ArrayList<DS_Joystick> joysticks);
	public abstract ArrayList<DS_Joystick> bufferToJoystick(byte[] buffer);
}
