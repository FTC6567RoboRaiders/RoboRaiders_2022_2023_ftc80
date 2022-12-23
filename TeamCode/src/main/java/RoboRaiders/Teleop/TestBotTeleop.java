package RoboRaiders.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import RoboRaiders.Utilities.Logger.Logger;
import RoboRaiders.Properties.RoboRaidersProperties;
import RoboRaiders.Robot.TestRobot;

// This line establishes this op mode as a teleop op mode and allows for it to be displayed
// in the drop down list on the Driver Station phone to be chosen to run.
@TeleOp (name="Steve's TestBot Teleop", group="Test Teleops")

public class TestBotTeleop extends OpMode {



    enum tState {
        turret_start,
        turret_turning,
        turret_returning,
        turret_returningHome
    }
    double turret_home = 0.0;
    double turret_right = 54.0; // 1/4 of a turn
    double turret_left = 54.0; // 1/4 of a turn
    double turret_back = 108.0; // 1/2 of a turn
    double turretFinalPosition;

    // Create an instance of the TestRobot and store it into StevesRobot
    public TestRobot stevesRobot = new TestRobot();
    public Logger myLogger =  new Logger("TestBotTeleop");

    tState turretState = tState.turret_start;

    boolean yButton = false;

    @Override
    public void init() {

        // Initialize stevesRobot and tell user that the robot is initialized
        stevesRobot.initialize(hardwareMap);
        telemetry.addData("Robot Initialized waiting your command", true);
        telemetry.update();
    }


    @Override
    public void loop() {

        //double autoHeading = RoboRaidersProperties.getHeading();


        // Read inverse IMU heading, as the IMU heading is CW positive
        double botHeading = stevesRobot.getHeading();

        double y = -gamepad1.left_stick_y; // Remember, this is reversed!
        double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x;

        double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
        double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);





        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio, but only when
        // at least one is ocut of the range [-1, 1]

        switch(turretState){
            case turret_start:

                myLogger.Debug("turretState: "+turretState);
                myLogger.Debug("gamepad2.dpad_right"+gamepad2.dpad_right);
                myLogger.Debug("gamepad2.dpad_left"+gamepad2.dpad_left);
                myLogger.Debug("gamepad2.dpad_down"+gamepad2.dpad_down);

                if (gamepad2.dpad_right) {
                    stevesRobot.setTurretMotorTargetPosition(turret_right);
                    turretFinalPosition = turret_right;
                } else if (gamepad2.dpad_left) {
                    stevesRobot.setTurretMotorTargetPosition(turret_left);
                    turretFinalPosition = turret_left;
                } else if (gamepad2.dpad_down) {
                    stevesRobot.setTurretMotorTargetPosition(turret_back);
                    turretFinalPosition = turret_back;
                }

                if(gamepad2.dpad_left || gamepad2.dpad_right || gamepad2.dpad_down){
                    turretState = tState.turret_turning;
                    stevesRobot.turretRunWithEncodersSTP();
                    stevesRobot.setTurretMotorVelocity(500.0);
                }

                break;

            case turret_turning:
                myLogger.Debug("turretState: "+turretState);
                if(Math.abs(stevesRobot.getTurretEncoderCounts() - turretFinalPosition) < 10.0){
                    stevesRobot.setTurretMotorVelocity(0.0);
                    turretState = tState.turret_returning;
                }
                break;
            case turret_returning:
                myLogger.Debug("turretState: "+turretState);
                if(gamepad2.y){
                    stevesRobot.setTurretMotorTargetPosition(turret_home);
                    stevesRobot.setTurretMotorVelocity(500.0);
                    turretState = tState.turret_returningHome;
                }
            case turret_returningHome:
                myLogger.Debug("turretState: "+turretState);
                if(Math.abs(stevesRobot.getTurretEncoderCounts() - turret_home) < 10.0){
                    stevesRobot.setTurretMotorVelocity(0.0);
                    turretState = tState.turret_start;
                }
            default:
                myLogger.Debug("turretState: "+turretState);
                turretState = tState.turret_start;
                break;
        }
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (rotY + rotX + rx) / denominator;
        double backLeftPower = (rotY - rotX + rx) / denominator;
        double frontRightPower = (rotY - rotX - rx) / denominator;
        double backRightPower = (rotY + rotX - rx) / denominator;

        telemetry.addLine("MAKE SURE THE ARROWS ON MOTORS 1 AND 3 FACE THE DRIVER");
        telemetry.addLine("Variables");
        telemetry.addData("botHeading", String.valueOf(botHeading));
        telemetry.addData("y", String.valueOf(y));
        telemetry.addData("x", String.valueOf(x));
        telemetry.addData("rx", String.valueOf(rx));
        telemetry.addData("rotX", String.valueOf(rotX));
        telemetry.addData("rotY", String.valueOf(rotY));
        telemetry.addData("denominator", String.valueOf(denominator));
        telemetry.addData("frontLeftPower", String.valueOf(frontLeftPower));
        telemetry.addData("backLeftPower", String.valueOf(backLeftPower));
        telemetry.addData("frontRightPower", String.valueOf(frontRightPower));
        telemetry.addData("backRightPower", String.valueOf(backRightPower));
        telemetry.addData("auto heading: ", RoboRaidersProperties.getHeading());

        myLogger.Debug("botheading ", botHeading);
        myLogger.Debug("x / y / rx ", x, y, rx);
        myLogger.Debug("rotX / rotY ", rotX, rotY);
        myLogger.Debug("frontLeftPower / backLeftPower / frontRightPower / backRightPower ",
                frontLeftPower,
                backLeftPower,
                frontRightPower,
                backRightPower);
        myLogger.Debug("Y Button", yButton);

        stevesRobot.setDriveMotorPower(
                frontLeftPower,
                frontRightPower,
                backLeftPower,
                backRightPower);











    }
    /**
     * smoothPower will attempt to smooth or scale joystick input when driving the
     * robot in teleop mode.  By smoothing the joystick input more controlled movement
     * of the robot will occur, especially at lower speeds.
     * <br><br>
     * To scale the input, 16 values are used that increase in magnitude, the algorithm
     * will determine where the input value roughly falls in the array by multiplying it
     * by 16, then will use the corresponding array entry from the scaleArray variable to
     * return a scaled value.
     * <br><br>
     * <b>Example 1:</b> dVal (the input value or value passed to this method) is set to 0.76
     * <br>
     * Stepping through the algorithm
     * <ol>
     * <li> 0.76*16 = 12.16, but because we cast the calculations as an integer (int)
     * we lose the .16 so the value just is 12, variable index now contains 12.  <b>Note:</b>
     * the index variable will tell us which of the array entries in the scaleArray array to
     * use.</li>
     * <li> Check if the index is negative (less than zero), in this example the
     * variable index contains a positive 12</li>
     * <li> Check if the variable index is greater than 16, this is done so the
     * algorithm does not exceed the number of entries in the scaleArray array</li>
     * <li> Initialize the variable dScale to 0.0 (not really needed but we are
     * just being safe)</li>
     * <li> If dVal (value passed to this method) was initially negative, then
     * set the variable dScale to the negative of the scaleArray(index), in this example
     * dVal was initially 0.76 so not negative</li>
     * <li> If dVal (value passed to this method) was initially positive, then
     * set the variable dScale to the scaleArray(index), since index is 12, then
     * scaleArray(12) = 0.60.  <b>Remember, in java the first array index is 0,
     * this is why scaleArray(12) is not 0.50</b></li>
     * <li> Return the dScale value (0.60)</li>
     * </ol>
     * <p>
     * <br><br>
     * <b>Example 2</b> dVal (the input value or value passed to this method) is set to -0.43
     * <br>
     * Stepping through the algorithm
     * <ol>
     * <li> -0.43*16 = -6.88, but because we cast the calculations as an integer (int)
     * we lose the .88 so the value just is -6, variable index now contains -6.  <b>Note:</b>
     * the index variable will tell us which of the array entries in the scaleArray array to
     * use.</li>
     * <li> Check if the index is negative (less than zero), in this example the
     * variable index is negative, so make the negative a negative (essentially
     * multiplying the variable index by -1, the variable index now contains 6</li>
     * <li> Check if the variable index is greater than 16, this is done so the
     * algorithm does not exceed the number of entries in the scaleArray array</li>
     * <li> Initialize the variable dScale to 0.0 (not really needed but we are
     * just being safe)</li>
     * <li> If dVal (value passed to this method) was initially negative, then
     * set the variable dScale to the negative of the scaleArray(index), in this example
     * dVal was initially -0.43, so make sure to return a negative value of scaleArray(6).
     * scaleArray(6) is equal to 0.18 and the negative of that is -0.18 <b>Remember,
     * in java the first array index is 0, this is why scaleArray(6) is not 0.15</b></li>
     * <li> Return the dScale value (-0.18)</li>
     * </ol>
     *
     * @param dVal the value to be scaled -between -1.0 and 1.0
     * @return the scaled value
     * <B>Author(s)</B> Unknown - copied from internet
     */
    double smoothPower(double dVal) {
        // in the floats.
        double[] scaleArray = {0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00};
        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);
        // index should be positive.
        if (index < 0) {
            index = -index;
        }
        // index cannot exceed size of array minus 1.
        if (index > 16) {
            index = 16;
        }
        // get value from the array.
        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }
        // return scaled value.
        return dScale;
    }
}
