package com.azurefunction.example.retrieveVoteReport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;
import java.util.logging.Logger;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

public class Reports
{
	private Logger logger;
	
	public Reports()
	{
	}

	public String getElectionReport(Logger logger)
	{
		String retVal = "No Results";
		
		try
		{
			this.logger = logger;
			
			logger.info("Loading application properties");
			Properties properties = new Properties();
			properties.load(Function.class.getClassLoader().getResourceAsStream("application.properties"));

			logger.info("Connecting to the database");
			logger.info("URL: " + properties.getProperty("url")); // + logKey);
			logger.info("user: " + properties.getProperty("user"));
			logger.info("password: " + properties.getProperty("password"));

			Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("user"), properties.getProperty("password"));
			logger.info("Database connection test: " + connection.getCatalog());

			logger.info("Closing database connection");
			connection.close();
			
			AbandonedConnectionCleanupThread.uncheckedShutdown();
		} catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage());
			return "";
		}
		
		return retVal;
	}
}
