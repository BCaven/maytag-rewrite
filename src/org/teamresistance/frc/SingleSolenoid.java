package org.teamresistance.frc;


/**
 * @author Shreya Ravi
 */
public interface SingleSolenoid {
  void extend();
  void retract();
  boolean isExtended();
  boolean isRetracted();
}
