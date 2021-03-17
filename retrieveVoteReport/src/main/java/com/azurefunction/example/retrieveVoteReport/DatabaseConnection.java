package com.azurefunction.example.retrieveVoteReport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DatabaseConnection
{
	Properties properties = null;
	Connection connection = null;
			
	public DatabaseConnection()
	{
		try
		{
			properties = new Properties();
			properties.load(Function.class.getClassLoader().getResourceAsStream("application.properties"));
	
			connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("user"), properties.getProperty("password"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public Connection getConnection()
	{
		return connection;
	}
}
