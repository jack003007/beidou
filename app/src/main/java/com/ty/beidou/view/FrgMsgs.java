package com.ty.beidou.view;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ty.beidou.R;
import com.ty.beidou.adapter.AdapterMsgs;
import com.ty.beidou.model.MsgBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ty on 2016/9/21.
 */
public class FrgMsgs extends Fragment {

    private static FrgMsgs f = null;

    @BindView(R.id.recycleview_msgs)
    RecyclerView mRecycleviewMsgs;
    @BindView(R.id.tv_publish)
    TextView tvPublish;

    //单例
    public static FrgMsgs newInstance() {
        if (f == null) {
            synchronized (FrgMsgs.class) {
                if (f == null) {
                    f = new FrgMsgs();
                }
            }
        }
        return f;
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
        tvPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),ActivityPublish.class));
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
