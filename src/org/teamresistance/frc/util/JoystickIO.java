package org.teamresistance.frc.util;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import org.teamresistance.frc.util.joystick.Button;
import org.teamresistance.frc.util.joystick.CodriverBox;

import java.util.ArrayList;

/**
 * Created by shrey on 2/20/2017.
 */
public class JoystickIO {
  // Joysticks
  public static Joystick leftJoystick = new Joystick(0);
  public static Joystick rightJoystick = new Joystick(1);
  public static Joystick coJoystick = new Joystick(2);
  public static CodriverBox codriverBox = new CodriverBox(3);

  //Buttons
  private static ArrayList<Button> buttons = new ArrayList<>();

  public static Button btnShooter = createButton(coJoystick, 1);
  public static Button btnAgitator = createButton(coJoystick, 3);
  public static Button btnClimber = createButton(coJoystick, 8);

  public static Button btnSnorflerIn = createButton(coJoystick, 6);

  public static Button btnChangeDrive = createButton(leftJoystick, 8);
  
  public static Button btnPickupGear = createButton(coJoystick, 2);
  public static Button btnPlaceGear = createButton(coJoystick, 5);

  public static void update() {
    for (Button b: buttons) {
      b.update();
    }
    codriverBox.update(Time.getDelta());
  }

  private static Button createButton(GenericHID stick, int button) {
    Button newButton = new Button(stick, button);
    buttons.add(newButton);
    return newButton;
  }
}
