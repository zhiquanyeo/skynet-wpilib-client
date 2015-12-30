package edu.wpi.first.wpilibj.command;

class LinkedListElement {
	private LinkedListElement next;
	private LinkedListElement previous;
	private Command data;
	
	public LinkedListElement() {}
	
	public void setData(Command newData) {
		data = newData;
	}
	
	public Command getData() {
		return data;
	}
	
	public LinkedListElement getNext() {
		return next;
	}
	
	public LinkedListElement getPrevious() {
		return previous;
	}
	
	public void add(LinkedListElement l) {
		if (next == null) {
			next = l;
			next.previous = this;
		}
		else {
			next.previous = l;
			l.next = next;
			l.previous = this;
			next = l;
		}
	}
	
	public LinkedListElement remove() {
		if (previous == null && next == null) {
			
		}
		else if (next == null) {
			previous.next = null;
		}
		else if (previous == null) {
			next.previous = null;
		}
		else {
			next.previous = previous;
			previous.next = next;
		}
		
		LinkedListElement n = next;
		next = null;
		previous = null;
		return n;
	}
}
