package com.app.exceptions;

public class Errors {

    public static final int CRITICAL = 4;
    public static final int HIGH = 3;
    public static final int MIDDLE = 2;
    public static final int LOW = 1;

    public static final int COMMON = 5;
    public static final int USER = 6;
    public static final int PROFILE = 7;
    public static final int RATING = 8;
    public static final int CHAT = 9;

    public static final int FTP_CONNECTION_ERROR = getError(CRITICAL, COMMON, 0); // 40500
    public static final int AUTHORIZATION_ERROR = getError(HIGH, COMMON, 0); // 30500
    public static final int USER_NOT_ACTIVATED = getError(MIDDLE, USER, 1); // 20501
    public static final int USER_NOT_EXIST = getError(MIDDLE, USER, 0); // 20600
    public static final int USER_ALREADY_EXIST = getError(MIDDLE, USER, 1); // 20601
    public static final int USER_ALREADY_EXIST_ENTER_ACTIVATION_CODE = getError(MIDDLE, USER, 1); // 20601
    public static  final int ACTIVATION_CODE_IS_ACTIVE = getError(LOW, USER, 1); // 10601
    public static  final int ACTIVATION_CODE_IS_NOT_ACTIVE = getError(LOW, USER, 2); // 10602
    public static final int PROFILE_NOT_EXIST = getError(MIDDLE,PROFILE, 0); // 20700
    public static final int PROFILE_PICTURE_IS_BAD = getError(LOW,PROFILE, 1); // 10701
    public static final int MARKTYPE_NOT_EXIST = getError(MIDDLE,PROFILE, 3); // 20703
    public static final int WRONG_PROFILE_ID = getError(MIDDLE, PROFILE, 2); //20702
    public static final int RATING_TYPE_IS_EMPTY = getError(MIDDLE, RATING, 0); //20800
    public static final int WRONG_INPUT_DATA_CHAT = getError(MIDDLE, CHAT, 0); //20900
    public static final int WRONG_CHAT_TARGET_ID = getError(MIDDLE, CHAT, 1); //20901
    public static final int WRONG_CHAT_TYPE = getError(MIDDLE, CHAT, 2); //20902
    public static final int CHAT_ROOM_NOT_EXIST = getError(MIDDLE, CHAT, 3); //20903
    public static final int WRONG_PAGE_AS_PARAMETER = getError(MIDDLE, COMMON, 3); //20502


    public static int getError(int priority, int code, int order){

        return priority*10000+code*100+order;
    }
}
