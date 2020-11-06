package com.cognizant.microcredential.emailotpservice;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.cognizant.microcredential.emailotpservice.model.MailInfo;
import com.cognizant.microcredential.emailotpservice.model.MailResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

public class EmailOTPServiceApplication implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final Gson GSON = new GsonBuilder().create();

    private final String FROM = System.getenv("MAIL_FROM_ADDRESS");

    private final AmazonSimpleEmailService AMAZON_SES_CLIENT = AmazonSimpleEmailServiceClientBuilder
            .standard()
            .withRegion(Regions.AP_SOUTH_1)
            .build();

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("X-Custom-Header", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent().withHeaders(headers);
        final MailInfo MAIL_INFO = GSON.fromJson(input.getBody(), MailInfo.class);

        for (final String TO : MAIL_INFO.getTo()) {
            AMAZON_SES_CLIENT.sendEmail(new SendEmailRequest()
                    .withSource(FROM)
                    .withDestination(new Destination().withToAddresses(TO))
                    .withMessage(new Message()
                            .withSubject(new Content().withCharset("UTF-8").withData(MAIL_INFO.getSubject()))
                            .withBody(new Body().withText(new Content().withCharset("UTF-8").withData(MAIL_INFO.getMessage())))));
        }

        return response
                .withStatusCode(200)
                .withBody(GSON.toJson(new MailResponse.Builder().success("true").message("mail sent !!!")));
    }

}
