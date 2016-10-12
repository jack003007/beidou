package com.ty.beidou.common;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jph.takephoto.app.TakePhotoActivity;

/**
 * 使用TakePhoto库必须继承它的TakePhotoActivity
 * 为了实现MVP
 * 所以此处间接继承
 * Created by ty on 2016/10/9.
 */

public abstract class BaseMvpActivityForTakePhoto<V, T extends BasePresenter<V>> extends TakePhotoActivity {

    public T presenter;

    /**
     * 完成presenter的初始化
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initPresenter();
    }

    /**
     * 绑定View
     */
    @Override
    protected void onResume() {
        super.onResume();
        presenter.attach((V) this);
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
