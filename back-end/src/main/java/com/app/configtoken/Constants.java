package com.app.configtoken;

import java.util.*;

public class Constants {
    public static final String SIGN_UP_URLS = "/api/users/**";
    public static final String H2_URL = "h2-console/**";
    public static final String SECRET ="SecretKeyToGenJWTs";
    public static final String TOKEN_PREFIX= "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final long EXPIRATION_TIME = 300_000_000; //30 000 seconds
    public static final String FTP_SERVER = "10.11.1.155";
    public static final int FTP_PORT = 21;
    public static final String FTP_IMG_LINK = "ftp://10.11.1.155/";

    public static List<Locale> SUPPORTED_LOCALES =  Arrays.asList(new Locale("en"), new Locale("ru"));
}
