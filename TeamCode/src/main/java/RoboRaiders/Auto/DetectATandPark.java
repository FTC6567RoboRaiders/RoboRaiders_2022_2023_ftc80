package RoboRaiders.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

import RoboRaiders.Pipelines.AprilTagDetectionPipeline;
import RoboRaiders.Robot.TestRobot;

@Autonomous


public class DetectATandPark extends LinearOpMode  {
    OpenCvCamera camera;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;

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

    final float DECIMATION_HIGH = 3;
    final float DECIMATION_LOW = 2;
    final float THRESHOLD_HIGH_DECIMATION_RANGE_METERS = 1.0f;
    final int THRESHOLD_NUM_FRAMES_NO_DETECTION_BEFORE_LOW_DECIMATION = 4;
    @Override
    public void runOpMode()
    {
        TestRobot bill = new TestRobot();
        bill.initialize(hardwareMap);
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

        camera.setPipeline(aprilTagDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.startStreaming(320,240, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {

            }
        });

        int AprilTagId = getAprilTag();
        telemetry.addData("AprilTagId: ",AprilTagId);
        telemetry.addData("Robot initialized: ", true);
        telemetry.update();

        waitForStart();
        bill.resetEncoders();
        bill.runWithEncoders();


        telemetry.setMsTransmissionInterval(50);

        while (opModeIsActive())
        {
            switch (AprilTagId) {
                case 1:
                    //move left than forward
                    break;
                case 2:
                    //move forward
                    telemetry.addData("AprileTagId: ",AprilTagId);
                    numofticks =  bill.driveTrainCalculateCounts(30);
                    telemetry.addData("numofticks: ", numofticks);
                    bill.setDriveMotorPower(0.5, 0.5, 0.5, 0.5);
                    while (opModeIsActive() && bill.getSortedEncoderCount() <= numofticks){
                        telemetry.addData("getSortEncoderCount()", bill.getSortedEncoderCount());
                    }
                    telemetry.update();
                    bill.setDriveMotorPower(0.0, 0.0, 0.0, 0.0);

                    break;
                case 3:
                    //move right than forward
                    break;
                default:
                    telemetry.addData("No April Tag Found Parking In Default", AprilTagId);

            }
        }

        // sleep(20);
    }

    public int getAprilTag()
    {
        // Calling getDetectionsUpdate() will only return an object if there was a new frame
        // processed since the last time we called it. Otherwise, it will return null. This
        // enables us to only run logic when there has been a new frame, as opposed to the
        // getLatestDetections() method which will always return an object.
        ArrayList<AprilTagDetection> detections = aprilTagDetectionPipeline.getDetectionsUpdate();

        // If there's been a new frame...
        if (detections != null)
        {


            // If we don't see any tags
            if (detections.size() == 0)
            {
                numFramesWithoutDetection++;

                // If we haven't seen a tag for a few frames, lower the decimation
                // so we can hopefully pick one up if we're e.g. far back
                if (numFramesWithoutDetection >= THRESHOLD_NUM_FRAMES_NO_DETECTION_BEFORE_LOW_DECIMATION) {
                    aprilTagDetectionPipeline.setDecimation(DECIMATION_LOW);
                }
            }
            // We do see tags!
            else
            {
                numFramesWithoutDetection = 0;

                // If the target is within 1 meter, turn on high decimation to
                // increase the frame rate
                if (detections.get(0).pose.z < THRESHOLD_HIGH_DECIMATION_RANGE_METERS) {
                    aprilTagDetectionPipeline.setDecimation(DECIMATION_HIGH);
                }

                for (AprilTagDetection detection : detections) {
                    telemetry.addLine(String.format("\nDetected tag ID=%d", detection.id));
                    return detection.id;

                }
            }

            telemetry.update();
        }
        return 0;
    }
}

