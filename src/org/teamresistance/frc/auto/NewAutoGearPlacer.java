package org.teamresistance.frc.auto;

import java.util.ArrayList;

import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.teamresistance.frc.io.IO;
import org.teamresistance.frc.mathd.Rectangle;
import org.teamresistance.frc.mathd.Vector2d;
import org.teamresistance.frc.util.Time;
import org.teamresistance.frc.vision.GearPipeline;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionThread;

public class NewAutoGearPlacer {

	private static NewAutoGearPlacer instance = null;
	
	private double imageWidth = 320;
	private double imageHeight = 240;
	
	private VisionThread visionThread;

	private Object imgLock = new Object();

	private ArrayList<Rectangle> rects = new ArrayList<>();
	private boolean newData = false;
	
	private Vector2d center = new Vector2d(144,120);
	private double distanceXCenters = 0.0;
	
	// PID variables
	private double prevError = 0.0; // The error from the previous loop
	private double integral = 0.0; // Error integrated over time

	private double errorDeadband = 5.0;
	
	private double minMoveSpeed = 0.135; //0.2
	
	private NewAutoGearPlacer() { }
	
	private void start() {
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
						SmartDashboard.putData("Contour " + i, rects.get(i));
					}
					SmartDashboard.putData("Contour " + rects.size(), rects.get(rects.size()-1));
				}
			}
		});
		visionThread.start();
		SmartDashboard.putNumber("kP Gear", 0.0);
		SmartDashboard.putNumber("kI Gear", 0.0);
		SmartDashboard.putNumber("kD Gear", 0.0);
		SmartDashboard.putNumber("Min Move Speed", 0.135);
	}

	public Vector2d update() {
		synchronized(imgLock) {
			if(newData && rects.size() >= 2) {
				newData = false;
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
				distanceXCenters = Math.abs(rects.get(pair).getCenter().getX() - rects.get(pair+1).getCenter().getX());
				center = rects.get(pair).getCenter().add(rects.get(pair+1).getCenter()).div(2);
			}
		}
		
		// Potentially Array index out of bounds exception
		if(rects.size() < 2) {
			return new Vector2d(0.0, 0.0);
		} 
		
		double robotXSpeed = calcRobotXSpeed();
		double robotYSpeed = 0;		
		return new Vector2d(robotXSpeed, robotYSpeed);
	}
	
	private double calcRobotXSpeed() {
		final double SETPOINT = 144;
		SmartDashboard.putData("Center Contours", center);
		SmartDashboard.putData("Center Image X", new Vector2d(SETPOINT, 123));
		double error = (center.getX() - SETPOINT) / distanceXCenters;
		SmartDashboard.putNumber("Image Error", error);
		SmartDashboard.putNumber("Distance Between Centers", distanceXCenters);
		double result;
		double kP = SmartDashboard.getNumber("kP Gear", 0.0);
		double kI = SmartDashboard.getNumber("kI Gear", 0.0);
		double kD = SmartDashboard.getNumber("kD Gear", 0.0);	
		
		double maxIntegralError = 0.2;
		if (kI != 0) {
            double potentialIGain = (integral + error) * kI;
            if (potentialIGain < maxIntegralError) {
              if (potentialIGain > -maxIntegralError) {
                integral += error;
              } else {
                integral = -maxIntegralError / kI; // -1 / kI
              }
            } else {
              integral = maxIntegralError / kI; // 1 / kI
            }
        } else {
        	integral = 0;
        }
		
		if (onTarget(error)) {
			error = 0;
		}
        result = (kP * error) + (kI * integral) + (kD * (error - prevError));
       
       	prevError = error;
       	
        if (result > 1) {
          result = 1;
        } else if (result < -1) {
          result = -1;
        }
			
		return xSpeedCorrection(result);
	}
	
	private double calcRobotYSpeed(double xSpeed) {
		final double STOP_DISTANCE = 123;
		
		double result = 0.0;
		
		if(center.getY() > STOP_DISTANCE) {
			result = -0.45;
		}
		
		return result;
	}
	
	private double xSpeedCorrection(double in) {
		minMoveSpeed = SmartDashboard.getNumber("Min Move Speed", 0.135);
//		double result = (1 - minMoveSpeed) * in;
		double result = in;
		if(in == 0.0) {
			result = 0.0;
		} else if(in < 0.0 && in > -minMoveSpeed) {
			result = -minMoveSpeed;
		} else if(in > 0.0 && in < minMoveSpeed) {
			result = minMoveSpeed;
		} else if(in < -1.0) {
			result = -1.0;
		} else if(in > 1.0) {
			result = 1.0;
		}
		
		return result;
	}
	
	public static NewAutoGearPlacer getInstance() {
		if(instance == null) {
			instance = new NewAutoGearPlacer();
			instance.start();
		}
		return instance;
	}
	
	private boolean onTarget(double error) {
		return Math.abs(error) <= errorDeadband / distanceXCenters;
	}
	
}
