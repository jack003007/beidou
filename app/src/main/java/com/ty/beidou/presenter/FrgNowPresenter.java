package com.ty.beidou.presenter;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.orhanobut.logger.Logger;
import com.ty.beidou.common.BasePresenter;
import com.ty.beidou.common.API;
import com.ty.beidou.common.HttpSimpleRST;
import com.ty.beidou.model.BaseRespBean;
import com.ty.beidou.model.LocationBean;
import com.ty.beidou.model.ResponseBean;
import com.ty.beidou.view.IFrgNowView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by ty on 2016/9/12.
 */
public class FrgNowPresenter extends BasePresenter<IFrgNowView> {

    private Handler mHandler;

    public FrgNowPresenter() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 从服务器获取当前作业人员
     *
     */
    public void getLocationsFromServer() {
        HttpSimpleRST.getDataAutoAddToken(API.URL_NEARBY, new Callback() {
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
                        ResponseBean<LocationBean> result = JSON.parseObject(r, new TypeReference<ResponseBean<LocationBean>>() {
                        });
                        if (result.getStatus() == 1) {
                            mView.success(result.getDatas());
                        }
                    }
                });
            }
        });

    }

    /**
     * 向服务器提交自己的位置信息
     *
     * @param token
     */
    public void putLocationToServer(String locationJson) {
        HttpSimpleRST.putJsonAutoAddToken( locationJson, API.URL_PUT_LOCATION, new Callback() {
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
                        BaseRespBean result = JSON.parseObject(r, BaseRespBean.class);
                        if (result.getStatus() == 1) {
                            Logger.d("坐标提交成功");
                            mView.postSuccess(result.getMsg());
                        } else {
                            Logger.d("坐标提交失败");
                            mView.error(result.getMsg());
                        }
                    }
                });
            }
        });

    }
}
