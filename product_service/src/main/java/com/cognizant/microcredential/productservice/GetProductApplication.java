package com.cognizant.microcredential.productservice;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cognizant.microcredential.productservice.model.Product;
import com.cognizant.microcredential.productservice.util.HibernateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.Session;

import java.util.*;

public class GetProductApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        String[] ids = input.getQueryStringParameters().get("id").split(",");

        System.out.println("------------>>>"+ input.toString());

        List<Product> products = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            for (String productid : ids) {
                Product product = (Product) session.get(Product.class, Long.valueOf(productid));
                if(Objects.isNull(product)) {
                    System.out.println("Product was Not found with id :: " + productid);
                } else {
                    products.add(product);
                    System.out.println("PRODUCT ::::::::::: " + product);
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR::"+ e.getMessage());
            return response
                    .withStatusCode(500)
                    .withBody(String.format("{\"success\": false, \"message\" : \"%s\"}", e.getMessage()));
        }

        if(products.isEmpty()) {
            System.out.println("Product was Not found with id :: "+ ids);
            return response
                    .withStatusCode(500)
                    .withBody(String.format("{\"success\": false, \"message\" : \"product was Not found with id :: %s\"}", ids));
        }

        return response
                .withStatusCode(200)
                .withBody(GSON.toJson(products));
    }

}