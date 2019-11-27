package com.app.enums;

public enum  Mark implements CharSequence {
    BEST_LOOKER, DEADLINER, SMART_MIND, SUPER_WORKER, MOTIVATOR, TOP1, MENTOR;

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