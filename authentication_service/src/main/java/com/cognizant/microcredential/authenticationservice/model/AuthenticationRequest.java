package com.cognizant.microcredential.authenticationservice.model;

import java.io.Serializable;

public class AuthenticationRequest implements Serializable {

    private String username;

    private String password;

    private String newpassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    @Override
    public String toString() {
        return "AuthenticationRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", newpassword='" + newpassword + '\'' +
                '}';
    }
}
