package com.TestJavaWeb.beans;

import java.sql.*;
import java.util.ArrayList;

public class BDDRequest 
{
	final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	final String USER = "java";
	final String PWD = "javabddpwd";
	//final String URL = "jdbc:mysql://localhost:3306/bdd_java";
	final String URL = "jdbc:mysql://192.168.0.7:3306/bdd_java";

	private Connection connection = null;
	private Statement statement = null;
	private ResultSet resultset = null;
	
	private ArrayList<Long[]> requestResult = new ArrayList<Long[]>();
	private Long[][] requestResultArray;
	
	
	/**
	 * Constructor of the class
	 */
	public BDDRequest()
	{
		try
		{
			// Trying to get the driver
			Class.forName("com.mysql.jdbc.Driver");
			// Creating the connection to the SQL server using the user password and connection string provided
			connection = DriverManager.getConnection(URL, USER, PWD);
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
				dataset[1] = (long) value;
				
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
