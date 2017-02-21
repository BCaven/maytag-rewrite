package org.teamresistance.frc;

import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.teamresistance.frc.util.AutoTargetFollow;
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
  private AutoTargetFollow follower;
  
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
    follower = new AutoTargetFollow();
    SmartDashboard.putNumber("Shoot P", 0.0); 
    SmartDashboard.putNumber("Shoot D", 0.0);
    follower.init(0.0, 0, 0);
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
    if(JoystickIO.leftJoystick.getRawButton(2)) {
    	follower.init(SmartDashboard.getNumber("Shoot P", 0.0), 0.0, SmartDashboard.getNumber("Shoot D", 0.0));
    }
    
    if(JoystickIO.leftJoystick.getRawButton(1)) {
    	follower.update();
    } else {
	    IO.drive.drive(JoystickIO.leftJoystick.getX(),
	        JoystickIO.leftJoystick.getY(),
	        JoystickIO.rightJoystick.getX(),
	        JoystickIO.codriverBox.getRotation());
    }
  }
}