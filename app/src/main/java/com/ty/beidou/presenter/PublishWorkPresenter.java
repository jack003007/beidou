package com.ty.beidou.presenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.libs.view.utils.EmptyUtils;
import com.orhanobut.logger.Logger;
import com.ty.beidou.R;
import com.ty.beidou.common.BasePresenter;
import com.ty.beidou.common.HttpTool;
import com.ty.beidou.common.Urls;
import com.ty.beidou.model.BaseRespBean;
import com.ty.beidou.model.FormStyleBean;
import com.ty.beidou.model.ResponseBean;
import com.ty.beidou.view.IPublishWorkView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by ty on 2016/10/9.
 */

public class PublishWorkPresenter extends BasePresenter<IPublishWorkView> {


    public void getLayout() {

        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody mRequestBody = new FormBody.Builder()
                .add("token", "xx")
                .build();
        Request mRequest = new Request.Builder()
                .url(Urls.URL_WORK_FORM)
                .post(mRequestBody)
                .build();
        mOkHttpClient.newCall(mRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.netError(R.string.network_connect_error);
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
                        ResponseBean<FormStyleBean> m =
                                JSON.parseObject(r, new TypeReference<ResponseBean<FormStyleBean>>() {
                                });
                        if (EmptyUtils.isNotEmpty(m) || m.getStatus() == 1) {
                            mView.requestSuccess(m.getDatas());
                        } else {//失败
                            mView.netMsg(m.getMsg());
                        }
                    }
                });
            }
        });
    }

    /**
     * @param json
     */
    public void putWorkToServer(String json) {

        Logger.d(json);
        String key = "json";
        HttpTool.putJsonToServer(key,json, Urls.URL_WORK_POST, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.netError(R.string.network_connect_error);
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
                        BaseRespBean m =
                                JSON.parseObject(r, BaseRespBean.class);
                        if (EmptyUtils.isNotEmpty(m) && m.getStatus() == 1) {
                            mView.netMsg(m.getMsg());
                        } else {//失败
                            mView.netMsg(m.getMsg());
                        }
                    }
                });
            }
        });
    }
}
