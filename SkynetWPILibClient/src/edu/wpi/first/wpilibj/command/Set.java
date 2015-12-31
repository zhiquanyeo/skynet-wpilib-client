package edu.wpi.first.wpilibj.command;

import java.util.Enumeration;
import java.util.Vector;

class Set<V> {
	Vector<V> set = new Vector<V>();
	
	public Set() {}
	
	public void add(V o) {
		if (set.contains(o)) return;
		set.addElement(o);
	}
	
	public void add(Set<V> s) {
		Enumeration<V> stuff = s.getElements();
		for (Enumeration<V> e = stuff; e.hasMoreElements();) {
			add(e.nextElement());
		}
	}
	
	public boolean contains(Object o) {
		return set.contains(o);
	}
	
	public Enumeration<V> getElements() {
		return set.elements();
	}
}
