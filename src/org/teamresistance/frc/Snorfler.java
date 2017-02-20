package org.teamresistance.frc;

import org.teamresistance.frc.util.JoystickIO;

/**
 * Created by shrey on 2/20/2017.
 */
public class Snorfler {

  public void init() {

  }

  public void update() {
    if (JoystickIO.btnSnorflerIn.isDown()) {
      IO.snorflerMotor.set(1.0);
    } else {
      IO.snorflerMotor.set(0.0);
    }
  }
}
