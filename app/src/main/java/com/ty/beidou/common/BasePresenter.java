package com.ty.beidou.common;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by ty on 2016/10/9.
 */

public abstract class BasePresenter<T> {
    /*用于处理网络请求结果*/
    protected Handler mHandler;


    public BasePresenter() {
        mHandler = new Handler(Looper.getMainLooper());
    }
    /*用于处理网络请求结果*/


    public T mView;

    public void attach(T mView) {
        this.mView = mView;
    }

    public void dettach() {
        mView = null;
    }
}
