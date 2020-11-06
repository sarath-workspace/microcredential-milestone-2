package com.cognizant.microcredential.orderservice;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cognizant.microcredential.orderservice.model.Order;
import com.cognizant.microcredential.orderservice.util.HibernateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class DeleteOrderApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Gson GSON = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        System.out.println("------------>>>"+ input.toString());
        Long userid = Long.valueOf(input.getPathParameters().get("userid"));
        Long productid = Long.valueOf(input.getPathParameters().get("productid"));
        Order orderToDelete = null;

        Order order = new Order();
        order.setUserid(userid);
        order.setProductId(productid);

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            orderToDelete = (Order)session.get(Order.class, order);
            System.out.println("PRODUCT_TO_DELETE::"+orderToDelete);
            if(Objects.isNull(orderToDelete)) {
                return response
                        .withStatusCode(500)
                        .withBody(String.format("{\"success\": false, \"message\" : \"product was Not found with userid :: %s || productid :: %s\"}", userid, productid));
            }
            session.delete(orderToDelete);
            session.getTransaction().commit();
        }

        return response
                .withStatusCode(200)
                .withBody(GSON.toJson(orderToDelete));
    }

}
