package com.cognizant.microcredential.checkoutservice.model;

import java.io.Serializable;

public class Product implements Serializable {

    private long id;

    private String name;

    private int stock;

    private double price;

    private double offer;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
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
        private Product product = new Product();

        public Builder id(long id) {
            this.product.id = id;
            return this;
        }

        public Builder name(String name) {
            this.product.name = name;
            return this;
        }

        public Builder stock(int stock) {
            this.product.stock = stock;
            return this;
        }

        public Builder price(double price) {
            this.product.price = price;
            return this;
        }

        public Builder offer(double offer) {
            this.product.offer = offer;
            return this;
        }

        public Product build() {
            return product;
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", stock=" + stock +
                ", price=" + price +
                ", offer=" + offer +
                '}';
    }
}
