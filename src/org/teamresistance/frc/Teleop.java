package org.teamresistance.frc;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import org.teamresistance.frc.auto.AutoGearPlacer;
import org.teamresistance.frc.io.IO;
import org.teamresistance.frc.util.JoystickIO;
import org.teamresistance.frc.util.MecanumDrive.DriveType;

/**
 * Created by ShReYa on 2/20/2017.
 */
public class Teleop {

	private Climber climber;
	private Gear gear;

	public void init() {
		climber = new Climber();
		gear = new Gear();
		climber.init();
		gear.init();
		IO.drive.setState(DriveType.STICK_FIELD);
		//IO.drive.init(IO.navX.getAngle(), 0.08, 0.0, 0.0);
		// SmartDashboard.putNumber("Drive P", IO.drive.getkP());
		// SmartDashboard.putNumber("Drive I", IO.drive.getkI());
		// SmartDashboard.putNumber("Drive D", IO.drive.getkD());

		// SmartDashboard.putNumber("Distance", 100);
		// SmartDashboard.putNumber("Distance P", 0.0071);
		// SmartDashboard.putNumber("Distance D", 0.00325);

		// SmartDashboard.putNumber("Gear P", 0.0);
		// SmartDashboard.putNumber("Gear I", 0.0);
		// SmartDashboard.putNumber("Gear D", 0.0);
		// SmartDashboard.putNumber("Gear Distance", 100);
		// SmartDashboard.putNumber("Gear Distance P", 0.0071);
		// SmartDashboard.putNumber("Gear Distance D", 0.00325);

		// SmartDashboard.putNumber("Gear FeedForward", 0.0);

		// autoGearPlacer.start();

		// autoGearPlacer.init(0.0, 0, 0);
		// autoGearPlacer.initDistance(100, 0, 0, 0);
	}

	public void update() {
		IO.drive.drive(JoystickIO.leftJoystick.getX(),
				JoystickIO.leftJoystick.getY(),
				JoystickIO.rightJoystick.getX(), 0);

		Robot.shooter.update(JoystickIO.btnShooter.isDown(), JoystickIO.btnAgitator.isDown());

		climber.update();
		gear.update();

		if (JoystickIO.btnGyroReset.onButtonPressed()) {
			IO.navX.reset();
		}

		/*
		 * if(JoystickIO.leftJoystick.getRawButton(2)) {
		 * autoGearPlacer.init(SmartDashboard.getNumber("Gear P", 0.0),
		 * SmartDashboard.getNumber("Gear I", 0.0),
		 * SmartDashboard.getNumber("Gear D", 0.0));
		 * autoGearPlacer.initDistance(SmartDashboard.getNumber("Gear Distance",
		 * 100.0), SmartDashboard.getNumber("Gear Distance P", 0.0), 0.0,
		 * SmartDashboard.getNumber("Distance D", 0.0)); }
		 */

		/*
		 * if(JoystickIO.leftJoystick.getRawButton(2)) {
		 * follower.init(SmartDashboard.getNumber("Shoot P", 0.0), 0.0,
		 * SmartDashboard.getNumber("Shoot D", 0.0));
		 * follower.initDistance(SmartDashboard.getNumber("Distance", 100.0),
		 * SmartDashboard.getNumber("Distance P", 0.0), 0.0,
		 * SmartDashboard.getNumber("Distance D", 0.0)); }
		 */
		// if(JoystickIO.btnShooter.onButtonPressed()) {
		// follower.start();
		// } else if(JoystickIO.btnShooter.onButtonReleased()) {
		// follower.stop();
		// }
	}

	public void disable() {
		// autoGearPlacer.stop();
	}
}
