package RoboRaiders.Auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

import RoboRaiders.Pipelines.AprilTagDetectionPipeline;
import RoboRaiders.Robot.TestRobot;

@Autonomous


public class DetectATandPark extends OpMode
{

    enum State {
        NOT_INITIALIZED,            // Robot has not been initialized, this is the starting state
        INITIALIZED,                // Robot has been initialized properly, occurs after pushing the INIT button
        STARTED,                    // Robot has been started, occurs after the START button has been pushed
        PARKED,                     // Robot is parked in one of 4 locations on the field
        STOP,                       // Robot is stopped, occurs at the end of autonomous after 30 seconds
        DONE                        // Robot is done, nothing more for it to do
    }

    State state = State.NOT_INITIALIZED;

    OpenCvCamera camera;
    int cameraMonitorViewId;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;
    TestRobot bill;

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

    @Override
    public void init() {
        bill = new TestRobot();
        bill.initialize(hardwareMap);
        cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

        camera.setPipeline(aprilTagDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(320,240, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {

            }
        });
        state = State.INITIALIZED;
    }

    @Override
    public void init_loop() {

        telemetry.addData("Status", "Robot is stopped and Initialized...");
        aprilTagId = getAprilTag();
        telemetry.addData("AprilTagId: ",aprilTagId);
        telemetry.update();
    }

    @Override
    public void start() {
        state = State.STARTED;
    }

    @Override
    public void loop() {

        telemetry.addData("State: ", state);

        switch(state)  {
            case STARTED:
                bill.resetEncoders();
                bill.runWithEncoders();

                switch (aprilTagId) {
                    case 1:
                        //move left than forward
                        telemetry.addData("Status", "Case 1");
                        state = State.PARKED;
                        break;

                    case 2:
                        //move forward
                        telemetry.addData("aprilTagId: ", aprilTagId);
                        numofticks = bill.driveTrainCalculateCounts(30);
                        telemetry.addData("numofticks: ", numofticks);
                        bill.setDriveMotorPower(0.5, 0.5, 0.5, 0.5);
                        while (bill.getSortedEncoderCount() <= numofticks) {
                            telemetry.addData("getSortEncoderCount()", bill.getSortedEncoderCount());
                        }
                        telemetry.update();
                        bill.setDriveMotorPower(0.0, 0.0, 0.0, 0.0);
                        state = State.PARKED;
                        break;

                    case 3:
                        //move right than forward
                        telemetry.addData("Status", "Case 3");
                        state = State.PARKED;
                        break;

                    default:
                        telemetry.addData("No April Tag Found Parking In Default Location", aprilTagId);
                        state = State.PARKED;
                        break;
                }
                telemetry.addData("Robot State: ",state);
                break;

            case PARKED:
                state = State.STOP;
                telemetry.addData("Robot State: ",state);
                break;

            case STOP:
                state = State.DONE;
                telemetry.addData("Robot State: ",state);
                break;
        }
        telemetry.update();

    }

    public int getAprilTag() {
        // Calling getDetectionsUpdate() will only return an object if there was a new frame
        // processed since the last time we called it. Otherwise, it will return null. This
        // enables us to only run logic when there has been a new frame, as opposed to the
        // getLatestDetections() method which will always return an object.
        ArrayList<AprilTagDetection> detections = aprilTagDetectionPipeline.getDetectionsUpdate();

        // If there's been a new frame...
        if (detections != null) {


            // If we don't see any tags
            if (detections.size() == 0) {
                numFramesWithoutDetection++;

                // If we haven't seen a tag for a few frames, lower the decimation
                // so we can hopefully pick one up if we're e.g. far back
                if (numFramesWithoutDetection >= THRESHOLD_NUM_FRAMES_NO_DETECTION_BEFORE_LOW_DECIMATION) {
                    aprilTagDetectionPipeline.setDecimation(DECIMATION_LOW);
                }
            }
            // We do see tags!
            else {
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

