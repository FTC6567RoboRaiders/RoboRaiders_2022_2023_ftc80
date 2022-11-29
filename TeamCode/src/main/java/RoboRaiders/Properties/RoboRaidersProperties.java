package RoboRaiders.Properties;

public class RoboRaidersProperties {

// Class to contain static variables to hold data that may be needed
// for opmodes to communicate with each other.  All variables should be
// be defined as private and appropriate getters and setters should be
// created to access these variables.

    // +---------------------+---------------------------------------------+
// | Variable Name       | Description                                 |
// +=====================+=============================================+
// | lastHeading         | The last heading of the robot in radians    |
// |                     | inital setting 0                            |
// +---------------------+---------------------------------------------+
// |                     |                                             |
// |                     |                                             |
// |                     |                                             |
// |                     |                                             |
// +---------------------+---------------------------------------------+
//lastHeading, accessed by get and setHeading. Starts at 0.
    private static double lastHeading = 0.0;

    //Setter method
    public static void setHeading(double aHeading){
        lastHeading=aHeading;
    }
    //Getter method
    public static double getHeading(){
        return lastHeading;
    }

}
