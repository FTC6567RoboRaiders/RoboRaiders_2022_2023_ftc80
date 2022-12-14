/**
 * This is DetectATAndParkLO, LO for LinearOpMode.  This performs the same function as
 * DetectATandPark except this "extends" LinearOpMode, whereas, DetectAtandPark "extends"
 * OpMode.
 */

package RoboRaiders.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

import RoboRaiders.Utilities.Logger.Logger;
import RoboRaiders.Pipelines.AprilTagDetectionPipeline;
import RoboRaiders.Robot.TestRobot;

@Autonomous

public class DectectATAndParkLO extends LinearOpMode {

    Logger myLogger;
    OpenCvCamera camera;
    int cameraMonitorViewId;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;
    TestRobot stevesRobot;

    static final double FEET_PER_METER = 3.28084;

    // Lens intrinsics
    // UNITS ARE PIXELS
    // NOTE: this calibration is for the C920 webcam at 800x448.
    // You will need to do your own calibration for other configurations!
    double fx = 578.272;
    double fy = 578.272;
    double cx = 402.145;
    double cy = 221.506;
    double numofticks;


    // UNITS ARE METERS
    double tagsize = 0.166;

    int numFramesWithoutDetection = 0;
    int aprilTagId;

    final float DECIMATION_HIGH = 3;
    final float DECIMATION_LOW = 2;
    final float THRESHOLD_HIGH_DECIMATION_RANGE_METERS = 1.0f;
    final int THRESHOLD_NUM_FRAMES_NO_DETECTION_BEFORE_LOW_DECIMATION = 4;

    final int[] OUR_APRIL_TAGS = {0,1,2};



    @Override
    public void runOpMode() throws InterruptedException {

        /**
         * Initialize some stuff
         */
        myLogger = new Logger("DectectATAndParkLO");
        stevesRobot = new TestRobot();
        stevesRobot.initialize(hardwareMap);
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

        camera.setPipeline(aprilTagDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(800,448, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {

            }
        });


        stevesRobot.resetEncoders();
        stevesRobot.runWithEncoders();
        // waitForStart();
        while (!isStarted() && !isStopRequested()) {
            telemetry.addData("Status", "Robot is stopped and Initialized...");
            aprilTagId = getAprilTag();
            telemetry.addData("AprilTagId: ", aprilTagId);
            telemetry.update();
            myLogger.Debug("init_loop() - aprilTagId: ", aprilTagId);
        }

        /*
        Values For Different Directions:
        (pos, neg, neg, pos) = strafe left
        (neg, pos, pos, neg) = strafe right
        (neg, neg ,neg, neg) = move forward
        (pos, pos, pos, pos) = move backward
         */
        //This Part Will Deposit Cone, Same Code Regardless of AprilTag

        numofticks = stevesRobot.driveTrainCalculateCounts(1);
        telemetry.addData("numofticks: ", numofticks);
        stevesRobot.setDriveMotorPower(-0.25, -0.25, -0.25, -0.25);
        while (opModeIsActive() && stevesRobot.getSortedEncoderCount() <= numofticks) {
            telemetry.addData("getSortEncoderCount()", stevesRobot.getSortedEncoderCount());
        }
        stevesRobot.resetEncoders();
        stevesRobot.runWithEncoders();
        numofticks = stevesRobot.driveTrainCalculateCounts(18);
        telemetry.addData("numofticks: ", numofticks);
        stevesRobot.setDriveMotorPower(0.25, -0.25, -0.25, 0.25);
        while (opModeIsActive() && stevesRobot.getSortedEncoderCount() <= numofticks) {
            telemetry.addData("getSortEncoderCount()", stevesRobot.getSortedEncoderCount());
        }
        stevesRobot.resetEncoders();
        stevesRobot.runWithEncoders();
        numofticks = stevesRobot.driveTrainCalculateCounts(25);
        telemetry.addData("numofticks: ", numofticks);
        stevesRobot.setDriveMotorPower(-0.25, -0.25, -0.25, -0.25);
        while (opModeIsActive() && stevesRobot.getSortedEncoderCount() <= numofticks) {
            telemetry.addData("getSortEncoderCount()", stevesRobot.getSortedEncoderCount());
        }
        stevesRobot.resetEncoders();
        stevesRobot.runWithEncoders();

        //Move Lift To Turn Turret
        stevesRobot.setLiftMotorTargetPosition(-3000);
        stevesRobot.liftRunWithEncodersSTP();
        stevesRobot.setLiftMotorVelocity(-1500.0);
        while (opModeIsActive() && Math.abs(stevesRobot.getLiftEncoderCounts()) <= 3000){
            stevesRobot.liftRunWithoutEncoders();
            telemetry.addData("getLiftEncoderCounts()", stevesRobot.getLiftEncoderCounts());
        }
        //Turret Move to Position
        stevesRobot.setTurretMotorTargetPosition(-94.5);
        stevesRobot.turretRunWithEncodersSTP();
        stevesRobot.setTurretMotorVelocity(600.0);
        while (opModeIsActive() && Math.abs(stevesRobot.getTurretEncoderCounts()) <= 94.5){
            telemetry.addData("getTurretEncoderCounts()", stevesRobot.getTurretEncoderCounts());

        }
        //Lift Move to Height
        stevesRobot.setLiftMotorTargetPosition(-7750);
        stevesRobot.liftRunWithEncodersSTP();
        stevesRobot.setLiftMotorVelocity(-1500.0);
        while (opModeIsActive() && Math.abs(stevesRobot.getLiftEncoderCounts()) <= 7750.0){
            stevesRobot.liftRunWithoutEncoders();
            telemetry.addData("getLiftEncoderCounts()", stevesRobot.getLiftEncoderCounts());
        }
        //Deposit
        stevesRobot.setinTakeServoPosition(1.0);





        //Move To Park
        switch (aprilTagId) {
            case 0:
                //Move Backward to Park
                telemetry.addData("Status", "Case 1");
                telemetry.addData("Status", "Case 2");
                telemetry.addData("aprilTagId: ", aprilTagId);

                numofticks = stevesRobot.driveTrainCalculateCounts(10);
                telemetry.addData("numofticks: ", numofticks);
                stevesRobot.setDriveMotorPower(0.5, 0.5, 0.5, 0.5);
                while (opModeIsActive() && stevesRobot.getSortedEncoderCount() <= numofticks){
                    telemetry.addData("getSortEncoderCount()", stevesRobot.getSortedEncoderCount());
                }
                break;


            case 1:
                //move forward
                myLogger.Debug("loop() - Case 2");
                telemetry.addData("Status", "Case 2");
                telemetry.addData("aprilTagId: ", aprilTagId);

                numofticks = stevesRobot.driveTrainCalculateCounts(25);
                telemetry.addData("numofticks: ", numofticks);
                stevesRobot.setDriveMotorPower(-0.5, -0.5, -0.5, -0.5);
                while (opModeIsActive() && stevesRobot.getSortedEncoderCount() <= numofticks){
                            telemetry.addData("getSortEncoderCount()", stevesRobot.getSortedEncoderCount());
                }

                break;

            case 2:
                //Back and Then Right
                telemetry.addData("Status", "Case 1");

                numofticks = stevesRobot.driveTrainCalculateCounts(10.0);
                telemetry.addData("numofticks: ", numofticks);
                stevesRobot.setDriveMotorPower(0.25, 0.25, 0.25, 0.25);
                while (opModeIsActive() && stevesRobot.getSortedEncoderCount() <= numofticks) {
                    telemetry.addData("getSortEncoderCount()", stevesRobot.getSortedEncoderCount());
                }
                stevesRobot.resetEncoders();
                stevesRobot.runWithEncoders();

                numofticks = stevesRobot.driveTrainCalculateCounts(27.0);
                telemetry.addData("numofticks: ", numofticks);
                stevesRobot.setDriveMotorPower(-0.25, 0.25, 0.25, -0.25);
                while (opModeIsActive() && stevesRobot.getSortedEncoderCount() <= numofticks) {
                    telemetry.addData("getSortEncoderCount()", stevesRobot.getSortedEncoderCount());
                }
                stevesRobot.resetEncoders();
                stevesRobot.runWithEncoders();


                break;
            default:
                myLogger.Debug("loop() - default");
                telemetry.addData("No April Tag Found Parking In Default Location", aprilTagId);
                break;
        }

    }
    public int getAprilTag() {
        boolean april_tag_found = false;
        int atId = 0;
        // Calling getDetectionsUpdate() will only return an object if there was a new frame
        // processed since the last time we called it. Otherwise, it will return null. This
        // enables us to only run logic when there has been a new frame, as opposed to the
        // getLatestDetections() method which will always return an object.

        //ArrayList<AprilTagDetection> detections = aprilTagDetectionPipeline.getDetectionsUpdate();
        ArrayList<AprilTagDetection> detections = aprilTagDetectionPipeline.getLatestDetections();  // Changed to this line to always return an object

        // If there's been a new frame...
        if (detections != null) {


            // If we don't see any tags
            if (detections.size() == 0) {
                numFramesWithoutDetection++;
                telemetry.addData("numFramesWithoutDetection: ",numFramesWithoutDetection);

                // If we haven't seen a tag for a few frames, lower the decimation
                // so we can hopefully pick one up if we're e.g. far back
                if (numFramesWithoutDetection >= THRESHOLD_NUM_FRAMES_NO_DETECTION_BEFORE_LOW_DECIMATION) {
                    aprilTagDetectionPipeline.setDecimation(DECIMATION_LOW);
                    telemetry.addData("setting decimation to: ", "LOW");
                }
            }
            // We do see tags!
            else {
                numFramesWithoutDetection = 0;

                // If the target is within 1 meter, turn on high decimation to
                // increase the frame rate
                if (detections.get(0).pose.z < THRESHOLD_HIGH_DECIMATION_RANGE_METERS) {
                    aprilTagDetectionPipeline.setDecimation(DECIMATION_HIGH);
                    telemetry.addData("setting decimation to: ", "HIGH");
                }

                // get the first detection rather than all the detections, we will need to test this
                // out on the field since there could be other teams using apriltags for their sleeve
                // that are across the field, so we just need to print another sleeve and put in the
                // position across field from where the robot is stating from.
                AprilTagDetection detection = detections.get(0);

                // for (AprilTagDetection detection : detections) {
                //     telemetry.addLine(String.format("\nDetected tag ID=%d", detection.id));

                // get the detection id information and stash it into a variable for now
                atId = detection.id;

                for (int i = 0; i<=2; i++){
                    if(atId == OUR_APRIL_TAGS[i]){
                        april_tag_found = true;
                    }

                }

                // }
            }

            // telemetry.update();
        }
        if(april_tag_found){
            return atId;
        }
        else{
            return 99999999;
        }

    }
}
