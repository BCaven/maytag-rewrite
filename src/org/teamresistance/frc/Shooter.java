package org.teamresistance.frc;

import org.teamresistance.frc.util.JoystickIO;

/**
 * Created by shrey on 2/20/2017.
 */
public class Shooter {

  public void init() {

  }

  public void update() {
    if (JoystickIO.btnShooter.isDown()) {
      IO.shooterMotor.set(0.80);
      IO.feederMotor.set(1.0);
      if(JoystickIO.btnAgitator.isDown()) {
        IO.agitatorMotor.set(0.3);
      } else {
        IO.agitatorMotor.set(0.0);
      }
    } else {
      IO.shooterMotor.set(0.0);
      IO.agitatorMotor.set(0.0);
      IO.feederMotor.set(0.0);
    }
  }

}
