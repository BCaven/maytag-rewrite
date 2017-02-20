package org.teamresistance.frc;

import org.teamresistance.frc.util.JoystickIO;
import org.teamresistance.frc.util.Time;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Relay;

/**
 * Main robot class. Override methods from {@link IterativeRobot} to define behavior.
 */
public class Robot extends IterativeRobot {

  public Teleop teleop;

    @Override
    public void robotInit() {
    	
    	IO.rightFrontMotor.setInverted(true);
        IO.rightRearMotor.setInverted(true);
        
        IO.snorflerMotor.setInverted(true);
        IO.climberMotor.setInverted(true);
        
        IO.feederMotor.setInverted(true);
    }

    @Override
    public void teleopInit() {
      teleop = new Teleop();
      teleop.init();
    }


    @Override
    public void teleopPeriodic() {
      IO.compressorRelay.set(IO.compressor.enabled() ? Relay.Value.kForward : Relay.Value.kOff);
      Time.update();
      JoystickIO.update();
      teleop.update();
      
    }

}
