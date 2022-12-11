/**
 * This is DetectATAndParkLO, LO for LinearOpMode.  This performs the same function as
 * DetectATandPark except this "extends" LinearOpMode, whereas, DetectAtandPark "extends"
 * OpMode.
 */

package RoboRaiders.Auto;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import RoboRaiders.Logger.Logger;
import RoboRaiders.Pipelines.AprilTagDetectionPipeline;
import RoboRaiders.Robot.TestRobot;

public class DectectATAndParkLO extends LinearOpMode {

    Logger myLogger;
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
    public void runOpMode() throws InterruptedException {

        /**
         * Initialize some stuff
         */
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


        waitForStart();


    }
}
