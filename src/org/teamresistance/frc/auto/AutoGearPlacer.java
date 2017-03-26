package org.teamresistance.frc.auto;

import java.util.ArrayList;

import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.teamresistance.frc.io.IO;
import org.teamresistance.frc.mathd.Rectangle;
import org.teamresistance.frc.mathd.Vector2d;
import org.teamresistance.frc.util.JoystickIO;
import org.teamresistance.frc.util.Time;
import org.teamresistance.frc.vision.GearPipeline;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionThread;

public class AutoGearPlacer {

	private double imageWidth = 320;
	private double imageHeight = 240;
	
	private VisionThread visionThread;

	private Object imgLock = new Object();

	private ArrayList<Rectangle> rects = new ArrayList<>();
	private boolean newData = false;
	
	private Vector2d center = new Vector2d(144,120);
	
	public void start() {
		visionThread = new VisionThread(IO.gearCamera, new GearPipeline(), pipeline -> {
			SmartDashboard.putNumber("Contour Counts", pipeline.filterContoursOutput().size());
			SmartDashboard.putNumber("Find Contour Counts", pipeline.findContoursOutput().size());
			if(!pipeline.filterContoursOutput().isEmpty()) {
				synchronized(imgLock) {
					newData = true;
					rects.clear();
					for(int i = 0; i < pipeline.filterContoursOutput().size(); i++) {
						Rect rect = Imgproc.boundingRect(pipeline.filterContoursOutput().get(i));
						rects.add(new Rectangle(rect.x, rect.y, rect.width, rect.height));
					}
				}
			}
		});
		visionThread.start();
		SmartDashboard.putNumber("kP Gear Lateral Translate", 0.0);
	}

	public Vector2d update() {
		synchronized(imgLock) {
			if(newData) {
				newData = false;
				if(rects.size() < 2) {
					return new Vector2d(0.0, 0.0);
				}
//				for(int i = 0; i < rects.size(); i++) {
//					SmartDashboard.putData("Rectangle " + i, rects.get(i));
//				}
				// Find pair of objects closest together
				double minDifference = Double.MAX_VALUE;
				int pair = -1;
				for(int i = 0; i < rects.size() - 1; i++) {
					double difference = Math.max(rects.get(i).size.getY(), rects.get(i+1).size.getY()) - Math.min(rects.get(i).size.getY(), rects.get(i+1).size.getY());
					if(difference < minDifference) {
						minDifference = difference;
						pair = i;
					}
				}
				center = rects.get(pair).getCenter().add(rects.get(pair+1).getCenter()).div(2);
			}
		}
		double robotXSpeed = calcRobotYSpeed(center);
		double robotYSpeed = 0.0;
		
		return new Vector2d(robotXSpeed, robotYSpeed);
	}
	
	double calcRobotYSpeed(Vector2d center) {
		final double ERROR_DEADBAND = 15.0;
		final double SPEED = 0.1;
		final double SETPOINT = 144;
		SmartDashboard.putData("Center Contours", center);
		SmartDashboard.putNumber("Center Image X", SETPOINT);
		double error = center.getX() - SETPOINT;
		double result;
		if(Math.abs(error) < ERROR_DEADBAND) {
			result = 0.0;
		} else {
			double kP = SmartDashboard.getNumber("kP Gear Lateral Translate", 0.0);
			result = (error * kP);
			if (Math.abs(result) < SPEED) {
				if (result < 0) {
					result = -SPEED;
				} else {
					result = SPEED;
				}
			}
		}
		
		return result;
	}
}
