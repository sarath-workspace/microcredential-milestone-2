package com.cognizant.microcredential.authenticationservice;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cognizant.microcredential.authenticationservice.model.SignUpRequest;
import com.cognizant.microcredential.authenticationservice.model.User;
import com.cognizant.microcredential.authenticationservice.utils.CognitoUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

public class SignUpServiceApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson GSON = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();

    private static final String USER_POOL_ID = System.getenv("USER_POOL_ID");

    private static final String ADD_USER_FUNCTION_NAME = System.getenv("ADD_USER_FUNCTION_NAME");

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        System.out.println("------------>>>" + input.toString());

        SignUpRequest signUpRequest = GSON.fromJson(input.getBody(), SignUpRequest.class);

        User user = new User();

        user.setFirstName(signUpRequest.getFirstname());
        user.setLastName(signUpRequest.getLastname());
        user.setEmail(signUpRequest.getEmail());
        user.setDateOfBirth(Date.valueOf(signUpRequest.getDateofbirth()));

        User addedUser = addUserDetails(user, input);

        AWSCognitoIdentityProvider cognitoClient = CognitoUtil.getAmazonCognitoIdentityClient();
        AdminCreateUserRequest cognitoRequest = new AdminCreateUserRequest()
                .withUserPoolId(USER_POOL_ID)
                .withUsername(signUpRequest.getUsername())
        .withUserAttributes(
                new AttributeType()
                        .withName("email")
                        .withValue(signUpRequest.getEmail()),
                new AttributeType()
                        .withName("name")
                        .withValue(signUpRequest.getFirstname()),
                new AttributeType()
                        .withName("family_name")
                        .withValue(signUpRequest.getLastname()),
                new AttributeType()
                        .withName("birthdate")
                        .withValue(signUpRequest.getDateofbirth()),
                new AttributeType()
                        .withName("profile")
                        .withValue(String.valueOf(addedUser.getId())),
                new AttributeType()
                        .withName("email_verified")
                        .withValue("true"))

                .withTemporaryPassword("TEMPORARY_PASSWORD")

              .withMessageAction("SUPPRESS")
                .withDesiredDeliveryMediums(DeliveryMediumType.EMAIL)
                .withForceAliasCreation(Boolean.FALSE);

        AdminCreateUserResult createUserResult =  cognitoClient.adminCreateUser(cognitoRequest);
        UserType cognitoUser =  createUserResult.getUser();

        return response
                .withStatusCode(200)
                .withBody(GSON.toJson(cognitoUser, UserType.class));
    }

    private User addUserDetails(User user, final APIGatewayProxyRequestEvent input) {

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withResource("/api/user/add")
                .withPath("/api/user/add")
                .withBody(GSON.toJson(user, User.class))
                .withHeaders(input.getHeaders())
                .withMultiValueHeaders(input.getMultiValueHeaders())
                .withRequestContext(input.getRequestContext())
                .withIsBase64Encoded(false);

        System.out.println("ORDER REQUEST>>>>>>>>>>>>>>>>>>" + request);

        User createdUser = null;

        try {
            InvokeResult invokeResult = AWSLambdaClientBuilder.standard()
                    .withRegion(Regions.AP_SOUTH_1)
                    .withCredentials(new EnvironmentVariableCredentialsProvider()).build().invoke(new InvokeRequest()
                            .withFunctionName(ADD_USER_FUNCTION_NAME)
                            .withPayload(GSON.toJson(request)));

            String ans = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);
            System.out.println("::::::::::::::::::::" + ans);
            APIGatewayProxyResponseEvent response = GSON.fromJson(ans, APIGatewayProxyResponseEvent.class);

            System.out.println("+_+_+_+_+_+_+_+_+_>>>>>>>>>>"+ response.getBody());

            createdUser = GSON.fromJson(response.getBody(), User.class);

            System.out.println("::::::::::::::::::::" + ans);
            System.out.println(":::::::::::::" + invokeResult.getStatusCode());

        } catch (ServiceException e) {
            System.out.println("+++++++++++++++++" + e);
        } catch (Exception e) {
            System.out.println("~~~~~~~~~~~~~~~~>>>" + e);
        }

        return createdUser;
    }

}