package edu.wpi.first.wpilibj.command;

import java.util.Enumeration;
import java.util.Vector;

class Set {
	Vector set = new Vector();
	
	public Set() {}
	
	public void add(Object o) {
		if (set.contains(o)) return;
		set.addElement(o);
	}
	
	public void add(Set s) {
		Enumeration stuff = s.getElements();
		for (Enumeration e = stuff; e.hasMoreElements();) {
			add(e.nextElement());
		}
	}
	
	public boolean contains(Object o) {
		return set.contains(o);
	}
	
	public Enumeration getElements() {
		return set.elements();
	}
}
