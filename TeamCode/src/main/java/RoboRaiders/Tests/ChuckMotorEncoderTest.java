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

package RoboRaiders.Tests;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import RoboRaiders.Properties.RoboRaidersProperties;
import RoboRaiders.Robot.TestRobot;

@Autonomous
//@disable
public class ChuckMotorEncoderTest extends LinearOpMode
{
    double power = 0.20;
    double heading;

    //----------------------------------------------------------------------------------------------
    // Main logic
    //----------------------------------------------------------------------------------------------

    @Override

    public void runOpMode() throws InterruptedException {

        TestRobot dogMan = new TestRobot();
        dogMan.initialize(hardwareMap);

        telemetry.addData("Robot Initialized waiting your command", true);
        telemetry.update();

        // Wait for start to be pushed
        waitForStart();
        dogMan.runWithEncoders();
        //setDriveMotorPower(-power, power, -power, power);
        dogMan.setDriveMotorPower(power, power, power, power);
        //setDriveMotorPower(power,power,power,power);

        while (opModeIsActive()) {

            telemetry.addLine().addData("Back Left Encoder Count: ", dogMan.getBackLeftDriveEncoderCounts());
            telemetry.addLine().addData("Back Right Encoder Count: ", dogMan.getBackRightDriveEncoderCounts());
            telemetry.addLine().addData("Front Left Encoder Count: ", dogMan.getFrontLeftDriveEncoderCounts());
            telemetry.addLine().addData("Front Right Encoder Count: ", dogMan.getFrontRightDriveEncoderCounts());
            telemetry.addLine().addData("Power applied: ", power);
            heading = dogMan.getHeading();
            telemetry.addLine().addData("heading:", heading);
            RoboRaidersProperties.setHeading(heading);
            telemetry.update();
        }
        dogMan.setDriveMotorPower(0, 0, 0, 0);
        telemetry.addData("setting power to zero", true);



    }



}
