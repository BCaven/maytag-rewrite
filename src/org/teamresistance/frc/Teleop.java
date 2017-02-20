package org.teamresistance.frc;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.teamresistance.frc.util.JoystickIO;
import org.teamresistance.frc.util.MecanumDrive;
import org.teamresistance.frc.util.MecanumDrive.DriveType;

/**
 * Created by shrey on 2/20/2017.
 */
public class Teleop {

  private Shooter shooter;
  private Climber climber;
  private Snorfler snorfler;
  private Gear gear;
  
  public void init() {
    shooter = new Shooter();
    climber = new Climber();
    snorfler = new Snorfler();
    gear = new Gear();
    shooter.init();
    climber.init();
    snorfler.init();
    gear.init();
    IO.drive.setState(DriveType.STICK_FIELD);
    IO.drive.init(IO.navX.getAngle(), 0.03, 0.0, 0.06);
  }

  public void update() {
    shooter.update();
    climber.update();
    snorfler.update();
    gear.update();

    // Allow for changing drive controls
    if(JoystickIO.btnChangeDrive.onButtonPressed()) {
      IO.drive.init(IO.navX.getAngle(), IO.drive.getkP(), IO.drive.getkI(), IO.drive.getkD());
      IO.drive.nextState();
    }
    IO.drive.drive(JoystickIO.leftJoystick.getX(),
        JoystickIO.leftJoystick.getY(),
        JoystickIO.rightJoystick.getX(),
        JoystickIO.codriverBox.getRotation());
    
    double acceleration = 
        	Math.sqrt((Math.pow(IO.navX.getWorldLinearAccelX(),2) + Math.pow(IO.navX.getWorldLinearAccelY(), 2)));
        SmartDashboard.putNumber("Acceleration", acceleration);
    
  }
}
