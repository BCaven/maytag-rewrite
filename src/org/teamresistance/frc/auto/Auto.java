package org.teamresistance.frc.auto;

import java.util.ArrayList;

import org.teamresistance.frc.io.IO;
import org.teamresistance.frc.util.Time;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Auto {
	
	ArrayList<AutoMode> modes = new ArrayList<>();
	private int currentMode = 0;
	
	public void init() {
		currentMode = (int) SmartDashboard.getNumber("Auto Mode", currentMode);
		
		modes.add(new AutoTimedShoot());
		modes.add(new ShootThenDriveOverLineAutoMode());
		
		for(int i = 0; i < modes.size(); i++) {
			SmartDashboard.putNumber(modes.get(i).toString(), i);
		}
		
	    IO.drive.init(IO.navX.getAngle(), 0.06, 0.0, 0.0);
	    IO.ofs.init();
	    
	    modes.get(currentMode).init();
	}
	
	public void update() {
		Time.update();
	    IO.ofs.update();
	    
	    SmartDashboard.putNumber("OFS X", IO.ofs.getX());
	    SmartDashboard.putNumber("OFS Y", IO.ofs.getY());
	    SmartDashboard.putNumber("OFS Magnitude", IO.ofs.getPos().length());
	    
	    modes.get(currentMode).update();		
	}

}
