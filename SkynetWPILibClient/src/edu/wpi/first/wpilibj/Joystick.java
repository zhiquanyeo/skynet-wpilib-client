package edu.wpi.first.wpilibj;

import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary;
import edu.wpi.first.wpilibj.communication.FRCNetworkCommunicationsLibrary.tResourceType;
import edu.wpi.first.wpilibj.communication.UsageReporting;

public class Joystick extends GenericHID {
	static final byte kDefaultXAxis = 0;
    static final byte kDefaultYAxis = 1;
    static final byte kDefaultZAxis = 2;
    static final byte kDefaultTwistAxis = 2;
    static final byte kDefaultThrottleAxis = 3;
    static final int kDefaultTriggerButton = 1;
    static final int kDefaultTopButton = 2;

    /**
     * Represents an analog axis on a joystick.
     */
    public static class AxisType {

        /**
         * The integer value representing this enumeration
         */
        public final int value;
        static final int kX_val = 0;
        static final int kY_val = 1;
        static final int kZ_val = 2;
        static final int kTwist_val = 3;
        static final int kThrottle_val = 4;
        static final int kNumAxis_val = 5;
        /**
         * axis: x-axis
         */
        public static final AxisType kX = new AxisType(kX_val);
        /**
         * axis: y-axis
         */
        public static final AxisType kY = new AxisType(kY_val);
        /**
         * axis: z-axis
         */
        public static final AxisType kZ = new AxisType(kZ_val);
        /**
         * axis: twist
         */
        public static final AxisType kTwist = new AxisType(kTwist_val);
        /**
         * axis: throttle
         */
        public static final AxisType kThrottle = new AxisType(kThrottle_val);
        /**
         * axis: number of axis
         */
        public static final AxisType kNumAxis = new AxisType(kNumAxis_val);

        private AxisType(int value) {
            this.value = value;
        }
    }

    /**
     * Represents a digital button on the JoyStick
     */
    public static class ButtonType {

        /**
         * The integer value representing this enumeration
         */
        public final int value;
        static final int kTrigger_val = 0;
        static final int kTop_val = 1;
        static final int kNumButton_val = 2;
        /**
         * button: trigger
         */
        public static final ButtonType kTrigger = new ButtonType((kTrigger_val));
        /**
         * button: top button
         */
        public static final ButtonType kTop = new ButtonType(kTop_val);
        /**
         * button: num button types
         */
        public static final ButtonType kNumButton = new ButtonType((kNumButton_val));

        private ButtonType(int value) {
            this.value = value;
        }
    }
    
    
    /**
     * Represents a rumble output on the JoyStick
     */
    public static class RumbleType {

        /**
         * The integer value representing this enumeration
         */
        public final int value;
        static final int kLeftRumble_val = 0;
        static final int kRightRumble_val = 1;
        /**
         * Left Rumble
         */
        public static final RumbleType kLeftRumble = new RumbleType((kLeftRumble_val));
        /**
         * Right Rumble
         */
        public static final RumbleType kRightRumble = new RumbleType(kRightRumble_val);

        private RumbleType(int value) {
            this.value = value;
        }
    }
    
    private DriverStation d_ds;
    private final int d_port;
    private final byte[] d_axes;
    private final byte[] d_buttons;
    private int d_outputs;
    private short d_leftRumble;
    private short d_rightRumble;

    /**
     * Construct an instance of a joystick.
     * The joystick index is the usb port on the drivers station.
     *
     * @param port The port on the driver station that the joystick is plugged into.
     */
    public Joystick(final int port) {
        this(port, AxisType.kNumAxis.value, ButtonType.kNumButton.value);

        d_axes[AxisType.kX.value] = kDefaultXAxis;
        d_axes[AxisType.kY.value] = kDefaultYAxis;
        d_axes[AxisType.kZ.value] = kDefaultZAxis;
        d_axes[AxisType.kTwist.value] = kDefaultTwistAxis;
        d_axes[AxisType.kThrottle.value] = kDefaultThrottleAxis;

        d_buttons[ButtonType.kTrigger.value] = kDefaultTriggerButton;
        d_buttons[ButtonType.kTop.value] = kDefaultTopButton;

        UsageReporting.report(tResourceType.kResourceType_Joystick, port);
    }

    /**
     * Protected version of the constructor to be called by sub-classes.
     *
     * This constructor allows the subclass to configure the number of constants
     * for axes and buttons.
     *
     * @param port The port on the driver station that the joystick is plugged into.
     * @param numAxisTypes The number of axis types in the enum.
     * @param numButtonTypes The number of button types in the enum.
     */
    protected Joystick(int port, int numAxisTypes, int numButtonTypes) {
        d_ds = DriverStation.getInstance();
        d_axes = new byte[numAxisTypes];
        d_buttons = new byte[numButtonTypes];
        d_port = port;
    }

    /**
     * Get the X value of the joystick.
     * This depends on the mapping of the joystick connected to the current port.
     *
     * @param hand Unused
     * @return The X value of the joystick.
     */
    public double getX(Hand hand) {
        return getRawAxis(d_axes[AxisType.kX.value]);
    }

    /**
     * Get the Y value of the joystick.
     * This depends on the mapping of the joystick connected to the current port.
     *
     * @param hand Unused
     * @return The Y value of the joystick.
     */
    public double getY(Hand hand) {
        return getRawAxis(d_axes[AxisType.kY.value]);
    }

    /**
     * Get the Z value of the joystick.
     * This depends on the mapping of the joystick connected to the current port.
     *
     * @param hand Unused
     * @return The Z value of the joystick.
     */
    public double getZ(Hand hand) {
        return getRawAxis(d_axes[AxisType.kZ.value]);
    }

    /**
     * Get the twist value of the current joystick.
     * This depends on the mapping of the joystick connected to the current port.
     *
     * @return The Twist value of the joystick.
     */
    public double getTwist() {
        return getRawAxis(d_axes[AxisType.kTwist.value]);
    }

    /**
     * Get the throttle value of the current joystick.
     * This depends on the mapping of the joystick connected to the current port.
     *
     * @return The Throttle value of the joystick.
     */
    public double getThrottle() {
        return getRawAxis(d_axes[AxisType.kThrottle.value]);
    }

    /**
     * Get the value of the axis.
     *
     * @param axis The axis to read, starting at 0.
     * @return The value of the axis.
     */
    public double getRawAxis(final int axis) {
        return d_ds.getStickAxis(d_port, axis);
    }

    /**
     * For the current joystick, return the axis determined by the argument.
     *
     * This is for cases where the joystick axis is returned programatically, otherwise one of the
     * previous functions would be preferable (for example getX()).
     *
     * @param axis The axis to read.
     * @return The value of the axis.
     */
    public double getAxis(final AxisType axis) {
        switch (axis.value) {
        case AxisType.kX_val:
            return getX();
        case AxisType.kY_val:
            return getY();
        case AxisType.kZ_val:
            return getZ();
        case AxisType.kTwist_val:
            return getTwist();
        case AxisType.kThrottle_val:
            return getThrottle();
        default:
            return 0.0;
        }
    }

    /**
    * For the current joystick, return the number of axis
    */
    public int getAxisCount(){
        return d_ds.getStickAxisCount(d_port);
    }

    /**
     * Read the state of the trigger on the joystick.
     *
     * Look up which button has been assigned to the trigger and read its state.
     *
     * @param hand This parameter is ignored for the Joystick class and is only here to complete the GenericHID interface.
     * @return The state of the trigger.
     */
    public boolean getTrigger(Hand hand) {
        return getRawButton(d_buttons[ButtonType.kTrigger.value]);
    }

    /**
     * Read the state of the top button on the joystick.
     *
     * Look up which button has been assigned to the top and read its state.
     *
     * @param hand This parameter is ignored for the Joystick class and is only here to complete the GenericHID interface.
     * @return The state of the top button.
     */
    public boolean getTop(Hand hand) {
        return getRawButton(d_buttons[ButtonType.kTop.value]);
    }

    /**
     * This is not supported for the Joystick.
     * This method is only here to complete the GenericHID interface.
     *
     * @param hand This parameter is ignored for the Joystick class and is only here to complete the GenericHID interface.
     * @return The state of the bumper (always false)
     */
    public boolean getBumper(Hand hand) {
        return false;
    }

    /**
     * Get the button value (starting at button 1)
     *
     * The appropriate button is returned as a boolean value.
     *
     * @param button The button number to be read (starting at 1).
     * @return The state of the button.
     */
    public boolean getRawButton(final int button) {
        return d_ds.getStickButton(d_port, (byte)button);
    }

    /**
    * For the current joystick, return the number of buttons
    */
    public int getButtonCount(){
        return d_ds.getStickButtonCount(d_port);
    }

    /**
     * Get the state of a POV on the joystick.
     *
	 * @param pov The index of the POV to read (starting at 0)
     * @return the angle of the POV in degrees, or -1 if the POV is not pressed.
     */
    public int getPOV(int pov) {
        return d_ds.getStickPOV(d_port, pov);
    }

    /**
    * For the current joystick, return the number of POVs
    */
    public int getPOVCount(){
        return d_ds.getStickPOVCount(d_port);
    }

    /**
     * Get buttons based on an enumerated type.
     *
     * The button type will be looked up in the list of buttons and then read.
     *
     * @param button The type of button to read.
     * @return The state of the button.
     */
    public boolean getButton(ButtonType button) {
        switch (button.value) {
        case ButtonType.kTrigger_val:
            return getTrigger();
        case ButtonType.kTop_val:
            return getTop();
        default:
            return false;
        }
    }

    /**
     * Get the magnitude of the direction vector formed by the joystick's
     * current position relative to its origin
     *
     * @return The magnitude of the direction vector
     */
    public double getMagnitude() {
        return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2));
    }

    /**
     * Get the direction of the vector formed by the joystick and its origin
     * in radians
     *
     * @return The direction of the vector in radians
     */
    public double getDirectionRadians() {
        return Math.atan2(getX(), -getY());
    }

    /**
     * Get the direction of the vector formed by the joystick and its origin
     * in degrees
     *
     * uses acos(-1) to represent Pi due to absence of readily accessable Pi
     * constant in C++
     *
     * @return The direction of the vector in degrees
     */
    public double getDirectionDegrees() {
        return Math.toDegrees(getDirectionRadians());
    }

    /**
     * Get the channel currently associated with the specified axis.
     *
     * @param axis The axis to look up the channel for.
     * @return The channel fr the axis.
     */
    public int getAxisChannel(AxisType axis) {
        return d_axes[axis.value];
    }

    /**
     * Set the channel associated with a specified axis.
     *
     * @param axis The axis to set the channel for.
     * @param channel The channel to set the axis to.
     */
    public void setAxisChannel(AxisType axis, int channel) {
        d_axes[axis.value] = (byte) channel;
    }
    
    /**
     * Set the rumble output for the joystick. The DS currently supports 2 rumble values,
     * left rumble and right rumble
     * @param type Which rumble value to set
     * @param value The normalized value (0 to 1) to set the rumble to
     */
    public void setRumble(RumbleType type, float value) {
        if (value < 0)
            value = 0;
        else if (value > 1)
            value = 1;
        if (type.value == RumbleType.kLeftRumble_val)
            d_leftRumble = (short)(value*65535);
        else
            d_rightRumble = (short)(value*65535);
        FRCNetworkCommunicationsLibrary.HALSetJoystickOutputs((byte)d_port, d_outputs, d_leftRumble, d_rightRumble);
    }

    /**
     * Set a single HID output value for the joystick.
     * @param outputNumber The index of the output to set (1-32)
     * @param value The value to set the output to
     */
        
    public void setOutput(int outputNumber, boolean value) {
        d_outputs = (d_outputs & ~(1 << (outputNumber-1))) | ((value?1:0) << (outputNumber-1));
        FRCNetworkCommunicationsLibrary.HALSetJoystickOutputs((byte)d_port, d_outputs, d_leftRumble, d_rightRumble);
    }

    /**
     * Set all HID output values for the joystick.
     * @param value The 32 bit output value (1 bit for each output)
     */
    public void setOutputs(int value) {
        d_outputs = value;
        FRCNetworkCommunicationsLibrary.HALSetJoystickOutputs((byte)d_port, d_outputs, d_leftRumble, d_rightRumble);
    }
}
