package com.app.util;

public class Constans {

    public static final int CRITICAL = 4;
    public static final int HIGH = 3;
    public static final int MIDDLE = 2;
    public static final int LOW = 1;

    public static final int COMMON = 5;
    public static final int USER = 6;
    public static final int PROFILE = 7;

    public static final int AUTHORIZATION_ERROR = getError(HIGH, COMMON); // 35000
    public static final int USER_ATION_ERROR = getError(HIGH, COMMON); // 35000
    public static final int PROFILE_NOT_EXIST = getError(MIDDLE,PROFILE); // 27000
    public static final int PROFILE_PICTURE_IS_NULL = getError(LOW,PROFILE); // 17000

    public static int getError(int priority, int code){

        return priority*10000+code*1000;
    }
}
