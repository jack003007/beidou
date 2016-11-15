package com.ty.beidou.common;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by ty on 2016/11/14.
 */

public class HttpTool {


    public static void putJsonToServer(String key, String json, String url, Callback callback) {

        OkHttpClient mOkHttpClient = new OkHttpClient();
        String token = MApplication.getInstance().getToken();
        if (TextUtils.isEmpty(token)) {
            //跳转到登陆界面

//            Logger.e(new NullPointerException("token不能为空"), "HttpTool:putJsonToServer");
            return;
        }
        if (TextUtils.isEmpty(url)) {
            Logger.e(new NullPointerException("url不能为空"), "HttpTool:putJsonToServer");
        }
        RequestBody body = new FormBody.Builder()
                .add("token", token)
                .add(key, json)
                .build();
        final Request mRequest = new Request
                .Builder()
                .url(url)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(mRequest);
        call.enqueue(callback);
    }


}
