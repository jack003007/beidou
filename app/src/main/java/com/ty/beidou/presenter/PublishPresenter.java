package com.ty.beidou.presenter;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import com.ty.beidou.common.BasePresenter;
import com.ty.beidou.common.Urls;
import com.ty.beidou.model.BaseRespBean;
import com.ty.beidou.utils.EmptyUtils;
import com.ty.beidou.view.IPublishView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by ty on 2016/10/9.
 */

public class PublishPresenter extends BasePresenter<IPublishView> {

    private Handler mHandler;


    public PublishPresenter() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    public void onResume(String title, String content, List<String> photos) {
        OkHttpClient mOkHttpClient = new OkHttpClient();

        MultipartBody.Builder mBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        mBody.addFormDataPart("title", title);
        mBody.addFormDataPart("content", content);
        //生成file,并添加到参数列表
        for (String t : photos
                ) {
            Logger.d("图片地址：" + t);
            File f = new File(t);
            Logger.d("生成的File对象：" + f.toString());
            mBody.addFormDataPart(t, f.getName(), RequestBody.create(MediaType.parse("image/png"), f));
        }
        RequestBody mRequestBody = mBody.build();
        Request mRequest = new Request.Builder()
                .url(Urls.URL_MSG_SUBMIT)
                .post(mRequestBody)
                .build();
        mOkHttpClient.newCall(mRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.requestResult(false, "上传失败");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String r = response.body().string();
                Logger.json(r);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BaseRespBean m = JSON.parseObject(r, BaseRespBean.class);
                        if (EmptyUtils.isNotEmpty(m) || m.getStatus() == 200) {
                            mView.hideLoading();
                            mView.requestResult(true, m.getMsg());
                        } else {//数据插入失败
                            mView.requestResult(false, m.getMsg());
                        }
                    }
                });
            }
        });
    }
}
