package com.azurefunction.example.retrieveVoteReport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.logging.Logger;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import com.azure.identity.DefaultAzureCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.identity.EnvironmentCredential;
import com.azure.identity.EnvironmentCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;

public class Reports
{
	private Logger logger;	
	private static final String KEY_VALUT_NAME = "azurefunctionsecrets";
	
	public Reports()
	{
	}

	public String getElectionReport(Logger logger)
	{
		String htmlRows = "<html><head></head><body><table>";
		
		try
		{
			this.logger = logger;

			//Map<String, String> env = System.getenv();
	        //for (String envName : env.keySet()) {
	        //    System.out.format("%s=%s%n",
	        //                      envName,
	        //                      env.get(envName));
	        //}
			
			logger.info("Loading secrets");
			String keyVaultName = KEY_VALUT_NAME;
			String keyVaultUri = "https://" + keyVaultName + ".vault.azure.net";
			logger.info("keyVaultUri: " + keyVaultUri);

		    //DefaultAzureCredential defaultCredential = new DefaultAzureCredentialBuilder().build();
			EnvironmentCredential defaultCredential = new EnvironmentCredentialBuilder().build();
			logger.info("Credential Builder created");
			
			SecretClient secretClient = new SecretClientBuilder()
			    .vaultUrl(keyVaultUri)
			    .credential(defaultCredential)
			    .buildClient();		
			logger.info("Secret Client created");
			
			String url = secretClient.getSecret("url").getValue();
			String user = secretClient.getSecret("user").getValue();
			String password = secretClient.getSecret("password").getValue();
			//logger.info("URL: " + url);
			//logger.info("user: " + user);
			//logger.info("password: " + password);

			Connection connection = DriverManager.getConnection(url, user, password);
			logger.info("Database connection test: " + connection.getCatalog());
			
			/*
			logger.info("Loading application properties");
			Properties properties = new Properties();
			properties.load(Function.class.getClassLoader().getResourceAsStream("application.properties"));

			logger.info("Connecting to the database");
			logger.info("URL: " + properties.getProperty("url")); // + logKey);
			logger.info("user: " + properties.getProperty("user"));
			logger.info("password: " + properties.getProperty("password"));

			Connection connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("user"), properties.getProperty("password"));
			logger.info("Database connection test: " + connection.getCatalog());
			*/
			
			htmlRows += "<tr>";
			htmlRows += "<td>Vote</td>";
			htmlRows += "<td>Count</td>";
			htmlRows += "</tr>";
			
			String sqlSelect = "select voteName, count(vote) as totalVote from votes v join voteTypes vt on v.vote = vt.idVoteType group by v.vote";
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
		htmlRows += "</table></body></html>";
		
		return htmlRows;
	}
}
