package org.teamresistance.frc;

import org.teamresistance.frc.io.IO;
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
	    IO.drive.init(IO.navX.getAngle(), 0.08, 0.0, 0.0);
	    IO.ofs.init();
	    autoShoot.init();
	}
	
	@Override
	public void autonomousPeriodic() {
	    IO.ofs.update();
	    
	    SmartDashboard.putNumber("OFS X", IO.ofs.getX());
	    SmartDashboard.putNumber("OFS Y", IO.ofs.getY());
	    SmartDashboard.putNumber("OFS Magnitude", IO.ofs.getPos().length());
		autoShoot.update();
	}

	@Override
	public void disabledInit() {
		// teleop.disable();
	}
}
