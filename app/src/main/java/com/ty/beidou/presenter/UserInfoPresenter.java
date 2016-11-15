package com.ty.beidou.presenter;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.libs.view.utils.EmptyUtils;
import com.orhanobut.logger.Logger;
import com.ty.beidou.common.BasePresenter;
import com.ty.beidou.common.MApplication;
import com.ty.beidou.common.Urls;
import com.ty.beidou.model.HashBean;
import com.ty.beidou.model.ResponseBean;
import com.ty.beidou.model.UserBean;
import com.ty.beidou.view.IUserInfoView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by ty on 2016/10/9.
 */

public class UserInfoPresenter extends BasePresenter<IUserInfoView> {

    private Handler mHandler;


    public UserInfoPresenter() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 提交设置给服务器
     */
    public void putSettingToServer(String token, String settingJson, String photoPath) {

        OkHttpClient mOkHttpClient = new OkHttpClient();
        MultipartBody.Builder mMultipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        mMultipartBody.addFormDataPart("token", token);
        mMultipartBody.addFormDataPart("settingJson", settingJson);
        if (!TextUtils.isEmpty(photoPath)) {
            File f = new File(photoPath);
            mMultipartBody.addFormDataPart(photoPath
                    , f.getName()
                    , RequestBody.create(MediaType.parse("image/png")
                            , f));
        }

        RequestBody mRequestBody = mMultipartBody.build();

        final Request mRequest = new Request
                .Builder().url(Urls.URL_MODIFY).post(mRequestBody).build();
        Call call = mOkHttpClient.newCall(mRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.netError("服务器连接异常");
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
    public void getCompanyFromServer(String token) {
        OkHttpClient mOkHttpClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("token", token)
                .build();
        final Request mRequest = new Request
                .Builder().url(Urls.URL_COMPANY).post(body).build();
        Call call = mOkHttpClient.newCall(mRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.netError("服务器连接异常");
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
    public void getIdentityFromServer(String token) {
        OkHttpClient mOkHttpClient = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("token", token)
                .build();
        final Request mRequest = new Request
                .Builder().url(Urls.URL_IDENTITY).post(body).build();
        Call call = mOkHttpClient.newCall(mRequest);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.netError("服务器连接异常");
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
                            mView.getIdentitySuccess(hash);
                        } else {//数据插入失败
                            mView.getError(m.getMsg());
                        }
                    }
                });
            }
        });
    }
}
