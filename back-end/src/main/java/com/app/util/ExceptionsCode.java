package com.app.util;

public enum ExceptionsCode {

    User(1.0),
    Profile(2.0),
    ProfilePicture(2.1);

    private final Double id;

    ExceptionsCode(Double id) {
        this.id = id;
    }

    public String getId() {

        return String.valueOf(id*1000);
    }
}
