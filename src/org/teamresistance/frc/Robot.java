package org.teamresistance.frc;

import org.teamresistance.frc.auto.Auto;
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
	
	Auto auto = new Auto();
	
	@Override
	public void robotInit() {
		// Potential Anarchy
		SmartDashboard.putNumber("Drive Path", 0);
		SmartDashboard.putNumber("Auto Drive Mode", 0);
		SmartDashboard.getNumber("Auto Mode", 0);
		SmartDashboard.getNumber("Drive Speed", 0);
		
		IO.init();
		teleop = new Teleop();
		teleop.init();

		vision = new Vision();
		vision.init();
		
		shooter = new Shooter();
		shooter.init();
		
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
		auto.init();
	}
	
	@Override
	public void autonomousPeriodic() {
		auto.update();
	}

	@Override
	public void disabledInit() {
		// teleop.disable();
	}
}
