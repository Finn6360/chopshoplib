package com.chopshop166.chopshoplib.motors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleSupplier;

import com.chopshop166.chopshoplib.sensors.IEncoder;
import com.chopshop166.chopshoplib.sensors.MockEncoder;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;

/**
 * Use a PID controller with a generic speed controller.
 */
public class SwPIDMotorController extends SmartMotorController {

    /** The PID controller for calculations. */
    final private PIDController pid;
    /** The measurement source. */
    final private DoubleSupplier measurement;
    /** The watchdog to repeatedly trigger the calculation. */
    final private Watchdog dog = new Watchdog(1.0 / 50.0, this::calculatePID);
    /** The feedforward coefficient. */
    private double feedforward;
    /** The setpoint to move to. */
    private double setpoint;
    /** Whether the PID controller is enabled. */
    private boolean pidEnabled = true;
    /** List of PID Parameters that we can switch between. */
    final private List<PIDParams> configs = new ArrayList<>(4);

    /**
     * Class to represent PID parameters
     */
    static public class PIDParams {
        /** Factor for "proportional" control */
        private double kP;

        /** Factor for "integral" control */
        private double kI;

        /** Factor for "derivative" control */
        private double kD;

        /** Factor for "feedforward" control */
        private double kF;
    }

    /**
     * Use a PID controller with the position of an encoder.
     *
     * @param <T>     A type that's a Speed Controller and also Sendable.
     * @param motor   The speed controller to move.
     * @param pid     The PID controller to use for calculation.
     * @param encoder The encoder to use for measurement.
     * @return The new PID controller.
     */
    public static <T extends Sendable & MotorController> SwPIDMotorController position(final T motor,
            final PIDController pid, final IEncoder encoder) {
        return new SwPIDMotorController(motor, encoder, pid, encoder::getDistance);
    }

    /**
     * Use a PID controller with the velocity of an encoder.
     *
     * @param <T>     A type that's a Speed Controller and also Sendable.
     * @param motor   The speed controller to move.
     * @param pid     The PID controller to use for calculation.
     * @param encoder The encoder to use for measurement.
     * @return The new PID controller.
     */
    public static <T extends Sendable & MotorController> SwPIDMotorController velocity(final T motor,
            final PIDController pid, final IEncoder encoder) {
        return new SwPIDMotorController(motor, encoder, pid, encoder::getRate);
    }

    /**
     * Create a PID speed controller using software PID.
     *
     * @param <T>         The unwrapped type of a motor controller
     * @param motor       The motor controller to use.
     * @param pid         The PID controller for calculation.
     * @param measurement The measurement source.
     * @param modifiers   Any output modifiers.
     */
    public <T extends Sendable & MotorController> SwPIDMotorController(final T motor, final PIDController pid,
            final DoubleSupplier measurement, final Modifier... modifiers) {
        this(motor, new MockEncoder(), pid, measurement, modifiers);
    }

    /**
     * Create a PID speed controller using software PID.
     *
     * @param <T>         The unwrapped type of a motor controller
     * @param motor       The motor controller to use.
     * @param encoder     The encoder to use.
     * @param pid         The PID controller for calculation.
     * @param measurement The measurement source.
     * @param modifiers   Any output modifiers.
     */
    public <T extends Sendable & MotorController> SwPIDMotorController(final T motor, final IEncoder encoder,
            final PIDController pid, final DoubleSupplier measurement, final Modifier... modifiers) {
        super(motor, encoder, modifiers);
        this.measurement = measurement;
        this.pid = pid;
        dog.suppressTimeoutMessage(true);
        dog.enable();
    }

    /**
     * Get the PID controller.
     *
     * @return The PID controller.
     */
    public PIDController getController() {
        return pid;
    }

    /** Enable the PID controller. */
    public void enablePID() {
        if (!pidEnabled) {
            pidEnabled = true;
            dog.enable();
        }
    }

    /** Disable the PID controller. */
    public void disablePID() {
        pidEnabled = false;
        dog.disable();
    }

    /**
     * Add the default configuration to the list of configurations and update the
     * PIDController.
     *
     * @param config Configuration to add to the list of stored configs.
     */
    public void addDefaultConfiguration(final PIDParams config) {
        this.configs.add(config);
        this.feedforward = config.kF;
        this.pid.setPID(config.kP, config.kI, config.kD);
    }

    /**
     * Add a configuration to the list of configurations we can swap to.
     *
     * @param config Configuration to add to the list of stored configs.
     */
    public void addConfiguration(final PIDParams config) {
        this.configs.add(config);
    }

    @Override
    public void setSetpoint(final double setPoint) {
        pid.setSetpoint(setPoint);
        this.setpoint = setPoint;
    }

    @Override
    public void setPidSlot(final int slotId) {
        final var config = this.configs.get(slotId);
        this.feedforward = config.kF;
        this.pid.setPID(config.kP, config.kI, config.kD);
    }

    /** Calculate the PID value, and set the speed controler to the result. */
    private void calculatePID() {
        final double ff = feedforward * setpoint;
        final double meas = measurement.getAsDouble();
        final double calc = pid.calculate(meas);
        set(ff + calc);
        dog.reset();
    }
}