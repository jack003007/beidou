package com.ty.beidou.presenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.libs.view.utils.EmptyUtils;
import com.orhanobut.logger.Logger;
import com.ty.beidou.R;
import com.ty.beidou.common.API;
import com.ty.beidou.common.BasePresenter;
import com.ty.beidou.common.HttpSimpleRST;
import com.ty.beidou.model.BaseRespBean;
import com.ty.beidou.model.ResponseBean;
import com.ty.beidou.model.UserBean;
import com.ty.beidou.view.IPutPlanView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by ty on 2016/10/9.
 */

public class PutPlanPresenter extends BasePresenter<IPutPlanView> {


    /**
     * 获取组长列表
     * @param json
     */
    public void getLeaderFromServer(String json) {

        Logger.d(json);
        HttpSimpleRST.putJsonAutoAddToken( json, API.URL_PLAN_LEADER, new Callback() {
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
                        ResponseBean<UserBean> m = JSON
                                .parseObject(r, new TypeReference<ResponseBean<UserBean>>() {
                                });
                        if (EmptyUtils.isNotEmpty(m) && m.getStatus() == 1) {
                            mView.netSuccess(m.getDatas());
                        } else {//失败
                            mView.netMsg(m.getMsg());
                        }
                    }
                });
            }
        });
    }
    /**
     * 获取组长列表
     * @param json
     */
    public void putJsonToServer(String json) {
        Logger.d(json);
        HttpSimpleRST.putJsonAutoAddToken(json, API.URL_PLAN_POST, new Callback() {
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
                        BaseRespBean m = JSON
                                .parseObject(r, BaseRespBean.class);
                        if (EmptyUtils.isNotEmpty(m) && m.getStatus() == 1) {
                            mView.netSuccess(m.getMsg());
                        } else {//失败
                            mView.netMsg(m.getMsg());
                        }
                    }
                });
            }
        });
    }
}
