package com.cognizant.microcredential.checkoutservice;

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;
import com.amazonaws.services.lambda.model.ServiceException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cognizant.microcredential.checkoutservice.model.*;
import com.cognizant.microcredential.checkoutservice.util.HibernateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OTPVerificationApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson GSON = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();

    private static final String GET_USER_DETAILS_SERVICE = System.getenv("GET_USER_DETAILS_SERVICE");

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        System.out.println("------------>>>" + input.toString());

        CheckoutRequest checkout = GSON.fromJson(input.getBody(), CheckoutRequest.class);
        User user = getUserDetails(String.valueOf(checkout.getUserid()), input);

        List<Checkout> checkoutList;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Checkout> query = session.createQuery("from Checkout c where c.userid=:userid and c.code=:code");
            query.setParameter("userid", Long.valueOf(checkout.getUserid()));
            query.setParameter("code", checkout.getCode());
            System.out.println("+++++++++++++++>>>" + query.getQueryString());
            checkoutList = query.list();

            System.out.println("USER ::::::::::: "+ user);
        } catch (Exception e) {
            System.out.println("ERROR::"+ e.getMessage());
            return response
                    .withStatusCode(500)
                    .withBody(String.format("{\"success\": false, \"message\" : \"%s\"}", e.getMessage()));
        }

        List<Product> products = checkoutList.stream().map(data -> new Product.Builder()
        .id(data.getProductId())
        .name(data.getProductName())
        .price(data.getPrice())
        .stock(data.getQuantity())
        .offer(data.getOffer())
        .build()).collect(Collectors.toList());

        double netPrice = products.stream().mapToDouble(product -> product.getPrice()).sum();
        double discountPrice = products.stream().mapToDouble(product -> product.getPrice() * (product.getOffer() / 100)).sum();

        return response
                .withStatusCode(200)
                .withBody(GSON.toJson(new CheckoutOrder.Builder()
                        .userid(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .dateOfBirth(user.getDateOfBirth())
                        .orderItems(products)
                        .netPrice(netPrice)
                        .discount(discountPrice)
                        .totalPrice(netPrice - discountPrice)
                        .build()));
    }


    private User getUserDetails(String userid, final APIGatewayProxyRequestEvent input) {
        Map<String, String> pathParam = new HashMap<>();
        pathParam.put("id", userid);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withResource("/api/product/view/{id}")
                .withPath(String.format("/api/product/view/%s", userid))
                .withPathParameters(pathParam)
                .withHeaders(input.getHeaders())
                .withMultiValueHeaders(input.getMultiValueHeaders())
                .withRequestContext(input.getRequestContext())
                .withIsBase64Encoded(false);

        System.out.println("ORDER REQUEST>>>>>>>>>>>>>>>>>>" + request);

        User user = null;

        try {
            InvokeResult invokeResult = AWSLambdaClientBuilder.standard()
                    .withRegion(Regions.AP_SOUTH_1)
                    .withCredentials(new EnvironmentVariableCredentialsProvider()).build().invoke(new InvokeRequest()
                            .withFunctionName(GET_USER_DETAILS_SERVICE)
                            .withPayload(GSON.toJson(request)));

            String ans = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);
            System.out.println("::::::::::::::::::::" + ans);
            APIGatewayProxyResponseEvent response = GSON.fromJson(ans, APIGatewayProxyResponseEvent.class);

            System.out.println("+_+_+_+_+_+_+_+_+_>>>>>>>>>>"+ response.getBody());

            user = GSON.fromJson(response.getBody(), User.class);

            System.out.println("::::::::::::::::::::" + ans);
            System.out.println(":::::::::::::" + invokeResult.getStatusCode());

        } catch (ServiceException e) {
            System.out.println("+++++++++++++++++" + e);
        } catch (Exception e) {
            System.out.println("~~~~~~~~~~~~~~~~>>>" + e);
        }

        return user;
    }

}