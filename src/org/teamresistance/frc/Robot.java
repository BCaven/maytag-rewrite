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
  public Vision vision;

    @Override
    public void robotInit() {
    	IO.init();
        teleop = new Teleop();
        teleop.init();
        
        vision = new Vision();
    	vision.init();
    }

    @Override
    public void teleopInit() {
    }

    @Override
    public void teleopPeriodic() {
      IO.compressorRelay.set(IO.compressor.enabled() ? Relay.Value.kForward : Relay.Value.kOff);
      Time.update();
      JoystickIO.update();
      teleop.update();
      vision.update();
    }
    
    @Override
    public void disabledInit() {
//    	teleop.disable();
    }
}
