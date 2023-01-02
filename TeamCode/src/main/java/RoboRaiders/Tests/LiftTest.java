package RoboRaiders.Tests;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

import RoboRaiders.Robot.TestRobot;

@TeleOp(name = "Test Lift", group = "Test Teleop")


public class LiftTest extends OpMode {

    TestRobot LiftTestBot = new TestRobot();

    @Override
    public void init() {

        LiftTestBot.initialize(hardwareMap);
        LiftTestBot.resetIMU();
        telemetry.addData("robot initialised, waiting for command", true);
        telemetry.update();

    }

    @Override
    public void loop() {

        double power = gamepad2.left_stick_y;
        LiftTestBot.setLiftPower(-power);


    }
}


