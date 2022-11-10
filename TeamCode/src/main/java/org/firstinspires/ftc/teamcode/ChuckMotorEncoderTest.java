/*
 * Copyright (c) 2021 OpenFTC Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous
//@disable
public class ChuckMotorEncoderTest extends LinearOpMode
{
    double power = 0.20;
    public DcMotor lFMotor = null;
    public DcMotor rFMotor = null;
    public DcMotor lRMotor = null;
    public DcMotor rRMotor = null;

    //----------------------------------------------------------------------------------------------
    // Main logic
    //----------------------------------------------------------------------------------------------

    @Override

    public void runOpMode() throws InterruptedException {

        // Initialize stevesRobot and tell user that the robot is initialized
        //robot.initialize(hardwareMap);
        //telemetry.addData("Robot Initialized waiting your command", true);
        //telemetry.update();

        //telemetry.addData("Status", "Initialized");
        //telemetry.update();




        // Define and initialize motors
        lFMotor = hardwareMap.get(DcMotor.class, "lFMotor");
        rFMotor = hardwareMap.get(DcMotor.class, "rFMotor");
        lRMotor = hardwareMap.get(DcMotor.class, "lRMotor");
        rRMotor = hardwareMap.get(DcMotor.class, "rRMotor");
        // Wait for start to be pushed
        waitForStart();
        runWithEncoders();
        //setDriveMotorPower(-power, power, -power, power);
        setDriveMotorPower(power,power,power,power);

        while (opModeIsActive()) {

            telemetry.addLine().addData("Back Left Encoder Count: ", getBackLeftDriveEncoderCounts());
            telemetry.addLine().addData("Back Right Encoder Count: ", getBackRightDriveEncoderCounts());
            telemetry.addLine().addData("Front Left Encoder Count: ", getFrontLeftDriveEncoderCounts());
            telemetry.addLine().addData("Front Right Encoder Count: ", getFrontRightDriveEncoderCounts());
            telemetry.addLine().addData("Power applied: ", power);
            telemetry.update();
        }
        setDriveMotorPower(0, 0, 0, 0);
        telemetry.addData("setting power to zero", true);

    }
    /**
     * This method will set the mode of all of the drive train motors to run using encoder
     */
    public void runWithEncoders() {

        lFMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rFMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lRMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rRMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }
    /**
     * This method will set the power for the drive motors
     *
     * @param leftFront  power setting for the left front motor
     * @param rightFront power setting for the right front motor
     * @param leftBack   power setting for the left back motor
     * @param rightBack  power setting for the right back motor
     */
    public void setDriveMotorPower(double leftFront, double rightFront, double leftBack, double rightBack) {

        lFMotor.setPower(leftFront);
        rFMotor.setPower(rightBack);
        lRMotor.setPower(leftBack);
        rRMotor.setPower(rightFront);

    }
    /**
     * These methods will get individual encoder position from any of the drive train motors
     * @return the encoder position
     */
    public double getBackLeftDriveEncoderCounts() { return lRMotor.getCurrentPosition(); }
    public double getBackRightDriveEncoderCounts() { return rRMotor.getCurrentPosition(); }
    public double getFrontLeftDriveEncoderCounts() { return lFMotor.getCurrentPosition(); }
    public double getFrontRightDriveEncoderCounts() { return rFMotor.getCurrentPosition(); }


}
