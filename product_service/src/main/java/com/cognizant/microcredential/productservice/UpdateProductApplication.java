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

public class UpdateProductApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Gson GSON = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        Long id = Long.valueOf(input.getPathParameters().get("id"));

        System.out.println("------------>>>"+ input.toString());

        Product productToUpdate = null;

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Product product = GSON.fromJson(input.getBody(), Product.class);
            productToUpdate = (Product)session.get(Product.class, id);
            System.out.println("UPDATE_INFO::"+product);
            System.out.println("PRODUCT_TO_UPDATE::"+productToUpdate);

            if(Objects.isNull(productToUpdate)) {
                return response
                        .withStatusCode(500)
                        .withBody(String.format("{\"success\": false, \"message\" : \"product was Not found with id :: %s\"}", id));
            }

            productToUpdate.setName(product.getName());
            productToUpdate.setPrice(product.getPrice());
            productToUpdate.setOffer(product.getOffer());
            productToUpdate.setStock(product.getStock());

            System.out.println("UPDATED_PRODUCT::"+productToUpdate);

            session.update(productToUpdate);
            session.getTransaction().commit();
        }

        return response
                .withStatusCode(200)
                .withBody(GSON.toJson(productToUpdate));
    }

}
