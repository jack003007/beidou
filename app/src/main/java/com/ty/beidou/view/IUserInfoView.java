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
     * 提交失败
     */
    void netError(String msg);

    /**
     * 获取公司名称列表
     * @param hash
     */
    void getCompanySuccess(HashMap<String, String> hash);

    /**
     * 获取身份列表
     * @param hash
     */
    void getIdentitySuccess(HashMap<String, String> hash);

    /**
     * 获取失败
     * @param msg
     */
    void getError(String msg);
}
