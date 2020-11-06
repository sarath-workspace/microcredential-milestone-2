package com.cognizant.microcredential.checkoutservice.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CheckoutOrder implements Serializable {

    private long userid;

    private String firstName;

    private String lastName;

    private String email;

    private Date dateOfBirth;

    private List<Product> orderItems;

    private double netPrice;

    private double discount;

    private double totalPrice;

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<Product> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<Product> orderItems) {
        this.orderItems = orderItems;
    }

    public double getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(double netPrice) {
        this.netPrice = netPrice;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public static class Builder {

        private CheckoutOrder checkoutOrder = new CheckoutOrder();

        public Builder userid(long userid) {
            this.checkoutOrder.userid = userid;
            return this;
        }

        public Builder firstName(String firstName) {
            this.checkoutOrder.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.checkoutOrder.lastName = lastName;
            return this;
        }

        public Builder email(String email) {
            this.checkoutOrder.email = email;
            return this;
        }

        public Builder dateOfBirth(Date dateOfBirth) {
            this.checkoutOrder.dateOfBirth = dateOfBirth;
            return this;
        }

        public Builder orderItems (List<Product> orderItems) {
            this.checkoutOrder.orderItems = orderItems;
            return this;
        }

        public Builder netPrice(double netPrice) {
            this.checkoutOrder.netPrice = netPrice;
            return this;
        }

        public Builder discount(double discount) {
            this.checkoutOrder.discount = discount;
            return this;
        }

        public Builder totalPrice(double totalPrice) {
            this.checkoutOrder.totalPrice = totalPrice;
            return this;
        }

        public CheckoutOrder build() {
            return checkoutOrder;
        }
    }

    @Override
    public String toString() {
        return "CheckoutOrder{" +
                "userid=" + userid +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", orderItems=" + orderItems +
                ", netPrice=" + netPrice +
                ", discount=" + discount +
                ", totalPrice=" + totalPrice +
                '}';
    }
}
