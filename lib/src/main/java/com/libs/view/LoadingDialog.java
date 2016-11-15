package com.libs.view;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;


/**
 * Created by ty on 2016/10/21.
 */

public class LoadingDialog extends DialogFragment {


    private static LoadingDialog instance;
    private View view;

    public static LoadingDialog newInstance() {
        if (instance == null) {
            instance = new LoadingDialog();
        }
//        Bundle bundle = new Bundle();
//        instance.setArguments(bundle);
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //设置无标题样式
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置动画
        getDialog().getWindow().getAttributes().windowAnimations = R.style.CustomDialog;

        getDialog().setCanceledOnTouchOutside(false);


        view = inflater.inflate(R.layout.dialog, container);
        if (savedInstanceState != null) {

        }
//        CircleZoom mCircleZoom = (CircleZoom) view.findViewById(R.id.circle_zoom);
//        mCircleZoom.startAnim();
        return view;
    }


}
