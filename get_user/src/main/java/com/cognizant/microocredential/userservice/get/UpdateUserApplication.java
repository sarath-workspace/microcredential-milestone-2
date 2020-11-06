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
import java.util.Objects;

public class UpdateUserApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Gson GSON = new GsonBuilder().setDateFormat("YYYY-MM-dd").create();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        Long id = Long.valueOf(input.getPathParameters().get("id"));

        System.out.println("------------>>>"+ input.toString());

        User userToUpdate = null;

        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            User user = GSON.fromJson(input.getBody(), User.class);
            userToUpdate = (User)session.get(User.class, id);
            System.out.println("UPDATE_INFO::"+user);
            System.out.println("USER_TO_UPDATE::"+userToUpdate);

            if(Objects.isNull(userToUpdate)) {
                return response
                        .withStatusCode(500)
                        .withBody(String.format("{\"success\": false, \"message\" : \"User was Not found with id :: %s\"}", id));
            }

            userToUpdate.setFirstName(user.getFirstName());
            userToUpdate.setLastName(user.getLastName());
            userToUpdate.setDateOfBirth(user.getDateOfBirth());
            userToUpdate.setEmail(user.getEmail());

            System.out.println("UPDATED_USER::"+userToUpdate);

            session.update(userToUpdate);
            session.getTransaction().commit();
        }

        return response
                .withStatusCode(200)
                .withBody(GSON.toJson(userToUpdate));
    }

}
