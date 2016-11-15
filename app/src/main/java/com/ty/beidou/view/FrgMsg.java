package com.ty.beidou.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.orhanobut.logger.Logger;
import com.ty.beidou.R;
import com.ty.beidou.adapter.AdapterMsgs;
import com.ty.beidou.common.BaseMvpFragment;
import com.ty.beidou.common.GeneralToolbar;
import com.ty.beidou.model.MsgBean;
import com.ty.beidou.presenter.FrgMsgPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ty on 2016/9/21.
 */
public class FrgMsg extends BaseMvpFragment<IFrgMsgView, FrgMsgPresenter> implements IFrgMsgView, BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private static FrgMsg f = null;

    @BindView(R.id.rv_msg)
    RecyclerView rvMsg;

    List<MsgBean> msgList = new ArrayList<>();

    AdapterMsgs adapter = null;

    @BindView(R.id.swipelayout)
    SwipeRefreshLayout mSwipeLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    /**
     * ragments have a different view lifecycle than activities.
     * When binding a fragment in onCreateView, set the views to null in onDestroyView.
     * Butter Knife returns an Unbinder instance when you call bind to do this for you.
     * Call its unbind method in the appropriate lifecycle callback.
     */
    private Unbinder unbinder;

    private long sinceTime;//大于该时间
    private long maxTime;//小于该时间
    private final int count = 10;//每页数量
    private int page = 1;//页码

    private GeneralToolbar generalToolbar;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logger.d("Fragment:onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Logger.d("Fragment:onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, null);
        ButterKnife.bind(this, view);
        configureToolbar();
        configureAdapter();
        configureRecyclerView();
        configurePullRefresh();

        Logger.d("Fragment:onCreateView");
        return view;
    }

    private void configureToolbar() {
        toolbar.setTitle("消息");
        toolbar.inflateMenu(R.menu.menu_default_titlebar);
        MenuItem mMenuItem = toolbar.getMenu().findItem(R.id.item_default);
        mMenuItem.setIcon(R.drawable.icon_pen_edit);
        mMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(_mActivity, ActivityPublish.class));
                return false;
            }
        });
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        sinceTime = maxTime = System.currentTimeMillis();
        //查询最新的count条数据
        presenter.onResume("", maxTime + "", page + "", count + "", "xxxx");

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        unbinder.unbind();
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

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

    private void configureAdapter() {
        adapter = new AdapterMsgs(R.layout.item_msg, msgList, me);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_BOTTOM);
        adapter.openLoadMore(count);
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
        mSwipeLayout.setOnRefreshListener(this);
        adapter.setOnLoadMoreListener(this);
    }


    /**
     * 网络请求成功
     *
     * @param list
     */
    @Override
    public void success(final List<MsgBean> list) {
        //下拉刷新，首先关闭动画;若请求成功，更新sinceTime
        Logger.d("adapter.isLoading(): " + adapter.isLoading());
        if (mSwipeLayout.isRefreshing()) {
            mSwipeLayout.setRefreshing(false);

            sinceTime = System.currentTimeMillis();
            rvMsg.post(new Runnable() {
                @Override
                public void run() {
                    if (list.size() == 0) {
                        return;
                    }
                    adapter.addData(0, list);
                    Toast.makeText(me, "更新了" + list.size() + "条数据", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        } else if (adapter.isLoading()) {

            rvMsg.post(new Runnable() {
                @Override
                public void run() {
                    if (list.size() < count) {
                        adapter.loadComplete();
                    } else if (list.size() == count) {
                        page++;//如果结果满一页
                    }
                    adapter.addData(list);
                    Toast.makeText(me, "加载了" + list.size() + "条数据", Toast.LENGTH_SHORT).show();
                }
            });
            return;
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
        if (mSwipeLayout.isRefreshing()) {
            mSwipeLayout.setRefreshing(false);
        }
        Toast.makeText(me, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        //刷新新增的后台数据
        presenter.onResume(sinceTime + "", "", "1", count + "", "xxx");
    }

    @Override
    public void onLoadMoreRequested() {
        //加载历史数据
        presenter.onResume("", maxTime + "", (page + 1) + "", count + "", "xxx");
        Logger.d("加载第：" + (page + 1) + "页");
    }


}
