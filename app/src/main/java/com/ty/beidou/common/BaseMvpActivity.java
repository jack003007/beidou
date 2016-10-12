package com.ty.beidou.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ty on 2016/10/9.
 */

public abstract class BaseMvpActivity<V, T extends BasePresenter<V>> extends AppCompatActivity {

    public T presenter;

    protected AppCompatActivity me;

    /**
     * 完成presenter的初始化
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        me = this;
        presenter = initPresenter();
    }

    /**
     * 绑定View
     */
    @Override
    protected void onResume() {
        super.onResume();
        presenter.attach((V)
                this);
    }

    /**
     * 解除View
     */
    @Override
    protected void onDestroy() {
        presenter.dettach();
        super.onDestroy();
    }

    /**
     * 初始化Presenter的方法
     *
     * @return
     */
    public abstract T initPresenter();
}