package com.zhiquanyeo.skynet.system;

import java.util.Dictionary;
import java.util.Hashtable;

public class SharedSystem {
	private static Dictionary<String, Object> s_systemValues = new Hashtable<>();
	
	public static void putValue(String key, Object val) {
		s_systemValues.put(key, val);
	}
	
	public static Object getValue(String key, Object defaultVal) {
		if (s_systemValues.get(key) == null) {
			return defaultVal;
		}
		return s_systemValues.get(key);
	}
}
