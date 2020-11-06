package com.cognizant.microcredential.authenticationservice;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cognizant.microcredential.authenticationservice.model.AuthenticationRequest;
import com.cognizant.microcredential.authenticationservice.utils.CognitoUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class SignInServiceApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson GSON = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();

    private static final String USERNAME = "username";

    private static final String PASS_WORD = "password";

    private static final String NEW_PASS_WORD = "newpassword";

    private static final String CLIENT_ID = System.getenv("CLIENT_ID");

    private static final String USER_POOL_ID = System.getenv("USER_POOL_ID");

    private static final String NEW_PASS_WORD_REQUIRED = "NEW_PASS_WORD_REQUIREDd";

    private static final String USER_MUST_PROVIDE_A_NEW_PASS_WORD = "new password is required cont proceed with the temporary password";

    private static final String USER_MUST_DO_ANOTHER_CHALLENGE = "invalid credential provided";

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        System.out.println("------------>>>" + input.toString());

        AuthenticationResultType authenticationResult = null;
        AWSCognitoIdentityProvider cognitoClient = CognitoUtil.getAmazonCognitoIdentityClient();

        AuthenticationRequest authenticationRequest = new AuthenticationRequest();

        final Map<String, String>authParams = new HashMap<>();
        authParams.put(USERNAME, authenticationRequest.getUsername());
        authParams.put(PASS_WORD, authenticationRequest.getPassword());

        final AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest();
        authRequest.withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withClientId(CLIENT_ID)
                .withUserPoolId(USER_POOL_ID)
        .withAuthParameters(authParams);

        AdminInitiateAuthResult result = cognitoClient.adminInitiateAuth(authRequest);

        //Has a Challenge
        if(null != result.getChallengeName() && result.getChallengeName().length() != 0) {
            //If the challenge is required new Password validates if it has the new password variable.
            if(NEW_PASS_WORD_REQUIRED.equals(result.getChallengeName())){
                if(null == authenticationRequest.getNewpassword()) {
                    throw new IllegalArgumentException(USER_MUST_PROVIDE_A_NEW_PASS_WORD);
                }else{
                    //we still need the username
                    final Map<String, String> challengeResponses = new HashMap<>();
                    challengeResponses.put(USERNAME, authenticationRequest.getUsername());
                    challengeResponses.put(PASS_WORD, authenticationRequest.getPassword());
                    //add the new password to the params map
                    challengeResponses.put(NEW_PASS_WORD, authenticationRequest.getNewpassword());
                    //populate the challenge response
                    final AdminRespondToAuthChallengeRequest request =
                            new AdminRespondToAuthChallengeRequest();
                    request.withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
                            .withChallengeResponses(challengeResponses)
                            .withClientId(CLIENT_ID)
                            .withUserPoolId(USER_POOL_ID)
                            .withSession(result.getSession());

                    AdminRespondToAuthChallengeResult resultChallenge =
                            cognitoClient.adminRespondToAuthChallenge(request);
                    authenticationResult = resultChallenge.getAuthenticationResult();
                }
            }else{
                //has another challenge
                throw new IllegalArgumentException(USER_MUST_DO_ANOTHER_CHALLENGE);
            }
        }else{
            //Doesn't have a challenge
            authenticationResult = result.getAuthenticationResult();
        }
        cognitoClient.shutdown();

        return response
                .withStatusCode(200)
                .withBody(GSON.toJson("{\"success\": \"true\", \"message\" : \"login success\"", UserType.class));
    }
}