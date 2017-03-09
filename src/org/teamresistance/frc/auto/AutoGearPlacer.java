package org.teamresistance.frc.auto;

import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;
import org.teamresistance.frc.io.IO;
import org.teamresistance.frc.mathd.Vector2d;
import org.teamresistance.frc.util.JoystickIO;
import org.teamresistance.frc.util.Time;
import org.teamresistance.frc.vision.GearPipeline;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.VisionThread;

public class AutoGearPlacer {

	// PID constants
	private double kP; // Proportional constant
	private double kI = 0.0; // Integral constant
	private double kD = 0.0; // Derivative constant

	private double kPDistance = 0.0071; // Proportional constant
	private double kIDistance = 0.0; // Integral constant
	private double kDDistance = 0.00325; // Derivative constant

	// PID variables
	private double prevError = 0.0; // The error from the previous loop
	private double integral = 0.0; // Error integrated over time

	private double prevErrorDistance = 0.0; // The error from the previous loop
	private double integralDistance = 0.0; // Error integrated over time

	private double distanceSetpoint = 100;

	private double tolerance = 0.1; // The percent tolerance for the error to be
									// considered on target

	private double maxOutput = 1.0;
	private double minOutput = -1.0;

	private double maxOutputDistance = 0.5;
	private double minOutputDistance = -0.5;

	private double imageWidth = 320;
	private double imageHeight = 240;

	private double centerX;
	private double centerY;
	
	private Vector2d rect1Pos = new Vector2d(0.0, 0.0);
	private Vector2d rect1Size = new Vector2d(0.0, 0.0);
	private Vector2d rect2Pos = new Vector2d(0.0, 0.0);
	private Vector2d rect2Size = new Vector2d(0.0, 0.0);

	private VisionThread visionThread;

	private Object imgLock;

	private double toleranceDistance = 0.1;

	int val = 0;

	public AutoGearPlacer() {
		imgLock = new Object();
	}

	public void init(double p, double i, double d) {
		this.kP = p;
		this.kI = i;
		this.kD = d;
		this.prevError = 0.0;
		this.integral = 0.0;
	}

	public void initDistance(double distance, double p, double i, double d) {
		this.kPDistance = p;
		this.kIDistance = i;
		this.kDDistance = d;
		this.prevErrorDistance = 0.0;
		this.integralDistance = 0.0;
	}

	public void start() {
		visionThread = new VisionThread(IO.gearCamera, new GearPipeline(),
				pipeline -> {
					SmartDashboard.putNumber("Updating?", val);
					val++;
					SmartDashboard.putNumber("Contour Counts", pipeline.filterContoursOutput().size());
					SmartDashboard.putNumber("Find Contour Counts", pipeline.findContoursOutput().size());
					if (pipeline.filterContoursOutput().size() >= 2) {
						Rect r1 = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
						Rect r2 = Imgproc.boundingRect(pipeline.filterContoursOutput().get(1));

						synchronized (imgLock) {
							rect1Pos = new Vector2d(r1.x, r1.y);
							rect1Size = new Vector2d(r1.width, r1.height);
							rect2Pos = new Vector2d(r2.x, r2.y);
							rect2Size = new Vector2d(r2.width, r2.height);
							centerX = (r1.x + r2.x) / 2.0;
							centerY = (r1.y + r2.y) / 2.0;
						}
					}/* else if (pipeline.filterContoursOutput().size() == 1) {
						Rect r1 = Imgproc.boundingRect(pipeline.filterContoursOutput().get(0));
//						Rect r2 = Imgproc.boundingRect(pipeline.filterContoursOutput().get(1));

						synchronized (imgLock) {
							rect1Pos = new Vector2d(r1.x, r1.y);
							rect1Size = new Vector2d(r1.width, r1.height);
							rect2Pos = new Vector2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
							rect2Size = new Vector2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
							centerX = r1.x;
							centerY = r1.y;
						}
					} */else {
						synchronized (imgLock) {
							centerX = Double.POSITIVE_INFINITY;
							centerY = Double.POSITIVE_INFINITY;
						}
					}
				});
		visionThread.start();
	}

	public void stop() {
		if (visionThread != null) {
			visionThread.interrupt();
			visionThread = null;
		}
	}

	public void update() {
		double error;
		double errorDistance;
		synchronized (imgLock) {
			if(Double.isFinite(centerX)) {
				error = imageWidth / 2.0 - centerX;
			} else {
				error = 0.0;
				integral = 0.0;
			}
			
			if(Double.isFinite(centerY)) {
				errorDistance = distanceSetpoint - centerY;
			} else {
				integralDistance = 0.0;
				errorDistance = 0.0;
			}
			/*
			SmartDashboard.putNumber("Center Y Gear", centerY);
			SmartDashboard.putNumber("Center X Gear", centerX);
			
			SmartDashboard.putNumber("Rect1 X", rect1Pos.getX());
			SmartDashboard.putNumber("Rect1 Y", rect1Pos.getY());
			SmartDashboard.putNumber("Rect1 Width", rect1Size.getX());
			SmartDashboard.putNumber("Rect1 Height", rect1Size.getY());
			
			SmartDashboard.putNumber("Rect2 X", rect2Pos.getX());
			SmartDashboard.putNumber("Rect2 Y", rect2Pos.getY());
			SmartDashboard.putNumber("Rect2 Width", rect2Size.getX());
			SmartDashboard.putNumber("Rect2 Height", rect2Size.getY());
			
			SmartDashboard.putNumber("Center X Gear", centerX);
			
			SmartDashboard.putNumber("Error Gear", error);
			SmartDashboard.putNumber("errorDistance Gear", errorDistance);
			*/
		}

		double deltaTime = Time.getDelta();

		double feedForward = SmartDashboard.getNumber("Gear FeedForward", 0.0);
		
		if (Double.isInfinite(error) || onTargetX(error)) {
			error = 0.0;
			feedForward = 0;
		} else {
			feedForward *= error / Math.abs(error);
		}
			
		integral += error * deltaTime;
		double result = (error * kP) + (integral * kI) + ((error - prevError) * kD / deltaTime) + feedForward;
		prevError = error;
		if (result > maxOutput)
			result = maxOutput;
		else if (result < minOutput)
			result = minOutput;

		if (onTargetDistance(errorDistance))
			errorDistance = 0.0;
		if(Math.abs(errorDistance * kPDistance) > 1.0) {
			integralDistance = 0;
		} else {
			integralDistance += errorDistance * deltaTime;
		}
		double resultDistance = (errorDistance * kPDistance) + (integralDistance * kIDistance) + ((errorDistance - prevErrorDistance) * kDDistance / deltaTime);
		prevErrorDistance = errorDistance;
		if (resultDistance > maxOutputDistance)
			resultDistance = maxOutputDistance;
		else if (resultDistance < minOutputDistance)
			resultDistance = minOutputDistance;

		//SmartDashboard.putNumber("Gear Result", result);
		//SmartDashboard.putNumber("Gear Distance Result", resultDistance);
		
		IO.drive.getDrive().mecanumDrive_Cartesian(JoystickIO.leftJoystick.getX(), result, 0, IO.navX.getAngle());
	}

	// If the error is less than or equal to the tolerance it is on target
	private boolean onTargetX(double error) {
		return Math.abs(error) <= tolerance * imageWidth / 2;
	}

	private boolean onTargetDistance(double error) {
		return Math.abs(error) <= tolerance * distanceSetpoint / 2;
	}

}
