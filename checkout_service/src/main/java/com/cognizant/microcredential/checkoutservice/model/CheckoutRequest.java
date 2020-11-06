package com.cognizant.microcredential.checkoutservice.model;

import java.io.Serializable;
import java.util.List;

public class CheckoutRequest implements Serializable {

    private String userid;

    private String code;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "CheckoutRequest{" +
                "userid=" + userid +
                ", products=" + code +
                '}';
    }
}
