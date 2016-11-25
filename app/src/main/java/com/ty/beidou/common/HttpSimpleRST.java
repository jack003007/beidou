package com.ty.beidou.common;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.ty.beidou.model.UserBean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by ty on 2016/11/14.
 */

public class HttpSimpleRST {

    /**
     * 提交json给服务器，并返回数据
     *
     * @param json
     * @param url
     * @param callback
     */
    public static void putJson(String json, String url, Callback callback) {
        putJson(new FormBody.Builder().add("json", json), url, callback);
    }

    /**
     * 提交token给服务器，并返回数据
     *
     * @param url
     * @param callback
     */
    public static void getDataAutoAddToken(String url, Callback callback) {
        putJsonAutoAddToken(new FormBody.Builder(), url, callback);
    }

    /**
     * 提交json和token给服务器，并返回数据，并返回数据
     *
     * @param json
     * @param url
     * @param callback
     */
    public static void putJsonAutoAddToken(String json, String url, Callback callback) {
        putJsonAutoAddToken(new FormBody.Builder().add("json", json), url, callback);
    }

    /**
     * 带token的网络请求
     *
     * @param builder
     * @param url
     * @param callback
     */
    public static void putJsonAutoAddToken(FormBody.Builder builder, String url, Callback callback) {

        UserBean user = MApplication.getInstance().getUser();
        if (user == null || TextUtils.isEmpty(user.getToken())) {
            //TODO
//            Logger.e(new NullPointerException("token不能为空"), "HttpTool:putJsonAutoAddToken");
            return;
        }
        putJson(builder.add("token", user.getToken()), url, callback);
    }

    /**
     * 不带token的网络请求
     *
     * @param builder
     * @param url
     * @param callback
     */
    public static void putJson(FormBody.Builder builder, String url, Callback callback) {

        OkHttpClient mOkHttpClient = new OkHttpClient();

        if (TextUtils.isEmpty(url)) {
            Logger.e(new NullPointerException("url不能为空"), "HttpTool:putJsonAutoAddToken");
        }
        RequestBody body = builder.build();
        final Request mRequest = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = mOkHttpClient.newCall(mRequest);
        call.enqueue(callback);
    }

}
