package com.cognizant.microocredential.userservice.get.exception;

public class UserNotFound extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Long userId;

    public UserNotFound(Long id) {
        super("User does not exist with id -> " + id);
        this.userId = id;
    }

    @Override
    public String getMessage() {
        return String.format("User with ID : %s doesn't exist", String.valueOf(userId));
    }

}
