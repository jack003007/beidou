package com.ty.beidou.presenter;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.libs.view.utils.EmptyUtils;
import com.orhanobut.logger.Logger;
import com.ty.beidou.R;
import com.ty.beidou.common.API;
import com.ty.beidou.common.BasePresenter;
import com.ty.beidou.common.HttpFilesRST;
import com.ty.beidou.common.HttpSimpleRST;
import com.ty.beidou.common.MApplication;
import com.ty.beidou.model.HashBean;
import com.ty.beidou.model.ResponseBean;
import com.ty.beidou.model.UserBean;
import com.ty.beidou.view.IUserInfoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by ty on 2016/10/9.
 */

public class UserInfoPresenter extends BasePresenter<IUserInfoView> {


    /**
     * 提交设置给服务器
     */
    public void putSetting(String json, String photoPath) {

        List<String> pathList = new ArrayList<>();
        if (!TextUtils.isEmpty(photoPath)) {
            pathList.add(photoPath);
        }

        HttpFilesRST.putFiles(pathList, json, API.URL_MODIFY, new Callback() {
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
                        if (EmptyUtils.isNotEmpty(m) || m.getStatus() == 1) {
                            MApplication.getInstance().setUser(m.getData());
                            mView.putSuccess(m.getMsg());
                        } else {//数据插入失败
                            mView.putError(m.getMsg());
                        }
                    }
                });
            }
        });
    }

    /**
     * 获取公司名称列表设置给服务器
     */
    public void getCompanyFromServer() {
        HttpSimpleRST.getDataAutoAddToken(API.URL_COMPANY, new Callback() {
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
                        ResponseBean<HashBean> m = JSON
                                .parseObject(r, new TypeReference<ResponseBean<HashBean>>() {
                                });
                        if (m.getStatus() == 1) {
                            HashMap<String, String> hash = new HashMap<>();
                            for (int i = 0; i < m.getDatas().size(); i++) {
                                hash.put(m.getDatas().get(i).getValue(), m.getDatas().get(i).getId());
                            }
                            mView.getCompanySuccess(hash);
                        } else {//数据插入失败
                            mView.getError(m.getMsg());
                        }
                    }
                });
            }
        });
    }

    /**
     * 获取身份列表设置给服务器
     */

    public void getIdentityFromServer() {
        HttpSimpleRST.getDataAutoAddToken(API.URL_GROUP, new Callback() {
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
                        ResponseBean<HashBean> m = JSON
                                .parseObject(r, new TypeReference<ResponseBean<HashBean>>() {
                                });
                        if (m.getStatus() == 1) {
                            HashMap<String, String> hash = new HashMap<>();
                            for (int i = 0; i < m.getDatas().size(); i++) {
                                hash.put(m.getDatas().get(i).getValue(), m.getDatas().get(i).getId());
                            }
                            mView.getGroupSuccess(hash);
                        } else {//数据插入失败
                            mView.getError(m.getMsg());
                        }
                    }
                });
            }
        });
    }
}
