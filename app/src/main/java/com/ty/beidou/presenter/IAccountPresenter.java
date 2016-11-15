package com.ty.beidou.presenter;

import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.orhanobut.logger.Logger;
import com.ty.beidou.common.BasePresenter;
import com.ty.beidou.common.MApplication;
import com.ty.beidou.common.Urls;
import com.ty.beidou.model.BaseRespBean;
import com.ty.beidou.model.ResponseBean;
import com.ty.beidou.model.UserBean;
import com.ty.beidou.view.IAccountView;

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
public class IAccountPresenter extends BasePresenter<IAccountView> {

    private Handler mHandler;

    public IAccountPresenter() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * 登陆
     *
     * @param phone 账号
     * @param passwd 密码
     */
    public void doLogin(String phone, String passwd) {
        //创建对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建request
        RequestBody body = new FormBody.Builder()
                .add("phone", phone)
                .add("passwd", passwd).build();

        final Request request = new Request
                .Builder().url(Urls.URL_LOGIN).post(body).build();
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                final String msg = e.getMessage();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.error(msg);
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
                        if (m.getStatus() == 1 && m.getData() != null) {//成功
                            UserBean user = m.getData();
                            MApplication.getInstance().setUser(user);
                            MApplication.getInstance().setToken(m.getToken());
                            Logger.d("token:"+m.getToken());
                            mView.loginSuccess("登陆成功");
                        } else {
                            mView.error(m.getMsg());
                        }
                    }
                });
            }
        });
    }

    /**
     * 自动登陆
     *
     * @param token token值
     */
    public void doAutoLogin(String token) {
        //创建对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建request
        RequestBody body = new FormBody.Builder()
                .add("token", token).build();

        final Request request = new Request
                .Builder().url(Urls.URL_AUTOLOGIN).post(body).build();
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                final String msg = e.getMessage();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.error(msg);
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
                        if (m.getStatus() == 1) {
//                                && m.getData() != null) {//成功
//                            UserBean user = m.getData();
//                            MApplication.setUser(user);
                            mView.loginSuccess("登陆成功");
                        } else {
                            mView.error(m.getMsg());
                        }
                    }
                });
            }
        });
    }

    /**
     * 注册
     *
     * @param u 账号
     * @param p 密码
     */
    public void doRegis(String u, String p) {
        //创建对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建request
        RequestBody body = new FormBody.Builder()
                .add("phone", u)
                .add("passwd", p)
                .build();

        final Request request = new Request
                .Builder().url(Urls.URL_REGIS).post(body).build();
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                final String msg = e.getMessage();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mView.error(msg);
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
                        if (m.getStatus() == 1) {
                            mView.regisSuccess(m.getMsg());
                        } else {
                            mView.error(m.getMsg());
                        }
                    }
                });
            }
        });
    }

}
