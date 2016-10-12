package com.ty.beidou.view;

/**
 * Created by ty on 2016/9/12.
 */
public interface ILoginView {
    /**
     * 注册请求
     */
    public void onRegisResult();

    /**
     * 登陆请求
     * @param result
     * @param msg
     */
    public void onLoginResult(boolean result,String msg);
}
