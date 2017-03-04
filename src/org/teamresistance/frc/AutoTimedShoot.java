package org.teamresistance.frc;

import java.util.ArrayList;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.teamresistance.frc.io.IO;
import org.teamresistance.frc.util.AutoTargetFollow;
import org.teamresistance.frc.util.Time;
import org.teamresistance.frc.util.MecanumDrive.DriveType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class AutoTimedShoot {
	
	private DriveToHopper driveToHopper = new DriveToHopper();
	private AutoTargetFollow follower = new AutoTargetFollow();
	
	private double speed = 1.0;
	private int currentState = 0;
	 
	private final double MAX_RUN_TIME_TO_HOPPER = 5.0;
	private final double MAX_HOPPER_RAM_TIME = 0.25;
	private final double MAX_DRIVE_TO_BALLS = 0.25;
	private final double MAX_SIT_FOR_BALLS = 2.0;
	private final double MAX_DRIVE_AWAY_FROM_HOPPER = 1.0;
	
	private final double DRIVE_ANGLE = 60.0;
	
	private double initialTime = Time.getTime();
	
	public void init() {
		SmartDashboard.putNumber("Acceleration Threshhold", 0.48);
		driveToHopper.init();
	    follower.init(0.0, 0.0, 0.0);
	    follower.initDistance(100, 0.0071, 0, 0.00325);
	    currentState = 0;
	}
	
	public void update() {
		SmartDashboard.putNumber("Auto State", currentState);
		double acceleration = Math.sqrt((Math.pow(IO.navX.getWorldLinearAccelX(),2) + Math.pow(IO.navX.getWorldLinearAccelY(), 2)));
		SmartDashboard.putNumber("Acceleration", acceleration);
		switch(currentState) {
		case 0:
			IO.drive.setState(DriveType.KNOB_FIELD);
			initialTime = Time.getTime();
			currentState = 1;
		case 1:
			boolean done = driveToHopper.update();
			SmartDashboard.putBoolean("Drive to Hopper", done);
			if (done || Time.getTime() - initialTime >= MAX_RUN_TIME_TO_HOPPER) {
				IO.drive.setState(DriveType.STICK_FIELD);
				IO.drive.drive(0, 0, 0, 0);
				currentState = 2;
				//currentState = -1;
				initialTime = Time.getTime();
			}
			break;
		case 2:
			IO.drive.setState(DriveType.STICK_FIELD);
			if (Time.getTime() - initialTime < MAX_HOPPER_RAM_TIME) {
				IO.drive.drive(1.0, 0, 0, 0);
			} else {
				currentState = 3;
				initialTime = Time.getTime();
			}
			break;
		case 3:
			IO.drive.setState(DriveType.STICK_FIELD);
			if (Time.getTime() - initialTime < MAX_DRIVE_TO_BALLS) {
				IO.drive.drive(0, 0.6, 0, 0);
			} else {
				IO.drive.drive(0, 0, 0, 0);
				currentState = 4;
				initialTime = Time.getTime();
			}
			break;
		case 4:
			IO.drive.setState(DriveType.STICK_FIELD);
			if (Time.getTime() - initialTime < MAX_SIT_FOR_BALLS) {
				IO.drive.drive(0, 0, 0, 0);
			} else {
				IO.drive.drive(0, 0, 0, 0);
				currentState = -1;
				initialTime = Time.getTime();
			}
			break;
		case 5:
			IO.drive.setState(DriveType.STICK_FIELD);
			//Start Tracking!
			Robot.shooter.update(true, follower.update());
			break;
		default:
			IO.drive.setState(DriveType.STICK_FIELD);
			IO.drive.drive(0, 0, 0, 0);
		}
	}
}
