package com.live.bean;

import java.io.Serializable;

public class ErrorMsg implements Serializable {
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private String msg;
    private int code;
}
