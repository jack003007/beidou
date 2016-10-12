package com.ty.beidou.model;

import java.util.List;

/**
 * Created by ty on 2016/9/20.
 */
public class ResponseBean<T> {

    private int status;

    private String msg;

    private String token;

    private List<T> data;

    public ResponseBean() {
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

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
