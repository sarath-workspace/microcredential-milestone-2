package com.cognizant.microcredential.authenticationservice;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cognizant.microcredential.authenticationservice.model.ChangepasswordRequest;
import com.cognizant.microcredential.authenticationservice.utils.CognitoUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class ChangePasswordServiceApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson GSON = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        System.out.println("------------>>>" + input.toString());

        ChangepasswordRequest changepasswordRequest = GSON.fromJson(input.getBody(), ChangepasswordRequest.class);


        AWSCognitoIdentityProvider cognitoClient= CognitoUtil.getAmazonCognitoIdentityClient();
        ChangePasswordRequest changePasswordRequest= new ChangePasswordRequest()
                .withAccessToken(changepasswordRequest.getAccessToken())
                .withPreviousPassword(changepasswordRequest.getOldPassword())
                .withProposedPassword(changepasswordRequest.getPassword());

        cognitoClient.changePassword(changePasswordRequest);
        cognitoClient.shutdown();

        return response
                .withStatusCode(200)
                .withBody("{\"success\": \"true\", \"message\" : \"login success\"}");
    }
}