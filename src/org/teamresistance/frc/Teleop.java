package org.teamresistance.frc;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.teamresistance.frc.io.IO;
import org.teamresistance.frc.util.JoystickIO;
import org.teamresistance.frc.util.MecanumDrive.DriveType;

/**
 * Created by shreya on 2/20/2017.
 */
public class Teleop {

  private Climber climber;
  private Gear gear;
  private AutoGearPlacer autoGearPlacer;
  
  public void init() {
    climber = new Climber();
    gear = new Gear();
    autoGearPlacer = new AutoGearPlacer();
    climber.init();
    gear.init();
    IO.drive.setState(DriveType.STICK_FIELD);
    IO.drive.init(IO.navX.getAngle(), 0.06, 0.0, 0.0);
//    SmartDashboard.putNumber("Drive P", IO.drive.getkP());
//    SmartDashboard.putNumber("Drive I", IO.drive.getkI());
//    SmartDashboard.putNumber("Drive D", IO.drive.getkD());
    
//    SmartDashboard.putNumber("Distance", 100);
//    SmartDashboard.putNumber("Distance P", 0.0071); 
//    SmartDashboard.putNumber("Distance D", 0.00325);
    
//    SmartDashboard.putNumber("Gear P", 0.0); 
//    SmartDashboard.putNumber("Gear I", 0.0); 
//    SmartDashboard.putNumber("Gear D", 0.0);
//    SmartDashboard.putNumber("Gear Distance", 100);
//    SmartDashboard.putNumber("Gear Distance P", 0.0071); 
//    SmartDashboard.putNumber("Gear Distance D", 0.00325);
    
//    SmartDashboard.putNumber("Gear FeedForward", 0.0);
    
    autoGearPlacer.start();
    
    autoGearPlacer.init(0.0, 0, 0);
    autoGearPlacer.initDistance(100, 0, 0, 0);
  }
  
  public void update() {	  
    Robot.shooter.update(JoystickIO.btnShooter.isDown(), JoystickIO.btnAgitator.isDown());
    IO.ofs.update();
    
    SmartDashboard.putNumber("OFS X", IO.ofs.getX());
    SmartDashboard.putNumber("OFS Y", IO.ofs.getY());
    SmartDashboard.putNumber("OFS Magnitude", IO.ofs.getPos().length());
    
    /* TODO: REMOVE THESE COMMENTS
    climber.update();
    gear.update();
     */
    
    if(JoystickIO.btnGyroReset.onButtonPressed()) {
    	IO.navX.reset();
    }
    
    // Allow for changing drive controls
    if(JoystickIO.btnChangeDrive.onButtonPressed()) {
      //IO.drive.init(IO.navX.getAngle(), IO.drive.getkP(), IO.drive.getkI(), IO.drive.getkD());
    	IO.drive.init(IO.navX.getAngle(), SmartDashboard.getNumber("Drive P", IO.drive.getkP()), SmartDashboard.getNumber("Drive I", IO.drive.getkI()), SmartDashboard.getNumber("Drive D", IO.drive.getkD()));
    	//IO.drive.nextState();
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
