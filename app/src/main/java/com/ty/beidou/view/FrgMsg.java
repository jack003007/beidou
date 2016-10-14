package com.ty.beidou.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
public class FrgMsg extends BaseMvpFragment<IFrgMsgsView,FrgMsgPresenter> implements IFrgMsgsView {

    private static FrgMsg f = null;

    @BindView(R.id.recycleview_msgs)
    RecyclerView mRecycleviewMsgs;

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
        mRecycleviewMsgs.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<MsgBean> list = new ArrayList<>();
        for (int i = 0; i < 4; i++) {

            MsgBean m = new MsgBean();
            m.setTitle("题目" + i);
            m.setContent("内容" + i);
            m.setTime("时间" + i);
            list.add(m);
        }
        mRecycleviewMsgs.setAdapter(new AdapterMsgs(getActivity(), list));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }



    @Override
    public void requestResult(boolean result, String data) {

    }
}
