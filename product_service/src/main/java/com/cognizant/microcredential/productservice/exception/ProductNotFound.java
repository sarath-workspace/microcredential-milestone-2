package com.cognizant.microcredential.productservice.exception;

public class ProductNotFound extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Long productId;

    public ProductNotFound(Long id) {
        super("product does not exist with id -> " + id);
        this.productId = id;
    }

    @Override
    public String getMessage() {
        return String.format("Product with ID : %s doesn't exist", String.valueOf(productId));
    }
}
