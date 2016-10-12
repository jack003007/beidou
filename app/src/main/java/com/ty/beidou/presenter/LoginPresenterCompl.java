package com.ty.beidou.presenter;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ty.beidou.common.MApplication;
import com.ty.beidou.common.Urls;
import com.ty.beidou.model.ResponseBean;
import com.ty.beidou.model.UserBean;
import com.ty.beidou.view.ILoginView;

import java.io.IOException;
import java.util.List;

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
public class LoginPresenterCompl implements ILoginPresenter {

    private ILoginView loginView;

    public LoginPresenterCompl(ILoginView loginView) {
        this.loginView = loginView;
    }


    @Override
    public void doLogin(String name, String passwd) {
        //创建对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建request
        RequestBody body = new FormBody.Builder()
                .add("name", name)
                .add("passwd", passwd).build();

        final Request request = new Request
                .Builder().url(Urls.URL_LOGIN).post(body).build();
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String msg = e.getMessage();
                ((Activity) loginView).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loginView.onLoginResult(false, msg);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String r = response.body().string();
//                Logger.json(r);
                ((Activity) loginView).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ResponseBean<UserBean> jsonReturn = JSON
                                .parseObject(r, new TypeReference<ResponseBean<UserBean>>() {
                                });
                        if (jsonReturn.getStatus() == 1) {//成功
                            List<UserBean> m = jsonReturn.getData();
                            if (m != null && m.size()>0) {
                                MApplication.getInstance().setMuser(m.get(0));
//                                String s = (String) SPUtils.get(((Activity)loginView),"userinfo","");
                                loginView.onLoginResult(true, "");
                            }
                        } else {//失败

                        }

                    }
                });
            }
        });
    }


}
