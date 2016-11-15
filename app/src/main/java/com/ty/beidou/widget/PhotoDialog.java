package com.ty.beidou.widget;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.ty.beidou.R;


/**
 * Created by ty on 2016/10/21.
 */

public class PhotoDialog extends DialogFragment {


    private static PhotoDialog instance;

    ImageView iv;
    TextView tvCamera;
    TextView tvPhoto;
    private View view;

    private View.OnClickListener cameraClickListener;
    private View.OnClickListener photoClickListener;

    public void setCameraClickListener(View.OnClickListener cameraClickListener) {
        this.cameraClickListener = cameraClickListener;
    }

    public void setPhotoClickListener(View.OnClickListener photoClickListener) {
        this.photoClickListener = photoClickListener;
    }

    public static PhotoDialog newInstance(Bundle bundle) {
        if (instance == null) {
            instance = new PhotoDialog();
        }
        instance.setArguments(bundle);
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //设置无标题样式
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置动画
        getDialog().getWindow().getAttributes().windowAnimations = com.libs.view.R.style.CustomDialog;

        getDialog().setCanceledOnTouchOutside(true);


        view = inflater.inflate(R.layout.dialog_photo, container);
        if (savedInstanceState != null) {

        }
        iv = (ImageView) view.findViewById(R.id.iv);
        tvCamera = (TextView) view.findViewById(R.id.tv_camera);
        tvPhoto = (TextView) view.findViewById(R.id.tv_photo);
        String url = instance.getArguments().getString("url");
        if (url != null) {
            Picasso.with(getActivity())
                    .load(url)
                    .fit()
                    .into(iv);
        }
        if (cameraClickListener != null) {
            tvCamera.setOnClickListener(cameraClickListener);
        }
        if (photoClickListener != null) {
            tvPhoto.setOnClickListener(photoClickListener);
        }
        return view;
    }


}
