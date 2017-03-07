package org.teamresistance.frc;

import java.util.ArrayList;

import org.teamresistance.frc.io.IO;
import org.teamresistance.frc.mathd.Vector2d;
import org.teamresistance.frc.util.JoystickIO;
import org.teamresistance.frc.util.Time;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Main robot class. Override methods from {@link IterativeRobot} to define
 * behavior.
 */
public class Robot extends IterativeRobot {

	public Teleop teleop;
	public Vision vision;

	public static Shooter shooter;
	private AutoTimedShoot autoShoot;
	private AutoPlaceGearSimple placeGearSimple = new AutoPlaceGearSimple();
	
	DriveToHopper driveToHopper = new DriveToHopper();
	DriveToHopperAcceleration accelerationDrive = new DriveToHopperAcceleration();
	
	@Override
	public void robotInit() {
		IO.init();
		teleop = new Teleop();
		teleop.init();

		vision = new Vision();
		vision.init();
		
		shooter = new Shooter();
		shooter.init();
		
		autoShoot = new AutoTimedShoot();
		autoShoot.init();
		
		IO.ofs.init();
		SmartDashboard.putNumber("Phase", 0);
	}

	@Override
	public void teleopInit() {
		
	}

	@Override
	public void teleopPeriodic() {
		IO.compressorRelay.set(IO.compressor.enabled() ? Relay.Value.kForward : Relay.Value.kOff);
		Time.update();
		JoystickIO.update();
		teleop.update();
		vision.update();
	}

	
	public void autonomousInit() {
	    IO.drive.init(IO.navX.getAngle(), 0.06, 0.0, 0.0);
	    IO.ofs.init();
	    autoShoot.init();
	    
	    ArrayList<Vector2d> targetList = new ArrayList<>();
	    targetList.add(new Vector2d(0, 7));
	    driveToHopper.init(new Vector2d(0,1.5), targetList);
	    
	    accelerationDrive.init();
	}
	
	@Override
	public void autonomousPeriodic() {
		Time.update();
	    IO.ofs.update();
	    
	    SmartDashboard.putNumber("OFS X", IO.ofs.getX());
	    SmartDashboard.putNumber("OFS Y", IO.ofs.getY());
	    SmartDashboard.putNumber("OFS Magnitude", IO.ofs.getPos().length());
//		autoShoot.update();
	    //placeGearSimple.update();
	    //driveToHopper.update();
	    SmartDashboard.putBoolean("Acceleration Stop", accelerationDrive.update());
	}

	@Override
	public void disabledInit() {
		// teleop.disable();
	}
}
