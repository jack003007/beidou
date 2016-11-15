package com.ty.beidou.view;

/**
 * Created by ty on 2016/9/12.
 */
public interface IAccountView {
    /**
     * 网络连接失败
     */
    void error(String msg);
    void loginSuccess(String msg);
    void regisSuccess(String msg);

}
