package com.cognizant.microocredential.userservice.get;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cognizant.microocredential.userservice.get.model.User;
import com.cognizant.microocredential.userservice.get.util.HibernateUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.HashMap;
import java.util.Map;

public class AddUserApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Gson GSON = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);
        System.out.println("------------>>>"+ input.toString());
        User user = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            user = GSON.fromJson(input.getBody(), User.class);
            Long id = (Long)session.save(GSON.fromJson(input.getBody(), User.class));
            System.out.println("USER_ID::"+id);
            user.setId(id);
            System.out.println("USER_SAVED::"+user);
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
                .withBody(GSON.toJson(user));
    }
}
