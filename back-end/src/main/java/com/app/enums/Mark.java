package com.app.enums;

public enum  Mark implements CharSequence {
    UNTIDY, DEADLINER, INTROVERT, BEST_LOOKER, SUPER_WORKER, EXTROVERT;

    public static Mark getMark(String markString) {
        return Mark.valueOf(markString.toUpperCase());
    }

    @Override
    public int length() {
        return 0;
    }

    @Override
    public char charAt(int index) {
        return 0;
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return null;
    }
}