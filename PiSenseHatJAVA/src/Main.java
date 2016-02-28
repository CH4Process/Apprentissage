/* 
 * 
 * Code redacted by Alexandre Charreton
 * For testing, fun and example for CH4Process project
 * 
 * Initial : 26/02/2016
 * 
 */

import java.io.IOException;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;

public class Main {
	
	private static I2CBus bus;
	private static SenseHat senseHat;
	private static BDD bdd;
	private static float temperature;

	public static void main(String[] args) 
	{
		try
		{
			bus = I2CFactory.getInstance(I2CBus.BUS_1);
			System.out.println("Connected to I2CBus !");
			
			senseHat = new SenseHat(bus);
			temperature = senseHat.getTemperatureFromHumidity();
			
			System.out.println("Temperature read from humidity sensor : " + temperature + "°C");
			
			bdd = new BDD();
			bdd.Connect("localhost","bdd_java", "java", "javabddpwd");
			int resultat = bdd.LogTemperature(temperature);
			System.out.println("Log en base : " + resultat + " elements inseres.");
		}
		catch (IOException ex)
		{
			System.out.println("Error while connecting to bus : " + ex.getMessage());
		}
	}

}
