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
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.Map;

public class AddProductApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Gson GSON = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        System.out.println("------------>>>"+ input.toString());
        Product product = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            product = GSON.fromJson(input.getBody(), Product.class);
            Long id = (Long)session.save(GSON.fromJson(input.getBody(), Product.class));
            System.out.println("PRODUCT_ID::"+id);
            product.setId(id);
            System.out.println("PRODUCT_SAVED::"+product);
            session.getTransaction().commit();
        } catch (Exception e) {
            System.out.println("ERROR::"+ e.getMessage());
            e.printStackTrace();
            return response
                    .withStatusCode(500)
                    .withBody(String.format("{\"success\": false, \"message\" : \"%s\"}", e.getMessage()));
        }

        return response
                .withStatusCode(200)
                .withBody(GSON.toJson(product));
    }
}