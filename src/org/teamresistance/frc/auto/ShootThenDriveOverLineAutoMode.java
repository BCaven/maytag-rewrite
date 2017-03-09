package org.teamresistance.frc.auto;

import org.teamresistance.frc.Robot;
import org.teamresistance.frc.io.IO;
import org.teamresistance.frc.util.Time;

public class ShootThenDriveOverLineAutoMode implements AutoMode {

	private static final double DELAY_TILL_SHOOT = 1.0;
	private static final double SHOOT_TIME = 3.0;
	private static final double DRIVE_TIME = 4.0;
	
	private static final double DRIVE_SPEED = 0.6;
	
	private double initialTime;
	private int currentState = 0;
	
	@Override
	public void init() {
		
	}

	@Override
	public boolean update() {
		boolean agitate = false;
		
		switch(currentState) {
		case 0:
			initialTime = Time.getTime();
			currentState = 1;
		case 1:
			if(Time.getTime() - initialTime > DELAY_TILL_SHOOT) {
				currentState = 2;
				initialTime = Time.getTime();
			}
			break;
		case 2:
			if(Time.getTime() - initialTime > SHOOT_TIME) {
				currentState = 3;
				initialTime = Time.getTime();
			}
			agitate = true;
			break;
		case 3:
			if(Time.getTime() - initialTime > DRIVE_TIME) {
				currentState = -1;
			}
			IO.drive.drive(DRIVE_SPEED, 0.0, 0.0, 0.0);
			agitate = false;
			break;
		default:
			IO.drive.drive(0.0, 0.0, 0.0, 0.0);
			agitate = false;
			break;
		}
		Robot.shooter.update(true, agitate);
		return false;
	}

	@Override
	public String toString() {
		return "Shoot Then Drive Over Line";
	}
	
	
}
