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
import java.util.Objects;

public class DeleteProductApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Gson GSON = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        System.out.println("------------>>>"+ input.toString());
        Long id = Long.valueOf(input.getQueryStringParameters().get("id"));
        Product productToDelete = null;

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            productToDelete = (Product)session.get(Product.class, id);
            System.out.println("PRODUCT_TO_DELETE::"+productToDelete);
            if(Objects.isNull(productToDelete)) {
                return response
                        .withStatusCode(500)
                        .withBody(String.format("{\"success\": false, \"message\" : \"product was Not found with id :: %s\"}", id));
            }
            session.delete(productToDelete);
            session.getTransaction().commit();
        }

        return response
                .withStatusCode(200)
                .withBody(GSON.toJson(productToDelete));
    }

}
