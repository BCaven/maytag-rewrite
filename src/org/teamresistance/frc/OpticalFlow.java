package org.teamresistance.frc;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SPI.Port;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Created by Joseph for testing the Optical Flow
 *
 * Registers
 * 0x00  -   Product ID
 * 0x01  -   Revision ID
 * 0x02  -   Motion
 * 0x03  -   Delta x
 * 0x04  -   Delta y
 * 0x05  -   Squall
 */
public class OpticalFlow {
  private final SPI spi;

  // Convert for decimal values (pixel-to-feet conversion factor)
  private static final float X_LEFT_PER_FT  = 450;
  private static final float X_RIGHT_PER_FT = 450;
  private static final float Y_FWD_PER_FT   = 450;
  private static final float Y_REV_PER_FT   = 450;

  private float tot_dxLinear;
  private float tot_dyLinear;
  
  private float dxLinear;
  private float dyLinear;
  
  public OpticalFlow() {
    spi = new SPI(Port.kOnboardCS0);    //Finds the OF on the SPI ports
    spi.setChipSelectActiveLow();
    spi.setClockActiveHigh();
    spi.setClockRate(500000);
  }

  public void init() {
    int motionRegister = readRegister((byte) 2);
    SmartDashboard.putNumber("Motion Register", motionRegister);
    
    // Reinitialize all values
    tot_dxLinear = 0;
    tot_dyLinear = 0;
    dxLinear = 0;
    dyLinear = 0;
  }

  public void update() {
    SmartDashboard.putNumber("Product ID", readRegister((byte) 0));
    SmartDashboard.putNumber("Squall", readRegister((byte) 5));

    int motionRegister = readRegister((byte) 2);
    SmartDashboard.putNumber("Motion Register", motionRegister);

    // Refresh raw values after register
    float raw_dx = 0;
    float raw_dy = 0;

    // Update the raw_dx/y
    if ((motionRegister & 0x80) != 0) {
      raw_dx = readRegister((byte) 3);   //use registry to update the change in position
      raw_dy = readRegister((byte) 4);
    }

    // ---------------------------------------------- Linear ------------------------------------------------------------
    
    // Update total value
    tot_dxLinear += raw_dx;
    tot_dyLinear += raw_dy;
    
    // Find Actual Distance Covered
    float xConversion = raw_dx < 0 ? X_LEFT_PER_FT : X_RIGHT_PER_FT; // positive dx = right
    float yConversion = raw_dy < 0 ? Y_REV_PER_FT : Y_FWD_PER_FT; // positive dy = forwards
    
    dxLinear = tot_dxLinear / xConversion;
    dyLinear = tot_dyLinear / yConversion;

    // Ensure that I'm getting values
    SmartDashboard.putNumber("Raw Y", raw_dy);
    SmartDashboard.putNumber("Raw X", raw_dx);

    // Ensure that I'm getting values (Linear)
    SmartDashboard.putNumber("Total X (Linear)", tot_dxLinear);
    SmartDashboard.putNumber("Total Y (Linear)", tot_dyLinear);
    SmartDashboard.putNumber("Actual X (Linear)", dxLinear);
    SmartDashboard.putNumber("Actual Y (Linear)", dyLinear);
  }

  private int readRegister(byte register) {
    final byte[] dataReceived = new byte[] { 0 };
    final byte[] dataToSend = new byte[] { register };
    
    spi.write(dataToSend, 1); // Writes the register to be read
    spi.read(true, dataReceived, 1); // Reads the garbage
    spi.read(false, dataReceived, 1); // Reads the real register value
    return dataReceived[0];
  }

 public double getX() {
   return dxLinear; // make dxLinear Negative to go in negative X
 }

  public double getY() {
    return dyLinear;
  }
}