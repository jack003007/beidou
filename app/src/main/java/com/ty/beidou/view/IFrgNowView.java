package com.ty.beidou.view;

import com.ty.beidou.model.LocationBean;

import java.util.List;

/**
 * Created by ty on 2016/9/12.
 */
public interface IFrgNowView {


    /**
     *
     * @param result
     * @param t
     */
    public void requestResult(boolean result, List<LocationBean> t);
}
