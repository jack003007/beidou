package com.ty.beidou.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dinuscxj.refresh.RecyclerRefreshLayout;
import com.orhanobut.logger.Logger;
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
    //上拉下拉View
    @BindView(R.id.pull_refresh)
    RecyclerRefreshLayout mPullRefreshView;
    //刷新控件的工具类
    PullRefreshTool mPullRefreshTool = null;

    private long sinceTime;//大于该时间
    private long maxTime;//小于该时间
    private final int count = 10;//每页数量
    private int page = 1;//页码


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
        configureView();
        configureRecyclerView();
        configurePullRefresh();
        return view;
    }

    private void configureView() {
        adapter = new AdapterMsgs(me, msgList);
        rvMsg.setAdapter(adapter);
    }

    /**
     * 配置RecycclerView
     */
    private void configureRecyclerView() {
        rvMsg.setLayoutManager(new LinearLayoutManager(me));
    }

    /**
     * 配置上拉下拉刷新控件
     */
    private void configurePullRefresh() {
        if (mPullRefreshView == null) {
            return;
        }
        sinceTime = maxTime = System.currentTimeMillis();

        mPullRefreshView.setNestedScrollingEnabled(true);
        mPullRefreshTool = new PullRefreshTool(mPullRefreshView);
        mPullRefreshView.setOnRefreshListener(new RecyclerRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPullRefreshTool.requestRefresh();
                Logger.d("开始刷新...");
            }
        });

//        设置将刷新动画从手势释放的位置移动到刷新位置的动画所需要的Interpolator
//        mPullRefreshView.setAnimateToRefreshInterpolator(Interpolator);

//        设置将刷新动画从手势释放的位置（或刷新位置）移动到起始位置的动画所需要的Interpolator
//        mPullRefreshView.setAnimateToStartInterpolator(Interpolator);

//        设置将刷新动画从手势释放的位置移动到刷新位置的动画所需要的时间
//        mPullRefreshView.setAnimateToRefreshDuration(int);

//        设置将刷新动画从手势释放的位置（或刷新位置）移动到起始位置的动画所需要的时间
//        mPullRefreshView.setAnimateToStartDuration(int);

//        设置RefreshView相对父组件的初始位置
////        pullRefreshsetRefreshInitialOffset(float);

//        设置触发刷新需要滑动的最小距离
//        mPullRefreshView.setRefreshTargetOffset(float);

//        设置RefreshView的样式
//        mPullRefreshView.setRefreshStyle(@NonNull RefreshStyle);

    }


    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume("", System.currentTimeMillis() + "", page + "", count + "", "xxxx");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 网络请求成功
     *
     * @param list
     */
    @Override
    public void success(List<MsgBean> list) {
        mPullRefreshTool.requestComplete();
        //请求成功，更新sinceTime
        sinceTime = System.currentTimeMillis();
        if (list.size() == 0) {
            Toast.makeText(me, "没有新的数据", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Toast.makeText(me, "更新了" + list.size() + "条数据", Toast.LENGTH_SHORT).show();
        }
        msgList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    /**
     * 网络请求失败
     *
     * @param msg
     */
    @Override
    public void error(String msg) {
        mPullRefreshTool.requestFailure();
        Toast.makeText(me, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 刷新、加载控件的工具类
     */
    public class PullRefreshTool implements pullRefreshListener {
        RecyclerRefreshLayout refreshView;

        public PullRefreshTool(RecyclerRefreshLayout refreshView) {
            this.refreshView = refreshView;
        }

        /**
         * 下拉刷新
         * 如果请求成功将sinceTime置为当前值
         * 如果有数据则提示并刷新
         * 如果没有数据只提示
         */
        @Override
        public void requestRefresh() {
            refreshView.setRefreshing(true);
            presenter.onResume(sinceTime + "", "", "1", page + "", "xxx");
            Logger.d("请求刷新的参数：" + sinceTime + "--------1-----" + page + "xxx");
        }

        /**
         * 上拉加载更多
         * 如果有数据
         * 如果没有数据
         */
        @Override
        public void requestMore() {

        }

        /**
         * 刷新失败
         */
        @Override
        public void requestSuccess() {
            requestComplete();

        }

        /**
         * 刷新成功
         */
        @Override
        public void requestFailure() {
            requestComplete();
            refreshView.setRefreshing(false);
            Logger.d("刷新失败。。。");
        }

        @Override
        public void requestComplete() {
            if (refreshView != null) {
                refreshView.setRefreshing(false);
                Logger.d("刷新结束。。。");
            }
        }

        @Override
        public boolean hasMore() {
            return false;
        }
    }

    /**
     * 下拉刷新 工具类接口
     */
    public interface pullRefreshListener {
        void requestRefresh();

        void requestMore();

        void requestSuccess();

        void requestComplete();

        void requestFailure();

        boolean hasMore();
    }

}
