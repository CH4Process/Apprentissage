package com.ch4process.beans;

import java.math.RoundingMode;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @author Alex
 *
 */

public class BDDRequest 
{
	final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	final String URL = "jdbc:mysql://";
	final String DATABASEPORT = "3306";
	private String databaseUser = "";
	private String databasePassword = "";
	private String databaseName = "";
	private String connectionString = "";
	private String databaseAddress = "";

	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultset = null;
	
	
	
	
	/**
	 * @param ip = IP Address of the database. Can be the string "localhost"
	 * @param database = Database name
	 * @param user = User with rights on the database
	 * @param password = Password of the User
	 * Constructor of the class
	 */
	public BDDRequest(String ip, String database, String user, String password)
	{
		this.databaseAddress = ip;
		this.databaseName = database;
		this.databaseUser = user;
		this.databasePassword = password;
		
		this.connectionString = URL + databaseAddress + ":" + DATABASEPORT + "/" + databaseName;
		
		try
		{
			// Trying to get the driver
			Class.forName(JDBC_DRIVER);
			// Creating the connection to the SQL server using the user password and connection string provided
			connection = DriverManager.getConnection(connectionString, databaseUser, databasePassword);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @param request String that represents a correct SQL SELECT query
	 */
	public int LogTemperature(int temperature, String capteur_id)
	{
		try
		{
			statement = connection.createStatement();
			
			// And insert in the the database
			int status = statement.executeUpdate("INSERT INTO mesure (valeur, capteur_id, datetime) VALUES (" + Integer.toString(temperature) + "," + capteur_id + ", NOW());");
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
				statement = null;
			}
			catch (SQLException ex)
			{
				// Rien
			}
		}
	}
	
	public void makeRequest(String request)
	{
		try
		{
			// Creating the object that will handle the request
			statement = connection.createStatement();
			// Executing the request and acquiring the result in a resultset object
			resultset = statement.executeQuery(request);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public Boolean nextResult()
	{
		try
		{
			return resultset.next();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
			return false;
		}
	}
	
	public void closeResultset()
	{
		try
		{
			resultset.close();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			resultset = null;
		}
	}
	
	public void closeStatement()
	{
		try
		{
			statement.close();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			statement = null;
		}
	}
	
	public void close()
	{
		try
		{
			closeResultset();
			closeStatement();
			connection.close();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			connection = null;
		}
	}

	public String getString(String columnLabel)
	{
		try
		{
			return resultset.getString(columnLabel);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	public Integer getInteger(String columnLabel)
	{
		try
		{
			return resultset.getInt(columnLabel);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	public Float getFloat(String columnLabel)
	{
		try
		{
			return resultset.getFloat(columnLabel);
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	public Long getDate(String columnLabel)
	{
		try
		{
			Timestamp date = resultset.getTimestamp(columnLabel);
			return date.getTime();
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
			return null;
		}
	}
	
}
