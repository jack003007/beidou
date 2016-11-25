package com.ty.beidou.common;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.ty.beidou.model.UserBean;

import java.io.File;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by ty on 2016/11/14.
 */

public class HttpFilesRST {

    /**
     * 提交文件给服务器，并返回数据
     *
     * @param url
     * @param callback
     */
    public static void putFiles(List<String> pathList, String json, String url, Callback callback) {

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        for (String path : pathList) {
            File f;
            f = new File(path);
            builder.addFormDataPart(
                    path,
                    f.getName(),
                    RequestBody.create(MediaType.parse("image/png"), f));
        }
        putFilesAutoAddToken(builder.addFormDataPart("json", json), url, callback);
    }


    /**
     * 带token的网络请求
     *
     * @param builder
     * @param url
     * @param callback
     */
    public static void putFilesAutoAddToken(MultipartBody.Builder builder, String url, Callback callback) {

        UserBean user = MApplication.getInstance().getUser();
        if (user == null || TextUtils.isEmpty(user.getToken())) {
            //TODO
//            Logger.e(new NullPointerException("token不能为空"), "HttpTool:putJsonAutoAddToken");
            return;
        }
        putFiles(builder.addFormDataPart("token", user.getToken()), url, callback);
    }

    /**
     * 不带token的网络请求
     *
     * @param builder
     * @param url
     * @param callback
     */
    public static void putFiles(MultipartBody.Builder builder, String url, Callback callback) {

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
