package com.ty.beidou.view;

import com.ty.beidou.common.BaseView;

import java.util.HashMap;

/**
 * Created by ty on 2016/9/12.
 */
public interface IUserInfoView extends BaseView {
    /**
     * 提交成功
     */
    void putSuccess(String msg);

    /**
     * 提交失败
     */
    void putError(String msg);


    /**
     * 获取公司列表
     * @param hash
     */
    void getCompanySuccess(HashMap<String, String> hash);

    /**
     * 获取组列表
     * @param hash
     */
    void getGroupSuccess(HashMap<String, String> hash);

    /**
     * 获取失败
     * @param msg
     */
    void getError(String msg);
}
