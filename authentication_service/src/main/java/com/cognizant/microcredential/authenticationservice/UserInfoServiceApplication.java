package com.cognizant.microcredential.authenticationservice;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cognizant.microcredential.authenticationservice.model.UserDetails;
import com.cognizant.microcredential.authenticationservice.utils.CognitoUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoServiceApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson GSON = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();

    private static final String USER_POOL_ID = "USER_POOL_ID";

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        String username = input.getQueryStringParameters().get("username");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        System.out.println("------------>>>" + input.toString());

        AWSCognitoIdentityProvider cognitoClient = CognitoUtil.getAmazonCognitoIdentityClient();
        AdminGetUserRequest userRequest = new AdminGetUserRequest()
                .withUsername(username)
                .withUserPoolId(USER_POOL_ID);


        AdminGetUserResult userResult = cognitoClient.adminGetUser(userRequest);

        UserDetails userResponse = new UserDetails();
        userResponse.setUsername(userResult.getUsername());
        userResponse.setUserstatus(userResult.getUserStatus());
        userResponse.setUsercreatedate(userResult.getUserCreateDate());
        userResponse.setUserlastmodifieddate(userResult.getUserLastModifiedDate());


        List<AttributeType> userAttributes = userResult.getUserAttributes();
        for(AttributeType attribute: userAttributes) {
            switch (attribute.getName()) {
                case "email":
                    userResponse.setEmail(attribute.getValue());
                    break;
                case "name":
                    userResponse.setFirstname(attribute.getValue());
                    break;
                case "family_name":
                    userResponse.setLastname(attribute.getValue());
                    break;
                case "birthdate":
                    userResponse.setDateofbirth(attribute.getValue());
                    break;
                case "profile":
                    userResponse.setUserid(attribute.getValue());
                    break;
            }
        }

        cognitoClient.shutdown();

        return response
                .withStatusCode(200)
                .withBody(GSON.toJson(userResponse, UserDetails.class));
    }
}
