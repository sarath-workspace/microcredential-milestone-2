package com.cognizant.microcredential.authenticationservice.utils;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;

public class CognitoUtil {

    public static final String REGION = System.getenv("REGION");

    public static AWSCognitoIdentityProvider getAmazonCognitoIdentityClient() {

        return AWSCognitoIdentityProviderClientBuilder.standard()
                .withCredentials(new ClasspathPropertiesFileCredentialsProvider())
                .withRegion(REGION)
                .build();

    }
}
