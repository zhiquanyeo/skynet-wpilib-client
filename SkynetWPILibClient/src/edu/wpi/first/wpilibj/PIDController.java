package edu.wpi.first.wpilibj;

import java.util.TimerTask;

import edu.wpi.first.wpilibj.livewindow.LiveWindowSendable;
import edu.wpi.first.wpilibj.tables.ITable;
import edu.wpi.first.wpilibj.tables.ITableListener;
import edu.wpi.first.wpilibj.util.BoundaryException;

public class PIDController implements LiveWindowSendable, Controller {
	
	public static final double kDefaultPeriod = 0.05;
	private static int instances = 0;
	
	private double d_P;
	private double d_I;
	private double d_D;
	private double d_F;
	
	private double d_maximumOutput = 1.0;
	private double d_minimumOutput = -1.0;
	private double d_maximumInput = 0.0;
	private double d_minimumInput = 0.0;
	
	private boolean d_continuous = false;
	private boolean d_enabled = false;
	
	private double d_prevError = 0.0;
	private double d_totalError = 0.0;
	
	private Tolerance d_tolerance;
	private double d_setpoint = 0.0;
	private double d_error = 0.0;
	private double d_result = 0.0;
	private double d_period = kDefaultPeriod;
	
	PIDSource d_pidInput;
	PIDOutput d_pidOutput;
	
	java.util.Timer d_controlLoop;
	
	private boolean d_freed = false;
	
	/**
     * Tolerance is the type of tolerance used to specify if the PID controller is on target.
     * The various implementations of this class such as PercentageTolerance and AbsoluteTolerance
     * specify types of tolerance specifications to use.
     */
    public interface Tolerance {
        public boolean onTarget();
    }

    public class PercentageTolerance implements Tolerance {
        double percentage;

        PercentageTolerance(double value) {
            percentage = value;
        }

        @Override
        public boolean onTarget() {
            return (Math.abs(getError()) < percentage / 100
                    * (d_maximumInput - d_minimumInput));
        }
    }

    public class AbsoluteTolerance implements Tolerance {
        double value;

        AbsoluteTolerance(double value) {
            this.value = value;
        }

        @Override
        public boolean onTarget() {
            return Math.abs(getError()) < value;
        }
    }

    public class NullTolerance implements Tolerance {

        @Override
        public boolean onTarget() {
            throw new RuntimeException("No tolerance value set when using PIDController.onTarget()");
        }
    }

    private class PIDTask extends TimerTask {

        private PIDController d_controller;

        public PIDTask(PIDController controller) {
            if (controller == null) {
                throw new NullPointerException("Given PIDController was null");
            }
            d_controller = controller;
        }

        @Override
        public void run() {
            d_controller.calculate();
        }
    }
    
    /**
     * Allocate a PID object with the given constants for P, I, D, and F
     * @param Kp the proportional coefficient
     * @param Ki the integral coefficient
     * @param Kd the derivative coefficient
     * @param Kf the feed forward term
     * @param source The PIDSource object that is used to get values
     * @param output The PIDOutput object that is set to the output percentage
     * @param period the loop time for doing calculations. This particularly effects calculations of the
     * integral and differential terms. The default is 50ms.
     */
    public PIDController(double Kp, double Ki, double Kd, double Kf,
                         PIDSource source, PIDOutput output,
                         double period) {

        if (source == null) {
            throw new NullPointerException("Null PIDSource was given");
        }
        if (output == null) {
            throw new NullPointerException("Null PIDOutput was given");
        }

        d_controlLoop = new java.util.Timer();


        d_P = Kp;
        d_I = Ki;
        d_D = Kd;
        d_F = Kf;

        d_pidInput = source;
        d_pidOutput = output;
        d_period = period;

        d_controlLoop.schedule(new PIDTask(this), 0L, (long) (d_period * 1000));

        instances++;
        HLUsageReporting.reportPIDController(instances);
        d_tolerance = new NullTolerance();
    }

    /**
     * Allocate a PID object with the given constants for P, I, D and period
     * @param Kp the proportional coefficient
     * @param Ki the integral coefficient
     * @param Kd the derivative coefficient
     * @param source the PIDSource object that is used to get values
     * @param output the PIDOutput object that is set to the output percentage
     * @param period the loop time for doing calculations. This particularly effects calculations of the
     * integral and differential terms. The default is 50ms.
     */
    public PIDController(double Kp, double Ki, double Kd,
                         PIDSource source, PIDOutput output,
                         double period) {
        this(Kp, Ki, Kd, 0.0, source, output, period);
    }

    /**
     * Allocate a PID object with the given constants for P, I, D, using a 50ms period.
     * @param Kp the proportional coefficient
     * @param Ki the integral coefficient
     * @param Kd the derivative coefficient
     * @param source The PIDSource object that is used to get values
     * @param output The PIDOutput object that is set to the output percentage
     */
    public PIDController(double Kp, double Ki, double Kd,
                         PIDSource source, PIDOutput output) {
        this(Kp, Ki, Kd, source, output, kDefaultPeriod);
    }

    /**
     * Allocate a PID object with the given constants for P, I, D, using a 50ms period.
     * @param Kp the proportional coefficient
     * @param Ki the integral coefficient
     * @param Kd the derivative coefficient
     * @param Kf the feed forward term
     * @param source The PIDSource object that is used to get values
     * @param output The PIDOutput object that is set to the output percentage
     */
    public PIDController(double Kp, double Ki, double Kd, double Kf,
                         PIDSource source, PIDOutput output) {
        this(Kp, Ki, Kd, Kf, source, output, kDefaultPeriod);
    }

    /**
     * Free the PID object
     */
    public void free() {
      d_controlLoop.cancel();
      synchronized (this) {
        d_freed = true;
        d_pidOutput = null;
        d_pidInput = null;
        d_controlLoop = null;
      }
      if(this.table!=null) table.removeTableListener(listener);
    }

    /**
     * Read the input, calculate the output accordingly, and write to the output.
     * This should only be called by the PIDTask
     * and is created during initialization.
     */
    private void calculate() {
        boolean enabled;
        PIDSource pidInput;

        synchronized (this) {
            if (d_pidInput == null) {
                return;
            }
            if (d_pidOutput == null) {
                return;
            }
            enabled = d_enabled; // take snapshot of these values...
            pidInput = d_pidInput;
        }

        if (enabled) {
          double input;
            double result;
            PIDOutput pidOutput = null;
            synchronized (this){
              input = pidInput.pidGet();
            }
            synchronized (this) {
                d_error = d_setpoint - input;
                if (d_continuous) {
                    if (Math.abs(d_error)
                            > (d_maximumInput - d_minimumInput) / 2) {
                        if (d_error > 0) {
                            d_error = d_error - d_maximumInput + d_minimumInput;
                        } else {
                            d_error = d_error
                                      + d_maximumInput - d_minimumInput;
                        }
                    }
                }

                if (d_I != 0) {
                    double potentialIGain = (d_totalError + d_error) * d_I;
                    if (potentialIGain < d_maximumOutput) {
                        if (potentialIGain > d_minimumOutput) {
                            d_totalError += d_error;
                        } else {
                            d_totalError = d_minimumOutput / d_I;
                        }
                    } else {
                        d_totalError = d_maximumOutput / d_I;
                    }
                }

                d_result = d_P * d_error + d_I * d_totalError + d_D * (d_error - d_prevError) + d_setpoint * d_F;
                d_prevError = d_error;

                if (d_result > d_maximumOutput) {
                    d_result = d_maximumOutput;
                } else if (d_result < d_minimumOutput) {
                    d_result = d_minimumOutput;
                }
                pidOutput = d_pidOutput;
                result = d_result;
            }

            pidOutput.pidWrite(result);
        }
    }

    /**
     * Set the PID Controller gain parameters.
     * Set the proportional, integral, and differential coefficients.
     * @param p Proportional coefficient
     * @param i Integral coefficient
     * @param d Differential coefficient
     */
    public synchronized void setPID(double p, double i, double d) {
        d_P = p;
        d_I = i;
        d_D = d;

        if (table != null) {
            table.putNumber("p", p);
            table.putNumber("i", i);
            table.putNumber("d", d);
        }
    }

    /**
    * Set the PID Controller gain parameters.
    * Set the proportional, integral, and differential coefficients.
    * @param p Proportional coefficient
    * @param i Integral coefficient
    * @param d Differential coefficient
    * @param f Feed forward coefficient
    */
    public synchronized void setPID(double p, double i, double d, double f) {
        d_P = p;
        d_I = i;
        d_D = d;
        d_F = f;

        if (table != null) {
            table.putNumber("p", p);
            table.putNumber("i", i);
            table.putNumber("d", d);
            table.putNumber("f", f);
        }
    }

    /**
     * Get the Proportional coefficient
     * @return proportional coefficient
     */
    public synchronized double getP() {
        return d_P;
    }

    /**
     * Get the Integral coefficient
     * @return integral coefficient
     */
    public synchronized double getI() {
        return d_I;
    }

    /**
     * Get the Differential coefficient
     * @return differential coefficient
     */
    public synchronized double getD() {
        return d_D;
    }

    /**
     * Get the Feed forward coefficient
     * @return feed forward coefficient
     */
    public synchronized double getF() {
        return d_F;
    }

    /**
     * Return the current PID result
     * This is always centered on zero and constrained the the max and min outs
     * @return the latest calculated output
     */
    public synchronized double get() {
        return d_result;
    }

    /**
     *  Set the PID controller to consider the input to be continuous,
     *  Rather then using the max and min in as constraints, it considers them to
     *  be the same point and automatically calculates the shortest route to
     *  the setpoint.
     * @param continuous Set to true turns on continuous, false turns off continuous
     */
    public synchronized void setContinuous(boolean continuous) {
        d_continuous = continuous;
    }

    /**
     *  Set the PID controller to consider the input to be continuous,
     *  Rather then using the max and min in as constraints, it considers them to
     *  be the same point and automatically calculates the shortest route to
     *  the setpoint.
     */
    public synchronized void setContinuous() {
        this.setContinuous(true);
    }

    /**
     * Sets the maximum and minimum values expected from the input and setpoint.
     *
     * @param minimumInput the minimum value expected from the input
     * @param maximumInput the maximum value expected from the input
     */
    public synchronized void setInputRange(double minimumInput, double maximumInput) {
        if (minimumInput > maximumInput) {
            throw new BoundaryException("Lower bound is greater than upper bound");
        }
        d_minimumInput = minimumInput;
        d_maximumInput = maximumInput;
        setSetpoint(d_setpoint);
    }

    /**
     * Sets the minimum and maximum values to write.
     *
     * @param minimumOutput the minimum percentage to write to the output
     * @param maximumOutput the maximum percentage to write to the output
     */
    public synchronized void setOutputRange(double minimumOutput, double maximumOutput) {
        if (minimumOutput > maximumOutput) {
            throw new BoundaryException("Lower bound is greater than upper bound");
        }
        d_minimumOutput = minimumOutput;
        d_maximumOutput = maximumOutput;
    }

    /**
     * Set the setpoint for the PIDController
     * @param setpoint the desired setpoint
     */
    public synchronized void setSetpoint(double setpoint) {
        if (d_maximumInput > d_minimumInput) {
            if (setpoint > d_maximumInput) {
                d_setpoint = d_maximumInput;
            } else if (setpoint < d_minimumInput) {
                d_setpoint = d_minimumInput;
            } else {
                d_setpoint = setpoint;
            }
        } else {
            d_setpoint = setpoint;
        }

        if (table != null)
            table.putNumber("setpoint", d_setpoint);
    }

    /**
     * Returns the current setpoint of the PIDController
     * @return the current setpoint
     */
    public synchronized double getSetpoint() {
        return d_setpoint;
    }

    /**
     * Returns the current difference of the input from the setpoint
     * @return the current error
     */
    public synchronized double getError() {
        //return d_error;
        return getSetpoint() - d_pidInput.pidGet();
    }

    /**
     * Set the percentage error which is considered tolerable for use with
     * OnTarget. (Input of 15.0 = 15 percent)
     * @param percent error which is tolerable
     * @deprecated Use {@link #setPercentTolerance(double)} or {@link #setAbsoluteTolerance(double)} instead.
     */
    @Deprecated
    public synchronized void setTolerance(double percent) {
        d_tolerance = new PercentageTolerance(percent);
    }

    /** Set the PID tolerance using a Tolerance object.
     * Tolerance can be specified as a percentage of the range or as an absolute
     * value. The Tolerance object encapsulates those options in an object. Use it by
     * creating the type of tolerance that you want to use: setTolerance(new PIDController.AbsoluteTolerance(0.1))
     * @param tolerance a tolerance object of the right type, e.g. PercentTolerance
     * or AbsoluteTolerance
     */
    private synchronized void setTolerance(Tolerance tolerance) {
        d_tolerance = tolerance;
    }

    /**
     * Set the absolute error which is considered tolerable for use with
     * OnTarget.
     * @param absvalue absolute error which is tolerable in the units of the input object
     */
    public synchronized void setAbsoluteTolerance(double absvalue) {
        d_tolerance = new AbsoluteTolerance(absvalue);
    }

    /**
     * Set the percentage error which is considered tolerable for use with
     * OnTarget. (Input of 15.0 = 15 percent)
     * @param percentage percent error which is tolerable
     */
    public synchronized void setPercentTolerance(double percentage) {
        d_tolerance = new PercentageTolerance(percentage);
    }

    /**
     * Return true if the error is within the percentage of the total input range,
     * determined by setTolerance. This assumes that the maximum and minimum input
     * were set using setInput.
     * @return true if the error is less than the tolerance
     */
    public synchronized boolean onTarget() {
        return d_tolerance.onTarget();
    }

    /**
     * Begin running the PIDController
     */
    @Override
    public synchronized void enable() {
        d_enabled = true;

        if (table != null) {
            table.putBoolean("enabled", true);
        }
    }

    /**
     * Stop running the PIDController, this sets the output to zero before stopping.
     */
    @Override
    public synchronized void disable() {
        d_pidOutput.pidWrite(0);
        d_enabled = false;

        if (table != null) {
            table.putBoolean("enabled", false);
        }
    }

    /**
     * Return true if PIDController is enabled.
     */
    public synchronized boolean isEnable() {
        return d_enabled;
    }

    /**
     * Reset the previous error,, the integral term, and disable the controller.
     */
    public synchronized void reset() {
        disable();
        d_prevError = 0;
        d_totalError = 0;
        d_result = 0;
    }

    @Override
    public String getSmartDashboardType() {
        return "PIDController";
    }

    private final ITableListener listener = new ITableListener() {
        @Override
    public void valueChanged(ITable table, String key, Object value, boolean isNew) {
            if (key.equals("p") || key.equals("i") || key.equals("d") || key.equals("f")) {
                if (getP() != table.getNumber("p", 0.0) || getI() != table.getNumber("i", 0.0) ||
                        getD() != table.getNumber("d", 0.0) || getF() != table.getNumber("f", 0.0))
                    setPID(table.getNumber("p", 0.0), table.getNumber("i", 0.0), table.getNumber("d", 0.0), table.getNumber("f", 0.0));
            } else if (key.equals("setpoint")) {
                if (getSetpoint() != ((Double) value).doubleValue())
                    setSetpoint(((Double) value).doubleValue());
            } else if (key.equals("enabled")) {
                if (isEnable() != ((Boolean) value).booleanValue()) {
                    if (((Boolean) value).booleanValue()) {
                        enable();
                    } else {
                        disable();
                    }
                }
            }
        }
    };
    private ITable table;
    @Override
    public void initTable(ITable table) {
        if(this.table!=null)
            this.table.removeTableListener(listener);
        this.table = table;
        if(table!=null) {
            table.putNumber("p", getP());
            table.putNumber("i", getI());
            table.putNumber("d", getD());
            table.putNumber("f", getF());
            table.putNumber("setpoint", getSetpoint());
            table.putBoolean("enabled", isEnable());
            table.addTableListener(listener, false);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ITable getTable() {
        return table;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTable() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startLiveWindowMode() {
        disable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stopLiveWindowMode() {
    }
}
