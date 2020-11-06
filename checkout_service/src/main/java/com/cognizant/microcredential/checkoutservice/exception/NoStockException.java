package com.cognizant.microcredential.checkoutservice.exception;

public class NoStockException extends RuntimeException{


    private static final long serialVersionUID = 1L;

    public NoStockException(Long id, int availableqty, int reqqty) {
        super(String.format("Not Enough Item Found for checkout with productid -> %s requestedqty = %s but availbleqty -> %s", id, reqqty, availableqty));
    }
}
