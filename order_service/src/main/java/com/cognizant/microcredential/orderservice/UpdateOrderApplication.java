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

public class UpdateOrderApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Gson GSON = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        Long userid = Long.valueOf(input.getPathParameters().get("userid"));
        Long productid = Long.valueOf(input.getPathParameters().get("productid"));

        System.out.println("------------>>>"+ input.toString());

        Order order = new Order();
        order.setUserid(userid);
        order.setProductId(productid);

        Order OrderToUpdate = null;

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Order product = GSON.fromJson(input.getBody(), Order.class);
            OrderToUpdate = (Order)session.get(Order.class, order);
            System.out.println("UPDATE_INFO::"+product);
            OrderToUpdate.setQuantity(product.getQuantity());
            System.out.println("ORDER_TO_UPDATE::"+OrderToUpdate);

            if(Objects.isNull(OrderToUpdate)) {
                System.out.printf("order was Not found with userid :: %s || product :: %s%n", userid, productid);
                return response
                        .withStatusCode(500)
                        .withBody(String.format("{\"success\": false, \"message\" : \"order was Not found with userid :: %s || product :: %s\"}", userid, productid));
            }

            System.out.println("UPDATED_ORDER::"+OrderToUpdate);

            session.update(OrderToUpdate);
            session.getTransaction().commit();
        }

        return response
                .withStatusCode(200)
                .withBody(GSON.toJson(OrderToUpdate));
    }

}
