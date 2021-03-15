package com.azurefunction.example.retrieveVoteReport;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {

    @FunctionName("RetrieveVoteReport")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
    	Logger logger = context.getLogger();
        logger.info("Java HTTP trigger processed a request.");

        String reportHtml = "";
        try
        {
        	PageBuilder pb = new PageBuilder();
        	int electionId = pb.setElectionId(logger);
        	pb.loadTemplate();
        	pb.setTitle(logger);        	
	    	pb.setVoteResults(electionId, logger);
	    	reportHtml = pb.getPage();
        }
        catch(Exception e)
        {
            return request.createResponseBuilder(HttpStatus.OK).body("Exception: " + e.getMessage()).build();
        }
    	
        return request.createResponseBuilder(HttpStatus.OK).header("Content-Type", "text/html").body(reportHtml).build();
    }
}
