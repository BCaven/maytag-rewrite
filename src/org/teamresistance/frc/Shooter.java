package org.teamresistance.frc;

import org.teamresistance.frc.util.JoystickIO;

import com.ctre.CANTalon.FeedbackDevice;
import com.ctre.CANTalon.TalonControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Created by shrey on 2/20/2017.
 */
public class Shooter {

	public void init() {
		IO.shooterMotor.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		IO.shooterMotor.reverseSensor(false);
		IO.shooterMotor.reverseOutput(false);
		IO.shooterMotor.configEncoderCodesPerRev(20);
		
		// Not sure what this nominal output voltage is for...
		IO.shooterMotor.configNominalOutputVoltage(+0.0, -0.0);
		// Only allow the motor to spin in the forward direction
		IO.shooterMotor.configPeakOutputVoltage(12.0, 0.0);
		
		//Will never change speed faster than 24V/Sec
		//IO.shooterMotor.setVoltageRampRate(24.0);
	}

	public void update() {
		double motorOutput = IO.shooterMotor.getOutputVoltage() / IO.shooterMotor.getBusVoltage();
		SmartDashboard.putNumber("Talon Motor Output", motorOutput);
		
		if(JoystickIO.leftJoystick.getRawButton(1)) {
			IO.shooterMotor.changeControlMode(TalonControlMode.Speed);
			IO.shooterMotor.set(4000);
			
			SmartDashboard.putNumber("Talon Error", IO.shooterMotor.getClosedLoopError());
		} else {
			IO.shooterMotor.changeControlMode(TalonControlMode.PercentVbus);
			IO.shooterMotor.set(0.0);
		}
		
		
		SmartDashboard.putNumber("Talon Speed", IO.shooterMotor.getSpeed());
		
//		if (JoystickIO.btnShooter.isDown()) {
//			IO.shooterMotor.set(0.80);
//			IO.feederMotor.set(1.0);
//			if (JoystickIO.btnAgitator.isDown()) {
//				IO.agitatorMotor.set(0.3);
//			} else {
//				IO.agitatorMotor.set(0.0);
//			}
//		} else {
//			IO.shooterMotor.set(0.0);
//			IO.agitatorMotor.set(0.0);
//			IO.feederMotor.set(0.0);
//		}
	}

}
