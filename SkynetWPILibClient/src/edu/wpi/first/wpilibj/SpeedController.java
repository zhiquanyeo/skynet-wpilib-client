package edu.wpi.first.wpilibj;

public interface SpeedController extends PIDOutput {
	/**
     * Common interface for getting the current set speed of a speed controller.
     *
     * @return The current set speed.  Value is between -1.0 and 1.0.
     */
    double get();

    /**
     * Common interface for setting the speed of a speed controller.
     *
     * @param speed The speed to set.  Value should be between -1.0 and 1.0.
     * @param syncGroup The update group to add this Set() to, pending UpdateSyncGroup().  If 0, update immediately.
     */
    void set(double speed, byte syncGroup);

    /**
     * Common interface for setting the speed of a speed controller.
     *
     * @param speed The speed to set.  Value should be between -1.0 and 1.0.
     */
    void set(double speed);

    /**
     * Disable the speed controller
     */
    void disable();
}
