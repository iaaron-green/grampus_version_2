package com.app.enums;

public enum ChatType {

    PRIVATE, PUBLIC, BROADCAST;

    String name;

    ChatType(String name) {
        this.name = name;
    }

    ChatType() {

    }

}
