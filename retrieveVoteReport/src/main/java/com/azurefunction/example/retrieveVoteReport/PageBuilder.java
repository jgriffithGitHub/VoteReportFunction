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
		int retVal = 0;
		try
		{
			PreparedStatement selectStatement = connection
					.prepareStatement("select * from electionDetails where startDate <= current_date() and endDate > current_date();");
	
			ResultSet rs = selectStatement.executeQuery();
			if(rs.next())
			{
				log.info("Found a row");
				electionId = rs.getInt("idElection");
				retVal = electionId;
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
		
		return retVal;
	}
	
	public void setTitle(Logger log)
	{
		try
		{
			log.info("Setting title");
			
			PreparedStatement selectStatement = connection
					.prepareStatement("select * from electionDetails where idElection = " + electionId + ";");
	
			ResultSet rs = selectStatement.executeQuery();
			if(rs.next())
			{
				log.info("Found a row");
				String title = rs.getString("electionTitle");
				log.info("Title = " + title);
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

			/*
			DatabaseConnection dbConn = new DatabaseConnection();
			Connection connection = dbConn.getConnection();
			
			PreparedStatement selectStatement = connection
					.prepareStatement("select * from voteTypes where electionId = " + electionId + ";");
	
			String voteButtons = "";
			ResultSet rs = selectStatement.executeQuery();
			int count = 0;
			while(rs.next())
			{
				log.info("Found a row");
				voteButtons += "<div class=\"form-check\">\n";
				voteButtons += "<input class=\"form-check-input\" type=\"radio\" name=\"vote\" id=\"vote" + (++count) + "\" value=\"" + rs.getString("idVoteType") + "\" >\n";
				voteButtons += "<label class=\"form-check-label\" for=\"vote" + count + "\">";
				voteButtons += rs.getString("voteName");  //rs.getString("idVoteType")
				voteButtons += "</label>\n";
				voteButtons += "</div>\n";
			}
			*/
			
			uiTemplate = uiTemplate.replaceAll(VOTE_RESULTS, voteResults);
			//log.info(count + " rows returned");
		}
		catch(Exception e)
		{
			log.info("Exception: ");
			e.printStackTrace();
		}
	}
}
