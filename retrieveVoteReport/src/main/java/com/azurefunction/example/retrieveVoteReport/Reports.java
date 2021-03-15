package com.azurefunction.example.retrieveVoteReport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Logger;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

public class Reports
{
	public Reports()
	{
	}

	public static String getElectionReport(int electionId, Logger logger)
	{
		String htmlRows = "<table class=\"table\">\n" + 
							"	  <thead class=\"thead-dark\">\n" + 
							"	    <tr>\n" + 
							"	      <th scope=\"col\">Vote</th>\n" + 
							"	      <th scope=\"col\">Count</th>\n" + 
							"	    </tr>\n" + 
							"	  </thead>\n" +
							"     <tbody>\n";
		
		try
		{		
			logger.info("Loading application properties");
			Properties properties = new Properties();
			properties.load(Function.class.getClassLoader().getResourceAsStream("application.properties"));

			//logger.info("Connecting to the database");
			//logger.info("URL: " + properties.getProperty("url"));
			//logger.info("user: " + properties.getProperty("user"));
			//logger.info("password: " + properties.getProperty("password"));

			Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("user"), properties.getProperty("password"));
			logger.info("Database connection test: " + connection.getCatalog());
			
			String sqlSelect = "select voteName, count(vote) as totalVote from votes v join voteTypes vt on v.vote = vt.idVoteType where idElection = " + electionId + " group by v.vote";
			PreparedStatement selectStatement = connection.prepareStatement(sqlSelect);
						
			ResultSet rs = selectStatement.executeQuery();
			while(rs.next())
			{
				htmlRows += "<tr>";
				htmlRows += "<td>" + rs.getString("voteName") + "</td>";
				htmlRows += "<td>" + rs.getInt("totalVote") + "</td>";
				htmlRows += "</tr>";
			}
			selectStatement.close();
			
			logger.info("Closing database connection");
			connection.close();
			
			AbandonedConnectionCleanupThread.uncheckedShutdown();
		} catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage());
			return "";
		}
		htmlRows += "</tbody>\n</table>";
		
		return htmlRows;
	}
}
