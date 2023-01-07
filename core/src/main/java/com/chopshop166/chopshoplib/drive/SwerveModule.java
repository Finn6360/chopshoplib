package com.chopshop166.chopshoplib.drive;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.util.sendable.Sendable;

/**
 * Base interface for a swerve module.
 *
 * Contains information about rotation and velocity.
 */
public interface SwerveModule extends Sendable {

    /**
     * Get the angle of the swerve module.
     * 
     * @return An angle as a rotation object.
     */
    default Rotation2d getAngle() {
        return this.getState().angle;
    }

    /**
     * Get the distance travelled.
     * 
     * @return Meters per second.
     */
    double getDistance();

    /**
     * Set the inverted state of the module.
     * 
     * @param isInverted Whether the module is inverted.
     */
    void setInverted(boolean isInverted);

    /** Reset the encoder distance. */
    void resetDistance();

    /**
     * Get the modules location in relation to the CoM of the robot.
     *
     * @return Location2d object representing the offset
     */
    Translation2d getLocation();

    /**
     * Process the desired state and set the output values for the motor
     * controllers.
     *
     * @param desiredState The direction and speed.
     */
    void setDesiredState(SwerveModuleState desiredState);

    /**
     * Get the current state of the module.
     *
     * @return A SwerveModuleState object with the module's current state
     */
    SwerveModuleState getState();

}
