package com.cognizant.microcredential.checkoutservice.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "checkout")
public class Checkout implements Serializable {
    @Id
    private long userid;

    @Id
    private String code;

    @Id
    private long productId;

    private String productName;

    private int quantity;

    private double price;

    private double offer;

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getOffer() {
        return offer;
    }

    public void setOffer(double offer) {
        this.offer = offer;
    }

    public static class Builder {

        private Checkout checkout = new Checkout();

        public Builder userid ( long userid) {
            this.checkout.userid = userid;
            return this;
        }

        public Builder code ( String code) {
            this.checkout.code = code;
            return this;
        }

        public Builder productId ( long product) {
            this.checkout.productId = product;
            return this;
        }

        public Builder productName ( String productName) {
            this.checkout.productName = productName;
            return this;
        }

        public Builder quantity ( int quantity) {
            this.checkout.quantity = quantity;
            return this;
        }

        public Builder price ( double price) {
            this.checkout.price = price;
            return this;
        }

        public Builder offer ( double offer) {
            this.checkout.offer = offer;
            return this;
        }

        public Checkout build() {
            return checkout;
        }
    }

    @Override
    public String toString() {
        return "Checkout{" +
                "userid=" + userid +
                ", code='" + code + '\'' +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", offer=" + offer +
                '}';
    }
}
