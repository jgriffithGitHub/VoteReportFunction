package com.azurefunction.example.retrieveVoteReport;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

public class PageBuilder
{
	private final String TITLE_MARKER = "\\{Title\\}";
	private final String VOTE_RESULTS = "\\{VoteResults\\}";
	
	String uiTemplate = "<html><head></head><body>Base Page Not Loaded</body></html>";
	DatabaseConnection dbConn = null;
	private Connection connection = null;
	private int electionId = 0;
	
	public PageBuilder()
	{
		dbConn = new DatabaseConnection();
		connection = dbConn.getConnection();
	}

	public void loadTemplate()
	{
		try
		{
			InputStream is = Function.class.getClassLoader().getResourceAsStream("baseUi.html");
			byte[] htmlData = is.readAllBytes();			
			uiTemplate = new String(htmlData);
	 	} 
		catch (IOException e)
		{
			uiTemplate = "<html><head></head><body>Exception:<br>" + e.getMessage() + "</body></html>";
		}
		
	}

	public String getPage()
	{
		return uiTemplate;
	}
	
	public int setElectionId(Logger log)
	{
		try
		{
			PreparedStatement selectStatement = connection
					.prepareStatement("select * from electionDetails where startDate <= current_date() and endDate > current_date();");
	
			ResultSet rs = selectStatement.executeQuery();
			if(rs.next())
			{
				log.info("Found a row");
				electionId = rs.getInt("idElection");
			}
			else
			{
				log.info("No rows returned");
			}
		}
		catch(Exception e)
		{
			log.info("Exception: ");
			e.printStackTrace();
		}
		
		return electionId;
	}
	
	public void setTitle(Logger log)
	{
		try
		{
			PreparedStatement selectStatement = connection
					.prepareStatement("select * from electionDetails where idElection = " + electionId + ";");
	
			ResultSet rs = selectStatement.executeQuery();
			if(rs.next())
			{
				String title = rs.getString("electionTitle");
				uiTemplate = uiTemplate.replaceAll(TITLE_MARKER, title);
			}
			else
			{
				log.info("No rows returned");
			}
		}
		catch(Exception e)
		{
			log.info("Exception: ");
			e.printStackTrace();
		}
	}

	public void setVoteResults(int electionId, Logger log)
	{
		try
		{
			log.info("Setting vote results");
			String voteResults = Reports.getElectionReport(electionId, log);
			
			uiTemplate = uiTemplate.replaceAll(VOTE_RESULTS, voteResults);
		}
		catch(Exception e)
		{
			log.info("Exception: ");
			e.printStackTrace();
		}
	}
}
