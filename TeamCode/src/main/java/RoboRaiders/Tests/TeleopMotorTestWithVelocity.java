package RoboRaiders.Tests;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import RoboRaiders.Robot.MotorBot;

//----------------------------------------------------------------------------------------------
// Static Test Teleop - tests the usage of Static variable in the RoboRaidersProperties class.
// This opmode will get the static variable.
//----------------------------------------------------------------------------------------------
@TeleOp(name="Teleop Motor Test w/Velocity", group="Test Teleops")

public class TeleopMotorTestWithVelocity extends OpMode {

    public MotorBot mBot = new MotorBot();
    boolean dPadUp, dPadDown, dPadLeft, dPadRight;
    double lastEncoderPosition;
    double encoderCounts;
    double distanceToTravel;
    boolean runSomeMore = true;
    boolean firstTime = true;
    boolean busy = true;



    @Override
    public void init() {

        // Initialize mBot and tell user that the robot is initialized
        mBot.initialize(hardwareMap);
        mBot.resetIMU();
    }

    @Override
    public void init_loop() {
        telemetry.addData("- init_loop - mBot.aMotor.encoderCount: ",mBot.getEncoderCounts());
    }

    @Override
    public void loop() {

        dPadUp    = gamepad1.dpad_up;
        dPadDown  = gamepad1.dpad_down;
        dPadLeft  = gamepad1.dpad_left;
        dPadRight = gamepad1.dpad_right;

//        telemetry.addData("dPadUp:    ",dPadUp);
//        telemetry.addData("dPadDown:  ",dPadDown);
//        telemetry.addData("dPadLeft:  ",dPadLeft);
//        telemetry.addData("dPadRight: ",dPadRight);

 //       if (dPadLeft) {
        telemetry.addData("runSomeMore: ",runSomeMore);
        telemetry.addData("firstTime: ",firstTime);
        telemetry.addData("isBusy: ",mBot.aMotor.isBusy());
        telemetry.addData("busy: ",busy);

        telemetry.addData("mBot.aMotor.target position: ",mBot.aMotor.getCurrentPosition());
        telemetry.addData("velocity: ",mBot.aMotor.getVelocity());
        telemetry.addData("encoderCounts: ",encoderCounts);

        if (firstTime) {
            distanceToTravel = 2.51 / 8.0;
            encoderCounts = -mBot.turretCalculateCounts(distanceToTravel);
            telemetry.addData("encoderCounts: ",encoderCounts);
            telemetry.addData("mBot.aMotor.encoderCount: ",mBot.getEncoderCounts());
            telemetry.addData("velocity: ",mBot.aMotor.getVelocity());

            mBot.setTargetPosition((int)encoderCounts);
            mBot.runWithEncodersSTP();
            mBot.setDriveMotorVelocity(200.0);
            firstTime = false;
        }
        if (runSomeMore) {

            telemetry.addData("isBusy: ",mBot.aMotor.isBusy());
            telemetry.addData("busy: ",busy);

            telemetry.addData("mBot.aMotor.target position: ",mBot.aMotor.getCurrentPosition());
            telemetry.addData("velocity: ",mBot.aMotor.getVelocity());
            telemetry.addData("encoderCounts: ",encoderCounts);

            if (!mBot.aMotor.isBusy()) {

                busy = false;
                mBot.setDriveMotorVelocity(0.0);   // we are done!!!!
             //   mBot.runWithoutEncoders();
                mBot.resetEncoders();
                runSomeMore = false;
            }
        }
        else {
            telemetry.addData("isBusy: ",mBot.aMotor.isBusy());
            telemetry.addData("mBot.aMotor.target position: ",mBot.aMotor.getCurrentPosition());
            telemetry.addData("velocity: ",mBot.aMotor.getVelocity());
            telemetry.addData("encoderCounts: ",encoderCounts);
            telemetry.addData("mBot.aMotor.target position: ",mBot.aMotor.getTargetPositionTolerance());
            telemetry.addData("mBot.aMotor.encoderCount: ",mBot.getEncoderCounts());
        }
    }

}
