package com.app.enums;

public enum  Mark {
    NONE, UNTIDY, DEADLINER, INTROVERT, BEST_LOOKER, SUPER_WORKER, EXTROVERT;

    public static Mark getMark(String markString) {
        return Mark.valueOf(markString.toUpperCase());
    }
}