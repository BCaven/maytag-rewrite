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
  private Gear gear;
  private AutoTargetFollow follower;
  private AutoTimedShoot autoShoot;
  private AutoGearPlacer autoGearPlacer;
  
  public void init() {
    shooter = new Shooter();
    climber = new Climber();
    gear = new Gear();
    autoShoot = new AutoTimedShoot();
    autoGearPlacer = new AutoGearPlacer();
    shooter.init();
    climber.init();
    gear.init();
    IO.drive.setState(DriveType.STICK_FIELD);
    IO.drive.init(IO.navX.getAngle(), 0.03, 0.0, 0.06);
    follower = new AutoTargetFollow();
    //SmartDashboard.putNumber("Shoot P", fol); 
    //SmartDashboard.putNumber("Shoot D", 0.0);
    SmartDashboard.putNumber("Distance", 100);
    SmartDashboard.putNumber("Distance P", 0.0071); 
    SmartDashboard.putNumber("Distance D", 0.00325);
    
    SmartDashboard.putNumber("Gear P", 0.0); 
    SmartDashboard.putNumber("Gear I", 0.0); 
    SmartDashboard.putNumber("Gear D", 0.0);
    SmartDashboard.putNumber("Gear Distance", 100);
    SmartDashboard.putNumber("Gear Distance P", 0.0071); 
    SmartDashboard.putNumber("Gear Distance D", 0.00325);
    
    SmartDashboard.putNumber("Gear FeedForward", 0.0);
    follower.init(0.0, 0, 0);
    follower.initDistance(100, 0, 0, 0);
    
    autoGearPlacer.start();
    
    autoGearPlacer.init(0.0, 0, 0);
    autoGearPlacer.initDistance(100, 0, 0, 0);
  }
  
  public void update() {	  
    shooter.update();
    /* TODO: REMOVE THESE COMMENTS
    climber.update();
    gear.update();

     */
    // Allow for changing drive controls
    if(JoystickIO.btnChangeDrive.onButtonPressed()) {
      IO.drive.init(IO.navX.getAngle(), IO.drive.getkP(), IO.drive.getkI(), IO.drive.getkD());
      IO.drive.nextState();
    }
    
    if(JoystickIO.leftJoystick.getRawButton(2)) {
    	autoGearPlacer.init(SmartDashboard.getNumber("Gear P", 0.0), SmartDashboard.getNumber("Gear I", 0.0), SmartDashboard.getNumber("Gear D", 0.0));
    	autoGearPlacer.initDistance(SmartDashboard.getNumber("Gear Distance", 100.0), SmartDashboard.getNumber("Gear Distance P", 0.0), 0.0, SmartDashboard.getNumber("Distance D", 0.0));
    }
    
    /*
    if(JoystickIO.leftJoystick.getRawButton(2)) {
    	follower.init(SmartDashboard.getNumber("Shoot P", 0.0), 0.0, SmartDashboard.getNumber("Shoot D", 0.0));
    	follower.initDistance(SmartDashboard.getNumber("Distance", 100.0), SmartDashboard.getNumber("Distance P", 0.0), 0.0, SmartDashboard.getNumber("Distance D", 0.0));
    }
    */
//    if(JoystickIO.btnShooter.onButtonPressed()) {
//    	follower.start();
//    } else if(JoystickIO.btnShooter.onButtonReleased()) {
//    	follower.stop();
//    }
    if(JoystickIO.leftJoystick.getRawButton(4)) {
    	IO.drive.setState(DriveType.KNOB_FIELD);
    	autoGearPlacer.update();
    } else if(JoystickIO.leftJoystick.getRawButton(1)) {
    	IO.drive.setState(DriveType.KNOB_FIELD);
    	follower.update();
    }else if(JoystickIO.leftJoystick.getRawButton(3)) {
    	IO.drive.setState(DriveType.KNOB_FIELD);
    	autoShoot.update();
    } else {
    	IO.drive.setState(DriveType.STICK_FIELD);
	    IO.drive.drive(JoystickIO.leftJoystick.getX(),
	        JoystickIO.leftJoystick.getY(),
	        JoystickIO.rightJoystick.getX(),
	        JoystickIO.codriverBox.getRotation());
    }
  }
  
  public void disable() {
	  autoGearPlacer.stop();
  }
}
