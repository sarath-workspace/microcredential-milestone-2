package com.cognizant.microcredential.orderservice.exception;

public class OrderNotFound extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OrderNotFound(Long productid, Long userid) {
        super(String.format("The ordered item with productId : %s and userid : %s", productid, userid));

    }

}
