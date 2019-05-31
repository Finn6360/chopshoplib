package com.chopshop166.chopshoplib.sensors;

import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.SendableBase;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

/**
 * An {@link IEncoder} that can be controlled via the dashboard.
 */
public class MockEncoder extends SendableBase implements IEncoder {

    private PIDSourceType sourceType;
    private double distance;
    private double rate;

    @Override
    public void setPIDSourceType(final PIDSourceType pidSource) {
        this.sourceType = pidSource;
    }

    @Override
    public PIDSourceType getPIDSourceType() {
        return this.sourceType;
    }

    @Override
    public double pidGet() {
        switch (sourceType) {
        case kDisplacement:
            return getDistance();
        case kRate:
            return getRate();
        default:
            return Double.NaN;
        }
    }

    @Override
    public void reset() {
        this.distance = 0;
    }

    @Override
    public double getDistance() {
        return this.distance;
    }

    /**
     * Set the current distance the "encoder" has traveled.
     * 
     * @param distance the "encoder" has traveled
     */
    public void setDistance(final double distance) {
        this.distance = distance;
    }

    @Override
    public double getRate() {
        return this.rate;
    }

    /**
     * Sets the current rate of the "encoder" movement.
     * 
     * @param rate the "encoder" is moving at
     */
    public void setRate(final double rate) {
        this.rate = rate;
    }

    @Override
    public void initSendable(final SendableBuilder builder) {
        builder.setSmartDashboardType("Quadrature Encoder");
        builder.addDoubleProperty("Speed", this::getRate, this::setRate);
        builder.addDoubleProperty("Distance", this::getDistance, this::setDistance);
        builder.addDoubleProperty("Distance per Tick", () -> 0, null);
    }
}