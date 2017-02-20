package org.teamresistance.frc;

import org.teamresistance.frc.util.MecanumDrive;
import org.teamresistance.frc.util.MecanumDrive.DriveType;

public class Auto {
	
	public void init() {
		IO.drive.setState(DriveType.KNOB_FIELD);
	}
	
	public void update() {
		
	}

}
