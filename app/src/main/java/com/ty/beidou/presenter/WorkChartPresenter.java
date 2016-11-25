package com.ty.beidou.presenter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.libs.view.utils.EmptyUtils;
import com.orhanobut.logger.Logger;
import com.ty.beidou.R;
import com.ty.beidou.common.API;
import com.ty.beidou.common.BasePresenter;
import com.ty.beidou.common.HttpSimpleRST;
import com.ty.beidou.model.ChartBean;
import com.ty.beidou.model.ResponseBean;
import com.ty.beidou.view.IWorkChartView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by ty on 2016/10/9.
 */

public class WorkChartPresenter extends BasePresenter<IWorkChartView> {


    /**
     * 获取人员统计列表
     *
     * @param json
     */
    public void getPersonFromServer(String json) {

        Logger.d(json);
        HttpSimpleRST.putJsonAutoAddToken(json, API.URL_CHART_PERSON, new Callback() {
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
                        ResponseBean<ChartBean> m = JSON
                                .parseObject(r, new TypeReference<ResponseBean<ChartBean>>() {
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


}
