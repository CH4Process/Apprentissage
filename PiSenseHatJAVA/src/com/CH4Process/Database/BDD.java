package com.CH4Process.Database;

import java.math.RoundingMode;
import java.sql.*;
import java.text.DecimalFormat;

public class BDD 
{
	private static String urlStart = "jdbc:mysql://";
	private static Connection connexion;
	private static String defaultPort = "3306";
	private static Statement statement;
	//private static ResultSet resultset;
	
	public BDD()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException ex)
		{
			System.out.println("Error : Database driver not found : " + ex.getMessage());
		}
	}
	
	public void Connect(String host,String base, String user, String pwd )
	{
		try
		{
			connexion = DriverManager.getConnection(urlStart+host+":"+defaultPort+"/"+base, user, pwd);
			statement = connexion.createStatement();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
		}
		catch (SQLException ex)
		{
			System.out.println("Error : No connection possible : " + ex.getMessage());
		}
	}

	public int LogTemperature(float temperature)
	{
		try
		{
			// To be sure that the number passed as argument will fit ! 
			DecimalFormat df = new DecimalFormat("###.###");
			df.setRoundingMode(RoundingMode.HALF_EVEN);
			temperature = Float.parseFloat(df.format(temperature));
			
			System.out.println("Value modifies to : "+ temperature);
			
			// And insert in the the database
			int status = statement.executeUpdate("INSERT INTO MEASURES (VALUE, DATE) VALUES (" + Float.toString(temperature) + ", NOW());");
			return status;
		}
		catch (SQLException ex)
		{
			System.out.println("Error while logging data : " + ex.getMessage());
			return 0;
		}
		finally
		{
			try
			{
				statement.close();
			}
			catch (SQLException ex)
			{
				// Rien
			}
		}
	}

	public void Disconnect()
	{
		try
		{
			connexion.close();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}
}
