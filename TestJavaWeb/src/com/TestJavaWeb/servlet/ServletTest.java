package com.TestJavaWeb.servlet;

import java.io.IOException;
import java.net.Inet4Address;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.TestJavaWeb.beans.BDDRequest;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.yoctopuce.YoctoAPI.YAPI;
import com.CH4Process.Database.BDD;
import com.CH4Process.PiSenseHat.SenseHat;

import com.yoctopuce.YoctoAPI.*;

/**
 * Servlet implementation class ServletTest
 */
@WebServlet("/ServletTest")
public class ServletTest extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private static Integer source = 1; // 0 for SenseHat, 1 for Yoctopuce
	private static String sourceAddress = "ch4pi1.ddns.net:4444"; // Address of the source of data (PiSenseHat of YoctoTemperature)
	private static String databaseAddress = "192.168.0.7";
	private static String databaseName = "bdd_java";
	private static String databaseUser = "java";
	private static String databasePassword = "javabddpwd";
	
	// Variables for reading the values in the Pi Sense Hat
	private static I2CBus bus;
	private static float temperature;
	
	// Objects handling the SenseHat and the Database for storing the SenseHat readings
	private static SenseHat senseHat;
	private static BDD bdd;
	BDDRequest bddrequest;
	
	// Objects handling the Yoctopuce API
	YTemperature temperatureSensor;
	
	// Debugging stuff
	private static String ipaddress;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ServletTest() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// This method handles the HTTP request and transfers it to the JSP
		try
		{
			ipaddress = Inet4Address.getLocalHost().getHostAddress();
			
		
			switch(source) 
			{
			case 0: temperature = getTemperatureFromSenseHat(ipaddress, sourceAddress); break;

			case 1: temperature = getTemperatureFromYoctoTemp(sourceAddress); break;

			default: temperature = 0.0f; break;
			}

			// Storing the value in database
			bdd = new BDD();
			bdd.Connect(databaseAddress,databaseName, databaseUser, databasePassword);
			bdd.LogTemperature(temperature);
			bdd.Disconnect();
			bdd = null;
			

		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		// Custom object to get datas from the database
		bddrequest = new BDDRequest(databaseAddress,databaseName, databaseUser, databasePassword);
		bddrequest.makeRequest("SELECT VALUE, DATE from MEASURES ORDER BY DATE ASC;");
		
		// Passing the custom class to the JSP
		request.setAttribute("bddrequest", bddrequest);
		
		// Giving the HTTP requezst to the JSP
		this.getServletContext().getRequestDispatcher("/WEB-INF/googlecharts.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * NOT USED
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private Float getTemperatureFromSenseHat(String localIPAddress, String sourceIPAddress)
	{
		if (localIPAddress == sourceIPAddress)
		{
			try
			{
				bus = I2CFactory.getInstance(I2CBus.BUS_1);

				senseHat = new SenseHat(bus);
				Float temp = senseHat.getTemperatureFromHumidity();
				bus.close();
				bus = null;
				senseHat.close();
				senseHat = null;
				return temp;
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return 0.0f;
			}
		}
		else
		{
			return 0.0f;
		}
	}

	private Float getTemperatureFromYoctoTemp(String address)
	{
		try
		{
			YAPI.RegisterHub(address);
			temperatureSensor = YTemperature.FirstTemperature();
			Float temp = (float) temperatureSensor.get_currentValue();
			return temp;
		}
		catch(YAPI_Exception ex)
		{
			ex.printStackTrace();
			System.out.println(ex.getMessage());
			return 0.0f;
		}
		finally
		{
			try
			{
				YAPI.FreeAPI();
			}
			catch (Exception ex)
			{
				System.out.println(ex.getMessage());
			}
		}
	}
}
