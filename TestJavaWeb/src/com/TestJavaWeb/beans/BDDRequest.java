package com.TestJavaWeb.beans;

import java.sql.*;
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
	
	private ArrayList<Long[]> requestResult = new ArrayList<Long[]>();
	private Long[][] requestResultArray;
	
	
	
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
	public void makeRequest(String request)
	{
		try
		{
			// Creating the object that will handle the request
			statement = connection.createStatement();
			// Executing the request and acquiring the result in a resultset object
			resultset = statement.executeQuery(request);

			// As a resultset is a list of values, we can iterate through it to manipulate the datas and stocking them in a variable
			while(resultset.next())
			{
				// This block of code basically gets a FLOAT and a DATETIME value in the database, and stores them as DOUBLE to avoid precision loss
				float value = resultset.getFloat("VALUE");
				Timestamp date = resultset.getTimestamp("DATE");
				Long[] dataset = new Long[2];

				dataset[0] = date.getTime();
				// Value x 1000 to keep the precision and get rid of the floating point value
				dataset[1] = (long) (value * 1000);
				
				// Addind the result into an array
				requestResult.add(dataset);
			}

			if (requestResult.size() > 0)
			{
				// Transfering the datas into a list because you can't mix datatypes in a Java array
				requestResultArray = new Long[requestResult.size()][];
				requestResult.toArray(requestResultArray);
			}
			
			// Closing everything and freeing the variables
			resultset.close();
			resultset = null;
			statement.close();
			statement = null;
			connection.close();
			connection = null;
		}
		catch (SQLException ex)
		{
			ex.printStackTrace();
		}
		finally
		{
			try
			{
				if( statement != null)
					statement.close(); 
					statement = null;
			}
			catch(SQLException ex)
			{
				ex.printStackTrace();
			}
			try
			{
				if(connection != null)
					connection.close();
					connection = null;
			}
			catch (SQLException ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @return This method returns the result of the query executed by makeRequest
	 */
	public Long[][] getRequestResultArray()
	{
		return this.requestResultArray;
	}

	
}
