package com.app.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PhotoUrl implements Serializable {

    private String url;


    public PhotoUrl() {
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "PhotoUrl{" +
                "url='" + url + '\'' +
                '}';
    }
}
