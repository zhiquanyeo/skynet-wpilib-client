package edu.wpi.first.wpilibj;

public interface PIDSource {
	public static class PIDSourceParameter {
		public final int value;
		static final int kDistance_val = 0;
		static final int kRate_val = 1;
		static final int kAngle_val = 2;
		public static final PIDSourceParameter kDistance = new PIDSourceParameter(kDistance_val);
		public static final PIDSourceParameter kRate = new PIDSourceParameter(kRate_val);
		public static final PIDSourceParameter kAngle = new PIDSourceParameter(kAngle_val);
		
		private PIDSourceParameter(int value) {
			this.value = value;
		}
	}
	
	public double pidGet();
}
