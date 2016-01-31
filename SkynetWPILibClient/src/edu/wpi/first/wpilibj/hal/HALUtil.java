package edu.wpi.first.wpilibj.hal;

import java.nio.IntBuffer;
import java.util.Dictionary;
import java.util.Hashtable;

import com.zhiquanyeo.skynet.system.SharedSystem;

import edu.wpi.first.wpilibj.DriverStation;

public class HALUtil {
	public static final int NULL_PARAMETER = -1005;
	public static final int SAMPLE_RATE_TOO_HIGH = 1001;
	public static final int VOLTAGE_OUT_OF_RANGE = 1002;
	public static final int LOOP_TIMING_ERROR = 1004;
	public static final int INCOMPATIBLE_STATE = 1015;
	public static final int ANALOG_TRIGGER_PULSE_OUTPUT_ERROR = -1011;
	public static final int NO_AVAILABLE_RESOURCES = -104;
	public static final int PARAMETER_OUT_OF_RANGE = -1028;
	
	private static Dictionary<Integer, String> s_errMsgMap = new Hashtable<>();

	//public static final int SEMAPHORE_WAIT_FOREVER = -1;
	//public static final int SEMAPHORE_Q_PRIORITY = 0x01;

	//public static native ByteBuffer initializeMutexNormal();
	//public static native void deleteMutex(ByteBuffer sem);
	//public static native byte takeMutex(ByteBuffer sem);
	//public static native ByteBuffer initializeSemaphore(int initialValue);
	//public static native void deleteSemaphore(ByteBuffer sem);
	//public static native byte takeSemaphore(ByteBuffer sem, int timeout);
	//public static native ByteBuffer initializeMultiWait();
	//public static native void deleteMultiWait(ByteBuffer sem);
	//public static native byte takeMultiWait(ByteBuffer sem, ByteBuffer m, int timeOut);
	
	public static short getFPGAVersion(IntBuffer status) {
		status.rewind();
		status.put(0);
		return 1;
	}
	
	public static int getFPGARevision(IntBuffer status) {
		status.rewind();
		status.put(0);
		return 1;
	}
	
	public static long getFPGATime(IntBuffer status) {
		status.rewind();
		status.put(0);
		// TODO Better way to do this?
		return 0;
	}
	
	public static boolean getFPGAButton(IntBuffer status) {
		status.rewind();
		status.put(0);
		return true;
	}

	public static String getHALErrorMessage(int code) {
		return s_errMsgMap.get(code);
	}
	
	public static int getHALErrno() {
		return (int)SharedSystem.getValue("HALErrorNumber", 0);
	}
	
	public static String getHALstrerror(int errno) {
		return s_errMsgMap.get(errno);
	}
	
	public static String getHALstrerror(){
		return getHALstrerror(getHALErrno());
	}

	public static void checkStatus(IntBuffer status)
	{
		int s = status.get(0);
		if (s < 0)
		{
			String message = getHALErrorMessage(s);
			throw new RuntimeException(" Code: " + s + ". " + message);
		} else if (s > 0) {
			String message = getHALErrorMessage(s);
			DriverStation.reportError(message, true);
		}
	}
	
	static {
		s_errMsgMap.put(NULL_PARAMETER, "Null Parameter");
		s_errMsgMap.put(SAMPLE_RATE_TOO_HIGH, "Sample Rate Too High");
		s_errMsgMap.put(VOLTAGE_OUT_OF_RANGE, "Voltage Out of Range");
		s_errMsgMap.put(LOOP_TIMING_ERROR, "Loop Timing Error");
		s_errMsgMap.put(INCOMPATIBLE_STATE, "Incompatible State");
		s_errMsgMap.put(ANALOG_TRIGGER_PULSE_OUTPUT_ERROR, "Analog Trigger Pulse Output Error");
		s_errMsgMap.put(NO_AVAILABLE_RESOURCES, "No Available Resources");
		s_errMsgMap.put(PARAMETER_OUT_OF_RANGE, "Parameter Out of Range");
	}
}
