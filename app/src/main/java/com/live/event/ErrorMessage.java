package com.live.event;

public class ErrorMessage {


    public ErrorMessage(String api, int code) {
        this.api = api;
        this.code = code;
    }
    private int code;

    public int getCode() {
        return code;
    }

    private String api;

    public String getApi() {
        return api;
    }
}
