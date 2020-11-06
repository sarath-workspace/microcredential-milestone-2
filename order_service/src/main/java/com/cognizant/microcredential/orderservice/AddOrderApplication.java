package com.cognizant.microcredential.orderservice;

import com.amazonaws.auth.*;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cognizant.microcredential.orderservice.model.Order;
import com.cognizant.microcredential.orderservice.util.HibernateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.client.CredentialsProvider;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AddOrderApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Gson GSON = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();

    private static final String GET_USER_SERVICE = System.getenv("GET_USER_SERVICE");
    private static final String APP_ACCESS_CODE = System.getenv("APP_ACCESS_CODE");
    private static final String APP_SECRET_CODE = System.getenv("APP_SECRET_CODE");

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        System.out.println("------------>>>"+ input.toString());
        Order order = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            order = GSON.fromJson(input.getBody(), Order.class);
            checkUser(order.getUserid(), input);
            session.save(GSON.fromJson(input.getBody(), Order.class));
            System.out.println("ORDER_SAVED::" + order);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("ERROR::" + e.getMessage());
            e.printStackTrace();
            return response
                    .withStatusCode(500)
                    .withBody(String.format("{\"success\": false, \"message\" : \"%s\"}", e.getMessage()));
        }

        return response
                .withStatusCode(200)
                .withBody(GSON.toJson(order));
    }

    private boolean checkUser(long userid, APIGatewayProxyRequestEvent input) {

        Map<String, String> pathParam = new HashMap<>();
        pathParam.put("id", String.valueOf(userid));
/*
{
	resource: /api/user/view/{id},
	path: /api/user/view/1,
	httpMethod: GET,
	headers: ,
    multiValueHeaders: ,
	pathParameters: {id=1},
	requestContext: {accountId: 414236375696,resourceId: o7xs5h,stage: Prod,requestId: 1918a4cc-30b7-4ad5-854a-5add5cc30d2a,identity: {sourceIp: 117.197.184.75,userAgent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.75 Safari/537.36,},resourcePath: /api/user/view/{id},httpMethod: GET,apiId: evq9rohqre,path: /Prod/api/user/view/1,},
	isBase64Encoded: false
}
 */
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withResource("/api/user/view/{id}")
                .withPath(String.format("/api/user/view/%s", userid))
                .withPathParameters(pathParam)
                .withHeaders(input.getHeaders())
                .withMultiValueHeaders(input.getMultiValueHeaders())
                .withRequestContext(input.getRequestContext())
                .withIsBase64Encoded(false);

        System.out.println("REQUEST>>>>>>>>>>>>>>>>>>" + request);


        try {
            InvokeRequest invokeRequest = new InvokeRequest()
                    .withFunctionName(GET_USER_SERVICE)
                    .withPayload(GSON.toJson(request));
            System.out.println("REQUEST FORMED");
            System.out.println("AWS LAMBDA :: "+ GET_USER_SERVICE);
            System.out.println("REQUEST FORMED");


            BasicAWSCredentials credential = new BasicAWSCredentials(APP_ACCESS_CODE,APP_SECRET_CODE);

            AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
                    .withRegion(Regions.AP_SOUTH_1)
                    .withCredentials(new AWSStaticCredentialsProvider(credential)).build();

            InvokeResult invokeResult = null;
            //AWSCredentialsProvider tt = new EnvironmentVariableCredentialsProvider();
            //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+tt.getCredentials().getAWSAccessKeyId());
            //System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+tt.getCredentials().getAWSSecretKey());
            //AWSLambda awsLambda = AWSLambdaClientBuilder.standard()//defaultClient();
            //        .withCredentials(new EnvironmentVariableCredentialsProvider())
            //        .withRegion(Regions.AP_SOUTH_1).build();
            //awsLambda.listFunctions().getFunctions().forEach(data -> System.out.println(":::::::::>>>>>)))))))))))"+data.getFunctionName()));
            System.out.println("Lambda FORMED");
            invokeResult = awsLambda.invoke(invokeRequest);

            System.out.println("Invoke Done");

            String ans = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);

            //write out the return value
            System.out.println("::::::::::::::::::::" + ans);
            System.out.println(":::::::::::::" + invokeResult.getStatusCode());

        } catch (ServiceException e) {
            System.out.println("+++++++++++++++++"+e);
        } catch (Exception e) {
            System.out.println("~~~~~~~~~~~~~~~~>>>"+e);
        }

        return true;
    }

}