package com.ty.beidou.model;

import java.util.List;

/**
 * Created by ty on 2016/9/20.
 */
public class ResponseBean<T> extends BaseRespBean {


    private List<T> datas;

    public ResponseBean() {
    }

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }
}
