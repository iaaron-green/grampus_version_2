package com.app.util;

public class Errors {

    public static final int CRITICAL = 4;
    public static final int HIGH = 3;
    public static final int MIDDLE = 2;
    public static final int LOW = 1;

    public static final int COMMON = 5;
    public static final int USER = 6;
    public static final int PROFILE = 7;

    public static final int FTP_CONNECTION_ERROR = getError(CRITICAL, COMMON, 0); // 40500
    public static final int AUTHORIZATION_ERROR = getError(HIGH, COMMON, 0); // 30500
    public static final int USER_NOT_EXIST = getError(MIDDLE, USER, 0); // 20600
    public static final int PROFILE_NOT_EXIST = getError(MIDDLE,PROFILE, 0); // 20700
    public static final int PROFILE_PICTURE_IS_BAD = getError(LOW,PROFILE, 1); // 10701


    public static int getError(int priority, int code, int order){

        return priority*10000+code*100+order;
    }
}
