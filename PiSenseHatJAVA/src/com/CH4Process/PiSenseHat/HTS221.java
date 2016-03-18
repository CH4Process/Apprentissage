/*
 * 
 * Code redacted by Alexandre Charreton
 * For testing, fun and example for CH4Process project
 * 
 * This class is supposed to be a HTS211 sensor interface
 * This code in STRONGLY inspired by gillwei7's code property of Nordic Semiconductor
 * https://developer.mbed.org/teams/Delta/code/hts221/file/030da9425166/hts221.cpp
 * THANK YOU SO MUCH FOR THIS CODE !
 * 
 * Initial : 26/02/2016
 * 
 */
package com.CH4Process.PiSenseHat;

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;

public class HTS221
{
	// Addresses of the registers to read
	private static final int adrDevice = 0x5F;
	private static final int adrTOutL = 0x2A;
	private static final int adrTOutH = 0x2B;
	private static final int adrT0_degC_x8 = 0x32;
	private static final int adrT1_degC_x8 = 0x33;
	private static final int adrT0_OutL = 0x3C;
	private static final int adrT0_OutH = 0x3D;
	private static final int adrT1_OutL = 0x3E;
	private static final int adrT1_OutH = 0x3F;
	private static final int adrT0T1_MSB = 0x35;

	// Reception variables
	private static int TOutL;
	private static int TOutH;
	private static int T0_degC_x8;
	private static int T1_degC_x8;
	private static int T0_OutL;
	private static int T0_OutH;
	private static int T1_OutL;
	private static int T1_OutH;
	private static float T0_cal;
	private static float T1_cal;
	private static int T0T1_MSB;

	private static int T0_Out;
	private static int T1_Out;
	private static int T_Out;
	
	// Class variables
	private I2CBus bus;
	private I2CDevice device;
	
	public HTS221(I2CBus bus)
	{
		try
		{
			// Connection to the device
			this.bus = bus;
			device = this.bus.getDevice(adrDevice);
			System.out.println("Connected to HTS221 sensor !");
			
			Calibration();				
		}
		
		catch (IOException ex)
		{
			System.out.println("Error while trying to connect to the sensor. Check bus initialisation. Exception : " + ex.getMessage());
		}
	}
	
	private void Calibration()
	{
		try
		{
			// Reading of the calibration registers
			T0_degC_x8 = device.read(adrT0_degC_x8);
			T1_degC_x8 = device.read(adrT1_degC_x8);
			T0_OutL = device.read(adrT0_OutL);
			T0_OutH = device.read(adrT0_OutH);
			T1_OutL = device.read(adrT1_OutL);
			T1_OutH = device.read(adrT1_OutH);
			T0T1_MSB = device.read(adrT0T1_MSB);

			// Merging 8 bits registers into single 16 bits registers
			T0_degC_x8 = ((T0T1_MSB & 0x03) << 8) + T0_degC_x8;
			T1_degC_x8 = ((T0T1_MSB & 0x0C) << 6) + T1_degC_x8;
			T0_Out = (T0_OutH << 8) + T0_OutL;
			T1_Out = (T1_OutH << 8) + T1_OutL;
			
			// Handling of negative values
			if ((T0_Out&0x8000) > 0) {T0_Out = -(0x8000-(0x7fff&T0_Out));};
			if ((T1_Out&0x8000) > 0) {T1_Out = -(0x8000-(0x7fff&T1_Out));};

			T0_cal = (float) T0_degC_x8/8;
			T1_cal = (float) T1_degC_x8/8;
		}
		catch (IOException ex)
		{
			System.out.println("Error while reading values : " + ex.getMessage());
		}
		
	}

	private float LinearInterpolation(int x0, float y0, int x1, float y1, int measure)
	{
		try
		{
			// Finding a and b factors in the y = ax + b equation of the linear interpolation
			float a = (float) ((y1 - y0) / (x1 - x0));
			float b = (float) -a*x0 + y0;
			float y = (float) a * measure + b;

			return y;
		}
		
		catch (Exception ex)
		{
			System.out.println("Calculation error : " + ex.getMessage());
			return (float) -1;
		}
	}
	
	public float getTemperature()
	{
		try
		{
			// Reading the value in the sensor
			TOutL = device.read(adrTOutL);
			TOutH = device.read(adrTOutH);
			
			// Merging the 8 bits register in a single 16 bits register
			T_Out = (TOutH << 8) + TOutL;
			
			// Handling of negative values
			if ((T_Out&0x8000) > 0) {T_Out = -(0x8000-(0x7fff&T_Out));};
			
			// Linear interpolation to get the usable value of the sensor reading
			return LinearInterpolation(T0_Out, T0_cal, T1_Out, T1_cal, T_Out);
			
		}
		catch (IOException ex)
		{
			System.out.println("Error while reading temperature : " + ex.getMessage());
			return (float) -1;
		}
	}
	
}
