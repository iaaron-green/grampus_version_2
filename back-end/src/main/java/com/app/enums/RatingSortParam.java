package com.app.enums;

public enum RatingSortParam {

    FOLLOWERS;

    public static RatingSortParam getParam(String sortParam) {
        return RatingSortParam.valueOf(sortParam.toUpperCase());
    }

}

