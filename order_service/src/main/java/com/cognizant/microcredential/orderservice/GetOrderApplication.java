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
import org.hibernate.query.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GetOrderApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        Long userid = Long.valueOf(input.getPathParameters().get("userid"));

        System.out.println("------------>>>"+ input.toString());

        Order order = new Order();
        order.setUserid(userid);

        List<Order> orderList = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Order> orderListQuery = session.createQuery("from Order where userid = :userid");
            orderListQuery.setParameter("userid", userid);
            orderList = orderListQuery.list();

            System.out.println("ORDER ::::::::::: "+ order);
        } catch (Exception e) {
            System.out.println("ERROR::"+ e.getMessage());
            return response
                    .withStatusCode(500)
                    .withBody(String.format("{\"success\": false, \"message\" : \"%s\"}", e.getMessage()));
        }

        if(Objects.isNull(orderList) || orderList.isEmpty()) {
            System.out.printf("Order was Not found with userid :: %s%n", userid);
            return response
                    .withStatusCode(500)
                    .withBody(String.format("{\"success\": false, \"message\" : \"order was Not found with userid :: %s\"}", userid));
        }

        return response
                .withStatusCode(200)
                .withBody(GSON.toJson(orderList));
    }

}