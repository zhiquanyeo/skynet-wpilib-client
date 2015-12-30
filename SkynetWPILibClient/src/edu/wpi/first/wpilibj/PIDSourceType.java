package edu.wpi.first.wpilibj;

public enum PIDSourceType {
	kDisplacement(0),
	kRate(1);
	
	public final int value;
	private PIDSourceType(int value) {
		this.value = value;
	}
}
