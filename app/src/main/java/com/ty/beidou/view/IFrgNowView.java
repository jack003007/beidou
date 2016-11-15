package com.ty.beidou.view;

import com.ty.beidou.model.LocationBean;

import java.util.List;

/**
 * Created by ty on 2016/9/12.
 */
public interface IFrgNowView {

    /**
     * @param t
     */
    void success(List<LocationBean> t);

    /**
     * @param msg
     */
    void error(String msg);

    /**
     * 位置提交成功
     *
     * @param msg
     */
    void postSuccess(String msg);
}
