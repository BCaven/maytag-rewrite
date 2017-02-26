package org.teamresistance.frc;

import java.util.ArrayList;

import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.teamresistance.frc.util.Time;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class AutoTimedShoot {
	
	private double speed = 1.0;
	private int currentState = 0;
	 
	private final double MAX_RUN_TIME_TO_HOPPER = 5.0;
	private final double MAX_HOPPER_RAM_TIME = 0.5;
	private final double MAX_DRIVE_TO_BALLS = 0.5;
	private final double MAX_SIT_FOR_BALLS = 2.0;
	private final double MAX_DRIVE_AWAY_FROM_HOPPER = 1.0;
	
	private double initialTime = Time.getTime();	
	
	public boolean done = false;
	
	public void init() {
	}
	
	public void update() {
		double acceleration = Math.sqrt(
					(Math.pow(IO.navX.getWorldLinearAccelX(),2) + Math.pow(IO.navX.getWorldLinearAccelY(), 2)));
		SmartDashboard.putNumber("Acceleration", acceleration);
		switch(currentState) {
		case 0:
			initialTime = Time.getTime();
			currentState = 1;
			break;
		case 1:
			if ((acceleration < 0.48) && (Time.getTime() - initialTime < MAX_RUN_TIME_TO_HOPPER)) {
				double xSpeed = Math.sin(Math.toRadians(60)) * speed;
				double ySpeed = -Math.cos(Math.toRadians(60)) * speed;
				IO.drive.drive(xSpeed, ySpeed, 0, 0);
			} else {
				done = true;
				IO.drive.drive(0, 0, 0, 0);
				//currentState = 2;
				initialTime = Time.getTime();
			}
			break;
		case 2:
			if (Time.getTime() - initialTime < MAX_HOPPER_RAM_TIME) {
				IO.drive.drive(1.0, 0, 0, 0);
			} else {
				IO.drive.drive(0, 0, 0, 0);
				currentState = 3;
				initialTime = Time.getTime();
			}
			break;
		case 3:
			if (Time.getTime() - initialTime < MAX_DRIVE_TO_BALLS) {
				IO.drive.drive(0, 1.0, 0, 0);
			} else {
				IO.drive.drive(0, 0, 0, 0);
				currentState = 4;
				initialTime = Time.getTime();
			}
			break;
		case 4:
			if (Time.getTime() - initialTime < MAX_SIT_FOR_BALLS) {
				IO.drive.drive(0, 0, 0, 0);
			} else {
				IO.drive.drive(0, 0, 0, 0);
				currentState = 5;
				initialTime = Time.getTime();
			}
			break;
		case 5:
			if (Time.getTime() - initialTime < MAX_DRIVE_AWAY_FROM_HOPPER) {
				IO.drive.drive(-1.0, 0, 0, 0);
			} else {
				IO.drive.drive(0, 0, 0, 0);
				currentState = 6;
				initialTime = Time.getTime();
			}
			break;
			
		}
	}
}
