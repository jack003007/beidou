package com.ty.beidou.common;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ty on 2016/10/12.
 */


public abstract class BaseMvpFragment<V, T extends BasePresenter<V>> extends Fragment {

    public T presenter;

    protected AppCompatActivity me;

    /**
     * 完成presenter的初始化
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        me = (AppCompatActivity) getActivity();
        presenter = initPresenter();
    }

    /**
     * 绑定View
     */
    @Override
    public void onResume() {
        super.onResume();
        presenter.attach((V)
                this);
    }

    /**
     * 解除View
     */
    @Override
    public void onDestroy() {
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
