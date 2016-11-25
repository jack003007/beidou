package com.ty.beidou.view;

import com.ty.beidou.common.BaseView;

/**
 * Created by ty on 2016/9/12.
 */
public interface IAccountView extends BaseView {
    /**
     * 网络连接失败
     */
    void error(String msg);

    void loginSuccess(String msg);

    void regisSuccess(String msg);


}
