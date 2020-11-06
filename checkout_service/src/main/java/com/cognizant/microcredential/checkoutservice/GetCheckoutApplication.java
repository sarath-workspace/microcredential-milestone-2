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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GetCheckoutApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson GSON = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();

    private static final String GET_ORDER_DETAILS_SERVICE = System.getenv("GET_ORDER_DETAILS_SERVICE");

    private static final String GET_USER_DETAILS_SERVICE = System.getenv("GET_USER_DETAILS_SERVICE");

    private static final String GET_PRODUCT_DETAILS_SERVICE = System.getenv("GET_PRODUCT_DETAILS_SERVICE");

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {


        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        System.out.println("------------>>>" + input.toString());

        Long userid = Long.valueOf(input.getPathParameters().get("id"));

        User user = getUserDetails(String.valueOf(userid), input);
        String code = UUID.randomUUID().toString().substring(0, 8);

        List<Order> orders = getOrderDetails(userid, input);

        List<String> productIds = orders.stream().map(Order::getProductId).map(String::valueOf).collect(Collectors.toList());

        Map<Long, Product> productMap = getProductDetails(productIds, input).stream().collect(Collectors.toMap(Product::getId, val -> val));

        List<Product> products = orders.stream().map(data -> {
            Product product = productMap.get(data.getProductId());
            product.setPrice(product.getPrice() * data.getQuantity());
            return product;
        }).collect(Collectors.toList());

        double netPrice = products.stream().mapToDouble(product -> product.getPrice()).sum();
        double discountPrice = products.stream().mapToDouble(product -> product.getPrice() * (product.getOffer() / 100)).sum();

        return response
                .withStatusCode(200)
                .withBody(GSON.toJson(new CheckoutOrder.Builder()
                        .userid(userid)
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

    private List<Product> getProductDetails(List<String> productids, final APIGatewayProxyRequestEvent input) {

        String productIDS = String.join(",", productids);

        Map<String, String> pathParam = new HashMap<>();
        pathParam.put("id", productIDS);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withResource("/api/product/view/{id}")
                .withPath(String.format("/api/product/view/%s", productIDS))
                .withQueryStringParameters(pathParam)
                .withHeaders(input.getHeaders())
                .withMultiValueHeaders(input.getMultiValueHeaders())
                .withRequestContext(input.getRequestContext())
                .withIsBase64Encoded(false);

        System.out.println("ORDER REQUEST>>>>>>>>>>>>>>>>>>" + request);

        List<Product> products = null;

        try {
            InvokeResult invokeResult = AWSLambdaClientBuilder.standard()
                    .withRegion(Regions.AP_SOUTH_1)
                    .withCredentials(new EnvironmentVariableCredentialsProvider()).build().invoke(new InvokeRequest()
                            .withFunctionName(GET_PRODUCT_DETAILS_SERVICE)
                            .withPayload(GSON.toJson(request)));

            String ans = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);

            System.out.println("::::::::::::::::::::" + ans);
            APIGatewayProxyResponseEvent response = GSON.fromJson(ans, APIGatewayProxyResponseEvent.class);

            System.out.println("+_+_+_+_+_+_+_+_+_>>>>>>>>>>"+ response.getBody());


            products = GSON.fromJson(
                    response.getBody(),
                    new TypeToken<List<Product>>() {
                    }.getType()
            );


            System.out.println("::::::::::::STATUS_CODE:" + invokeResult.getStatusCode());
            System.out.println("::::::::::::RESPONSE_CODE:" + response.getStatusCode());

        } catch (ServiceException e) {
            System.out.println("+++++++++++++++++" + e);
        } catch (Exception e) {
            System.out.println("~~~~~~~~~~~~~~~~>>>" + e);
        }

        return products;
    }

    private List<Order> getOrderDetails(long userid, final APIGatewayProxyRequestEvent input) {
        Map<String, String> pathParam = new HashMap<>();
        pathParam.put("userid", String.valueOf(userid));

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withResource("/api/order/view/{id}")
                .withPath(String.format("/api/order/view/%s", userid))
                .withPathParameters(pathParam)
                .withHeaders(input.getHeaders())
                .withMultiValueHeaders(input.getMultiValueHeaders())
                .withRequestContext(input.getRequestContext())
                .withIsBase64Encoded(false);

        System.out.println("ORDER REQUEST>>>>>>>>>>>>>>>>>>" + request);

        List<Order> orders = null;

        try {
            InvokeResult invokeResult = AWSLambdaClientBuilder.standard()
                    .withRegion(Regions.AP_SOUTH_1)
                    .withCredentials(new EnvironmentVariableCredentialsProvider()).build().invoke(new InvokeRequest()
                            .withFunctionName(GET_ORDER_DETAILS_SERVICE)
                            .withPayload(GSON.toJson(request)));

            String ans = new String(invokeResult.getPayload().array(), StandardCharsets.UTF_8);
            System.out.println("::::::::::::::::::::" + ans);
            APIGatewayProxyResponseEvent response = GSON.fromJson(ans, APIGatewayProxyResponseEvent.class);

            System.out.println("+_+_+_+_+_+_+_+_+_>>>>>>>>>>"+ response.getBody());
            orders = GSON.fromJson(
                    response.getBody(),
                    new TypeToken<List<Order>>() {
                    }.getType()
            );

            System.out.println("::::::::::::STATUS_CODE:" + invokeResult.getStatusCode());
            System.out.println("::::::::::::RESPONSE_CODE:" + response.getStatusCode());

        } catch (ServiceException e) {
            System.out.println("+++++++++++++++++" + e);
        } catch (Exception e) {
            System.out.println("~~~~~~~~~~~~~~~~>>>" + e);
        }

        return orders;
    }
}