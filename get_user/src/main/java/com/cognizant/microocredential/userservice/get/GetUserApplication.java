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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class GetUserApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final Gson GSON = new GsonBuilder().setDateFormat("YYYY-MM-dd").setPrettyPrinting().create();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        Long id = Long.valueOf(input.getPathParameters().get("id"));

        System.out.println("------------>>>"+ input.toString());

        User user;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            user = session.get(User.class, id);
            System.out.println("USER ::::::::::: "+ user);
        } catch (Exception e) {
            System.out.println("ERROR::"+ e.getMessage());
            return response
                    .withStatusCode(500)
                    .withBody(String.format("{\"success\": false, \"message\" : \"%s\"}", e.getMessage()));
        }

        if(Objects.isNull(user)) {
            System.out.println("User was Not found with id :: "+ id);
            return response
                    .withStatusCode(500)
                    .withBody(String.format("{\"success\": false, \"message\" : \"User was Not found with id :: %s\"}", id));
        }

        return response
                .withStatusCode(200)
                .withBody(GSON.toJson(user));
    }

}
