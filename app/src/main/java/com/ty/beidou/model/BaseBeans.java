package com.ty.beidou.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ty on 2016/9/20.
 */
public class BaseBeans<T> implements Serializable {

    private int status;

    private String msg;

    private List<T> data;

    public BaseBeans() {
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

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
