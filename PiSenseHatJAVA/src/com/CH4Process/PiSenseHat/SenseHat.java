/*
 * 
 * Code redacted by Alexandre Charreton
 * For testing, fun and example for CH4Process project
 * 
 * This class is supposed to be a JAVA interface to the Raspberry Pi Sense Hat
 * 
 * Initial : 26/02/2016
 * 
 */

//import java.io.IOException;
package com.CH4Process.PiSenseHat;

import com.pi4j.io.i2c.I2CBus;

public class SenseHat 
{
	
	// Class variables
	HTS221 HumiditySensor;
	I2CBus bus;
	
	public SenseHat(I2CBus bus)
	{
		this.bus = bus;
		HumiditySensor = new HTS221(this.bus);
	}
	
	public float getTemperatureFromHumidity()
	{
		return HumiditySensor.getTemperature();
	}
	
	public void close()
	{
		HumiditySensor.close();
		HumiditySensor = null;
	}

}
