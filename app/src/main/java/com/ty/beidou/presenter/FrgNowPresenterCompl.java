package com.ty.beidou.presenter;

import android.app.Activity;
import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.orhanobut.logger.Logger;
import com.ty.beidou.common.Urls;
import com.ty.beidou.model.LocationBean;
import com.ty.beidou.model.ResponseBean;
import com.ty.beidou.view.IFrgNowView;

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
public class FrgNowPresenterCompl implements IFrgNowPresenter {

    private Context context;

    private IFrgNowView fragmentView;

    public FrgNowPresenterCompl(Context context, IFrgNowView fragmentView) {
        this.fragmentView = fragmentView;
        this.context = context;
    }


    @Override
    public void doRequest(String token) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("token", token)
                .build();
        final Request request = new Request.Builder()
                .url(Urls.URL_NEARBY).post(body).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                final String msg = e.getMessage();
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fragmentView.requestResult(false, null);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String r = response.body().string();
                Logger.json(r);
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ResponseBean<LocationBean> jsonReturn = JSON.parseObject(r, new TypeReference<ResponseBean<LocationBean>>() {
                        });
                        if (jsonReturn.getStatus() == 1)
                        {
                            List<LocationBean> m = jsonReturn.getData();
                            fragmentView.requestResult(true,m);

                        }
                    }
                });
            }
        });
    }
}
