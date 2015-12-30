package edu.wpi.first.wpilibj;

public interface PIDSource {
	public void setPIDSourceType(PIDSourceType pidSource);
	public PIDSourceType getPIDSourceType();
	public double pidGet();
}
