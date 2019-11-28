package com.app.util;

public class CustomException extends Exception {

    private String message;
    private Integer errorCode;

    public CustomException(String message, Integer errorCode) {
        this.message = message;
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message + " " + errorCode;
    }
}
