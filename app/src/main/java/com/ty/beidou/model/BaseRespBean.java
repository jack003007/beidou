package com.ty.beidou.model;

/**
 * Created by ty on 2016/9/20.
 */
public class BaseRespBean {

    private int status;

    private String msg;

    private String token;

    public BaseRespBean() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
