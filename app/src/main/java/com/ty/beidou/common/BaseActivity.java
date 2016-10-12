package com.ty.beidou.common;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by ty on 2016/9/19.
 */
public class BaseActivity extends AppCompatActivity {

    protected Activity me;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        me = this;
    }

}
