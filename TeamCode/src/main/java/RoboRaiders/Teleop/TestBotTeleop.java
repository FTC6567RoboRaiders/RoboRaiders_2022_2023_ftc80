package RoboRaiders.Teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import RoboRaiders.Utilities.Logger.Logger;
import RoboRaiders.Properties.RoboRaidersProperties;
import RoboRaiders.Robot.TestRobot;
import RoboRaiders.Utilities.RRStopWatch.RRStopWatch;

// This line establishes this op mode as a teleop op mode and allows for it to be displayed
// in the drop down list on the Driver Station phone to be chosen to run.
@TeleOp (name="Steve's TestBot Teleop", group="Test Teleops")

public class TestBotTeleop extends OpMode {



    enum tState {
        turret_start,
        turret_turning,
        turret_deposit,
        turret_returning,
        turret_returningHome
    }
    enum lState{
        lift_start,
        lift_extending,
        lift_deposit,
        lift_retract,


    }

    double turret_home = 0.0;
    double turret_right = 100.0; // 1/4 of a turn
    double turret_left = -100.0; // 1/4 of a turn
    double turret_back = 190.0; // 1/2 of a turn
    double turretFinalPosition;

    double lift_ground = 0.0;
    double lift_high = 600.0;
    double lift_middle = 400.0;
    double lift_low = 200.0;
    double liftFinalPosition;

    // Create an instance of the TestRobot and store it into StevesRobot
    public TestRobot stevesRobot = new TestRobot();
    public Logger myLogger =  new Logger("TestBotTeleop");
    public Logger dtLogger = new Logger("DT");   // Drive train logger

    tState turretState = tState.turret_start;
    lState liftState = lState.lift_start;
    public RRStopWatch myStopWatch = new RRStopWatch();
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

        /**
         * To Do: Move the drive code into separate method, could also move turret, lift and
         * grabber code into separate methods.
         */


        boolean leftBumper = gamepad2.left_bumper;
        boolean rightBumper = gamepad2.right_bumper;



        doTurret();
        doLift();
        doDrive();



//
//        myLogger.Debug("botheading ", botHeading);
//        myLogger.Debug("x / y / rx ", x, y, rx);
//        myLogger.Debug("rotX / rotY ", rotX, rotY);
//        myLogger.Debug("frontLeftPower / backLeftPower / frontRightPower / backRightPower ",
//                frontLeftPower,
//                backLeftPower,
//                frontRightPower,
//                backRightPower);
//        myLogger.Debug("Y Button", yButton);

        /**
         * To Do: Add some kind of button push to toggle or change the maximum speed of robot
         */


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
    public void doTurret(){
        switch(turretState){
            case turret_start:
                myLogger.Debug("STARTHERE,STARTHERE,STARTHERE,STARTHERE");
                myLogger.Debug("turretState: "+turretState);
                myLogger.Debug("gamepad2.dpad_right"+gamepad2.dpad_right);
                myLogger.Debug("gamepad2.dpad_left"+gamepad2.dpad_left);
                myLogger.Debug("gamepad2.dpad_down"+gamepad2.dpad_down);
                if(Math.abs(stevesRobot.getLiftEncoderCounts() - liftFinalPosition) > 5.0){
                    if (gamepad2.b) {

                        stevesRobot.setTurretMotorTargetPosition(turret_right);
                        turretFinalPosition = turret_right;
                        turretState = tState.turret_turning;
                        stevesRobot.turretRunWithEncodersSTP();
                        stevesRobot.setTurretMotorVelocity(500.0);

                    }

                    else if (gamepad2.x) {

                        stevesRobot.setTurretMotorTargetPosition(turret_left);
                        turretFinalPosition = turret_left;
                        turretState = tState.turret_turning;
                        stevesRobot.turretRunWithEncodersSTP();
                        stevesRobot.setTurretMotorVelocity(500.0);

                    }

                    else if (gamepad2.a) {

                        stevesRobot.setTurretMotorTargetPosition(turret_back);
                        turretFinalPosition = turret_back;
                        turretState = tState.turret_turning;
                        stevesRobot.turretRunWithEncodersSTP();
                        stevesRobot.setTurretMotorVelocity(500.0);

                    }

                    else {

                        stevesRobot.setTurretMotorPower(0.5 * gamepad2.left_stick_x);

                    }
                }


                break;

            case turret_turning:
//                myLogger.Debug("turretState: "+turretState);
//                myLogger.Debug("TEC: " + stevesRobot.getTurretEncoderCounts());

                if(Math.abs(stevesRobot.getTurretEncoderCounts() - turretFinalPosition) < 5.0) {
                    stevesRobot.setTurretMotorVelocity(0.0);
                    turretState = tState.turret_deposit;
                }

                break;

            case turret_deposit:
                if(gamepad2.right_stick_button && liftState == lState.lift_deposit) {
                    myStopWatch.startTime();
                    stevesRobot.setTurretMotorPower(0.0);
                    stevesRobot.setinTakeServoPosition(1.0);
                    turretState = tState.turret_returning;

                }
                else {
                    stevesRobot.setTurretMotorPower(0.5 * gamepad2.left_stick_x);

                }
//                if(gamepad2.right_stick_button) {
//                    stevesRobot.setTurretMotorTargetPosition(turret_home);
//                    stevesRobot.setTurretMotorVelocity(500.0);
//                    myStopWatch.startTime();
//                    turretState = tState.turret_returningHome;
//                    break;
//                }


                break;
            case turret_returning:
//                myLogger.Debug("turretState: "+ turretState);
//                myLogger.Debug("Y: " + gamepad2.y);
                telemetry.addData("elapsed time: ", myStopWatch.getElaspedTime());
                telemetry.update();
                if(myStopWatch.getElaspedTime() >= 5.0){
                    stevesRobot.setTurretMotorTargetPosition(turret_home);
                    stevesRobot.setTurretMotorVelocity(500.0);
                    turretState = tState.turret_returningHome;
                }

                break;

            case turret_returningHome:
//                myLogger.Debug("turretState: "+turretState);
//                myLogger.Debug("TEC: " + stevesRobot.getSortedEncoderCount());

                if(Math.abs(stevesRobot.getTurretEncoderCounts() - turret_home) < 5.0) {

                    stevesRobot.setTurretMotorVelocity(0.0);
                    turretState = tState.turret_start;

                }
                break;

            default:
//                myLogger.Debug("turretState: "+turretState);
                turretState = tState.turret_start;
                break;

        }
        if(gamepad2.y && turretState != tState.turret_start) {

            stevesRobot.setTurretMotorVelocity(0.0);
            turretState = tState.turret_start;

        }
    }
    public void doLift(){
        switch(liftState){
            case lift_start:
                myLogger.Debug("STARTHERE,STARTHERE,STARTHERE,STARTHERE");
                myLogger.Debug("turretState: "+turretState);
                myLogger.Debug("gamepad2.dpad_right"+gamepad2.dpad_right);
                myLogger.Debug("gamepad2.dpad_left"+gamepad2.dpad_left);
                myLogger.Debug("gamepad2.dpad_down"+gamepad2.dpad_down);

                if (gamepad2.dpad_down) {
                    stevesRobot.setLiftMotorTargetPosition(lift_low);
                    liftFinalPosition = lift_low;
                    liftState = lState.lift_extending;
                    stevesRobot.liftRunWithEncodersSTP();
                    stevesRobot.setLiftMotorVelocity(500.0);
                }

                else if (gamepad2.dpad_left) {
                    stevesRobot.setLiftMotorTargetPosition(lift_ground);
                    liftFinalPosition = lift_low;
                    liftState = lState.lift_extending;
                    stevesRobot.liftRunWithEncodersSTP();
                    stevesRobot.setLiftMotorVelocity(500.0);

                }

                else if (gamepad2.dpad_right) {

                    stevesRobot.setLiftMotorTargetPosition(lift_middle);
                    liftFinalPosition = lift_low;
                    liftState = lState.lift_extending;
                    stevesRobot.liftRunWithEncodersSTP();
                    stevesRobot.setLiftMotorVelocity(500.0);

                    }

                else if (gamepad2.dpad_up) {
                    stevesRobot.setLiftMotorTargetPosition(lift_high);
                    liftFinalPosition = lift_low;
                    stevesRobot.liftRunWithEncodersSTP();
                    stevesRobot.setLiftMotorVelocity(500.0);
                    liftState = lState.lift_extending;
                }
                else {
                    stevesRobot.setLiftMotorPower(0.5 * (-gamepad2.right_stick_y));
                }

                break;


            case lift_extending:
                if(Math.abs(stevesRobot.getLiftEncoderCounts() - liftFinalPosition) < 5.0) {
                    stevesRobot.setLiftMotorVelocity(0.0);
                    liftState = lState.lift_deposit;
                }

                break;
            case lift_deposit:
                if(turretState == tState.turret_returningHome){
                    stevesRobot.setLiftMotorTargetPosition(lift_ground);
                    stevesRobot.setLiftMotorVelocity(500.0);
                    liftState = lState.lift_retract;
                }
                break;
            case lift_retract:
                if(Math.abs(stevesRobot.getLiftEncoderCounts() - liftFinalPosition) < 5.0) {
                    stevesRobot.setLiftMotorVelocity(0.0);
                    liftState = lState.lift_start;
                }
                break;
            default:
                liftState = lState.lift_start;
        }
    }
    public void doDrive(){
        //double autoHeading = RoboRaidersProperties.getHeading();
        // Read inverse IMU heading, as the IMU heading is CW positive

        double botHeading = stevesRobot.getHeading();

        double y = -gamepad1.left_stick_y; // Remember, this is reversed!`
        double x = gamepad1.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = gamepad1.right_stick_x;

        double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
        double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio, but only when
        // at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        double frontLeftPower = (rotY + rotX + rx) / denominator;
        double backLeftPower = (rotY - rotX + rx) / denominator;
        double frontRightPower = (rotY - rotX - rx) / denominator;
        double backRightPower = (rotY + rotX - rx) / denominator;
        double lTrigger = gamepad1.left_trigger;
        double rTrigger = gamepad1.right_trigger;

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

        if(lTrigger > 0.0){
            frontLeftPower = (frontLeftPower*0.65) - (0.2 * lTrigger);
            frontRightPower = (frontLeftPower*0.65) - (0.2 * lTrigger);
            backLeftPower = (frontLeftPower*0.65) - (0.2 * lTrigger);
            backRightPower = (frontLeftPower*0.65) - (0.2 * lTrigger);

        }

        else if(rTrigger > 0.0){
            frontLeftPower = (frontLeftPower*0.65) + (0.2 * lTrigger);
            frontRightPower = (frontLeftPower*0.65) + (0.2 * lTrigger);
            backLeftPower = (frontLeftPower*0.65) + (0.2 * lTrigger);
            backRightPower = (frontLeftPower*0.65) + (0.2 * lTrigger);

        }


        stevesRobot.setDriveMotorPower(
                frontLeftPower*0.65,
                frontRightPower*0.65,
                backLeftPower*0.65,
                backRightPower*0.65);
        //               dtLogger);
    }
}
