package org.teamresistance.frc;

import org.teamresistance.frc.auto.AutoGearPlacer;
import org.teamresistance.frc.io.IO;
import org.teamresistance.frc.mathd.Vector2d;
import org.teamresistance.frc.util.JoystickIO;
import org.teamresistance.frc.util.MecanumDrive.DriveType;
import org.teamresistance.frc.util.Util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Created by ShReYa on 2/20/2017.
 */
public class Teleop {

	private Climber climber;
	private Gear gear;
	private AutoGearPlacer gearPlacer = new AutoGearPlacer();

	private boolean grabAngleOnce = true;
	private double holdAngle = 0;

	public void init() {
		climber = new Climber();
		gear = new Gear();
		climber.init();
		gear.init();
		IO.drive.setState(DriveType.STICK_FIELD);
		SmartDashboard.putNumber("Rotate Speed", 0.0);
		SmartDashboard.putNumber("Y Speed", 0.0);
		SmartDashboard.putNumber("X Speed", 0.0);
		gearPlacer.start();
		// IO.drive.init(IO.navX.getAngle(), 0.08, 0.0, 0.0);
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
		SmartDashboard.putNumber("Joystick 0 X", JoystickIO.leftJoystick.getX());
		SmartDashboard.putNumber("Joystick 0 Y", JoystickIO.leftJoystick.getY());
		SmartDashboard.putNumber("Joystick 1 X", JoystickIO.rightJoystick.getX());
		SmartDashboard.putNumber("Joystick 1 Y", JoystickIO.rightJoystick.getY());
		SmartDashboard.putNumber("Joystick 2 X", JoystickIO.coJoystick.getX());
		SmartDashboard.putNumber("Joystick 2 Y", JoystickIO.coJoystick.getY());

		SmartDashboard.putNumber("Gyro Normalized Angle", IO.navX.getNormalizedAngle());
		SmartDashboard.putNumber("Gyro Raw Angle", IO.navX.getRawAngle());

		double rotateSpeed = SmartDashboard.getNumber("Rotate Speed", 0.0);
		double ySpeed = SmartDashboard.getNumber("Y Speed", 0.0);
		double xSpeed = SmartDashboard.getNumber("X Speed", 0.0);

		double scaledX = Util.scaleInput(JoystickIO.leftJoystick.getX());
		double scaledY = Util.scaleInput(JoystickIO.leftJoystick.getY());
		double scaledRotate = Util.scaleInput(JoystickIO.rightJoystick.getX());
		
		if (JoystickIO.btnHoldLeft.isDown()) {
			IO.drive.setState(DriveType.ROTATE_PID);
			IO.drive.drive(scaledX, scaledY, 0, 330);
			grabAngleOnce = true;
		} else if (JoystickIO.btnHoldCenter.isDown()) {
			Vector2d speed = gearPlacer.update().rotate(270);
			IO.drive.setState(DriveType.ROTATE_PID);
			IO.drive.drive(speed.getX(), speed.getY(), 0, 270);
			grabAngleOnce = true;
		} else if (JoystickIO.btnHoldRight.isDown()) {
			IO.drive.setState(DriveType.ROTATE_PID);
			IO.drive.drive(scaledX, scaledY, 0, 210);
			grabAngleOnce = true;
		} else {
			SmartDashboard.putNumber("Angle Being Held", holdAngle);
			if (scaledRotate == 0) {
				SmartDashboard.putBoolean("Is Holding Angle", true);
				if (grabAngleOnce) {
					grabAngleOnce = false;
					holdAngle = IO.navX.getNormalizedAngle();
				}
				IO.drive.setState(DriveType.ROTATE_PID);
				IO.drive.drive(scaledX, scaledY, 0, holdAngle);
			} else {
				grabAngleOnce = true;
				IO.drive.setState(DriveType.STICK_FIELD);
				IO.drive.drive(scaledX, scaledY, scaledRotate, 0);
			}
		}

		Robot.shooter.update(JoystickIO.btnShooter.isDown(), JoystickIO.btnAgitator.isDown());

		climber.update();
		gear.update();

		if (JoystickIO.btnGyroReset.onButtonPressed()) {
			IO.navX.reset();
			grabAngleOnce = true;
		}
	}

	public void disable() {

	}
}
