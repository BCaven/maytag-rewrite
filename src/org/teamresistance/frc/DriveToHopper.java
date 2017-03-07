package org.teamresistance.frc;

import java.util.ArrayList;

import org.teamresistance.frc.io.IO;
import org.teamresistance.frc.mathd.Vector2d;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveToHopper {

	private double SPEED = 0.4;
	
	private double STOP_DISTANCE = 1.0;
	
	private double xyRatio = 2.6;
	
	private ArrayList<Vector2d> targetList;
	private int currentTarget = 0;
	
	private Vector2d STARTING_POSITION;
//	private Vector2d TARGET_POSITION = new Vector2d(0.5, 6.7);
	
	public void init(Vector2d startPosition, ArrayList<Vector2d> targetList) {
		SmartDashboard.putNumber("Current Target", currentTarget);
		//SmartDashboard.putNumber("Drive Angle", ANGLE);
		SmartDashboard.putNumber("Drive Speed", SPEED);
		SmartDashboard.putNumber("Drive Distance", STOP_DISTANCE);
		SmartDashboard.putNumber("XY Ratio", xyRatio);
		currentTarget = 0;
		this.targetList = targetList;
		this.STARTING_POSITION = startPosition;
	}
	
	
	public boolean update() {
		SmartDashboard.putNumber("Current Target", currentTarget);
		SPEED = SmartDashboard.getNumber("Drive Speed", SPEED);
		xyRatio = SmartDashboard.getNumber("XY Ratio", xyRatio);
		STOP_DISTANCE = SmartDashboard.getNumber("Drive Distance", STOP_DISTANCE);
		
		Vector2d position = IO.ofs.getPos().add(STARTING_POSITION);
		
		//Vector2d direction1 = new Vector2d(SPEED * Math.cos(Math.toRadians(ANGLE)), SPEED * Math.sin(Math.toRadians(ANGLE)));
		Vector2d error = targetList.get(currentTarget).sub(position);
		Vector2d direction = error.normalized().mul(SPEED);
		double dirMag = direction.length();
		
		double xOutput;
		double yOutput;
		
		if(Math.abs(direction.getX()) * xyRatio < Math.abs(direction.getY())) {
			xOutput = xyRatio * direction.getX() / Math.abs(direction.getY()) * Math.min(1, dirMag);
			yOutput = direction.getY() / Math.abs(direction.getY()) * Math.min(1, dirMag);
		} else {
			xOutput = direction.getX() / Math.abs(direction.getX()) * Math.min(1, dirMag);
			yOutput = direction.getY() / Math.abs(direction.getX()) / xyRatio * Math.min(1, dirMag);
		}
		
		//SmartDashboard.putNumber("SPEED X", direction1.getX());
		//SmartDashboard.putNumber("SPEED Y", direction1.getY());
		
		SmartDashboard.putNumber("OUTPUT X", xOutput);
		SmartDashboard.putNumber("OUTPUT Y", yOutput);
		
		SmartDashboard.putNumber("error length", error.length());
		
		if(error.length() > STOP_DISTANCE) {
			IO.drive.drive(xOutput, -yOutput, 0);
			return false;
		} else if(currentTarget != targetList.size() - 1) {
			currentTarget++;
			IO.drive.drive(xOutput, -yOutput, 0);
			return false;
		} else {
			IO.drive.drive(0, 0, 0);
			return true;
		}
	}
	
	public boolean updateDistance() {
		SPEED = SmartDashboard.getNumber("Drive Speed", SPEED);
		STOP_DISTANCE = SmartDashboard.getNumber("Drive Distance", STOP_DISTANCE);
		xyRatio = SmartDashboard.getNumber("XY Ratio", xyRatio);
		
		Vector2d position = IO.ofs.getPos();
		
		Vector2d direction = new Vector2d(1, -1);
		direction = direction.normalized().mul(SPEED);
		double dirMag = direction.length();
		
		double xOutput;
		double yOutput;
		
		if(Math.abs(direction.getX()) * xyRatio < Math.abs(direction.getY())) {
			xOutput = xyRatio * direction.getX() / Math.abs(direction.getY()) * Math.min(1, dirMag);
			yOutput = direction.getY() / Math.abs(direction.getY()) * Math.min(1, dirMag);
		} else {
			xOutput = direction.getX() / Math.abs(direction.getX()) * Math.min(1, dirMag);
			yOutput = direction.getY() / Math.abs(direction.getX()) / xyRatio * Math.min(1, dirMag);
		}
		
		SmartDashboard.putNumber("OUTPUT X", xOutput);
		SmartDashboard.putNumber("OUTPUT Y", yOutput);
		
		if(position.length() < STOP_DISTANCE) {
			IO.drive.drive(xOutput, yOutput, 0);
			return false;
		} else {
			IO.drive.drive(0, 0, 0);
			return true;
		}
	}
	
}
