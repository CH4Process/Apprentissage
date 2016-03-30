package com.TestJavaWeb.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.TestJavaWeb.beans.BDDRequest;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory;
import com.CH4Process.Database.BDD;
import com.CH4Process.PiSenseHat.SenseHat;

/**
 * Servlet implementation class ServletTest
 */
@WebServlet("/ServletTest")
public class ServletTest extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	// Variables for reading the values in the Pi Sense Hat
	private static I2CBus bus;
	private static float temperature;
	
	// Objects handling the SenseHat and the Database for storing the SenseHat readings
	private static SenseHat senseHat;
	private static BDD bdd;
	BDDRequest bddrequest;
	
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
			// Connection to the I2CBus of the Pi and reading a temperature value
			bus = I2CFactory.getInstance(I2CBus.BUS_1);
			System.out.println("Connected to I2CBus !");

			senseHat = new SenseHat(bus);
			temperature = senseHat.getTemperatureFromHumidity();

			//System.out.println("Temperature read from humidity sensor : " + temperature + "°C");

			// Storing the value in database
			bdd = new BDD();
			bdd.Connect("192.168.0.7","bdd_java", "java", "javabddpwd");
			bdd.LogTemperature(temperature);
			bdd.Disconnect();
			
			bus.close();
			bus = null;
			senseHat.close();
			senseHat = null;
			bdd = null;
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		
		// Custom object to get datas from the database
		bddrequest = new BDDRequest();
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

}
