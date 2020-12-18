package com.macydevelopment.springboot.util;

public class ApiInvalidRequestException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public ApiInvalidRequestException(String message) {
        super(message);
    }
}