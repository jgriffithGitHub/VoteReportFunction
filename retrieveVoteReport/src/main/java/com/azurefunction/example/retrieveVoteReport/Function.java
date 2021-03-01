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

        // Parse query parameter
        //final String query = request.getQueryParameters().get("name");
        //final String name = request.getBody().orElse(query);

        //if (name == null) {
        //    return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        //} else {
        //    return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        //}
        
        Reports report = new Reports();
    	String reportHtml = report.getElectionReport(logger);
        return request.createResponseBuilder(HttpStatus.OK).body(reportHtml).build();
    }
}
