package com.ty.beidou.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ty.beidou.R;
import com.ty.beidou.adapter.AdapterMsgs;
import com.ty.beidou.common.BaseMvpFragment;
import com.ty.beidou.model.MsgBean;
import com.ty.beidou.presenter.FrgMsgPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ty on 2016/9/21.
 */
public class FrgMsg extends BaseMvpFragment<IFrgMsgView, FrgMsgPresenter> implements IFrgMsgView {

    private static FrgMsg f = null;

    @BindView(R.id.rv_msg)
    RecyclerView rvMsg;

    List<MsgBean> msgList = new ArrayList<>();

    AdapterMsgs adapter = null;
    //单例
    public static FrgMsg newInstance() {
        if (f == null) {
            synchronized (FrgMsg.class) {
                if (f == null) {
                    f = new FrgMsg();
                }
            }
        }
        return f;
    }

    @Override
    public FrgMsgPresenter initPresenter() {
        return new FrgMsgPresenter();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, null);
        ButterKnife.bind(this, view);
        rvMsg.setLayoutManager(new LinearLayoutManager(me));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume("xxxx");
    }


    @Override
    public void success(List<MsgBean> list) {
        msgList.clear();
        msgList.addAll(list);
        adapter = new AdapterMsgs(me, msgList);
        rvMsg.setAdapter(adapter);
    }

    @Override
    public void error(String msg) {
        Toast.makeText(me, msg, Toast.LENGTH_SHORT).show();
    }
}
