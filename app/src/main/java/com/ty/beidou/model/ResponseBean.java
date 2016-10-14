package com.ty.beidou.model;

import java.util.List;

/**
 * Created by ty on 2016/9/20.
 */
public class ResponseBean<T> extends BaseRespBean {


    private List<T> data;

    public ResponseBean() {
    }


    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
