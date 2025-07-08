package com.example.moneyway.common.exception.CustomException;

public class CustomGptException extends RuntimeException {
    public CustomGptException(String message, Throwable cause) {
        super(message, cause);
    }
}