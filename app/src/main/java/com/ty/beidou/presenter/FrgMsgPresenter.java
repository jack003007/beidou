package com.ty.beidou.presenter;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.orhanobut.logger.Logger;
import com.ty.beidou.common.BasePresenter;
import com.ty.beidou.common.API;
import com.ty.beidou.model.MsgBean;
import com.ty.beidou.model.ResponseBean;
import com.ty.beidou.view.IFrgMsgView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by ty on 2016/9/12.
 */
public class FrgMsgPresenter extends BasePresenter<IFrgMsgView> {

    private Handler mHandler;

    public FrgMsgPresenter() {
        mHandler = new Handler(Looper.getMainLooper());
    }


    public void onResume(String sinceTime,String maxTime,String page,String count,String token) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("since",sinceTime)
                .add("max",maxTime)
                .add("page",page)
                .add("count",count)
                .add("token", token)
                .build();
        final Request request = new Request.Builder()
                .url(API.URL_MSG_ALL).post(body).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.error("服务器连接异常");
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
                        ResponseBean<MsgBean> result = JSON.parseObject(r, new TypeReference<ResponseBean<MsgBean>>() {
                        });
                        if (result.getStatus() == 200) {
                            mView.success(result.getDatas());
                        }
                    }
                });
            }
        });
    }
}
