package com.ty.beidou.view;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ty.beidou.R;
import com.ty.beidou.common.BaseActivity;
import com.ty.beidou.common.MyTitleBar;

/**
 * Created by ty on 2016/9/21.
 */
public class ActivityHome extends BaseActivity implements  RadioGroup.OnCheckedChangeListener {

    private FrameLayout fl_main;
    private RadioGroup rg_bottom;
    private RadioButton rb_now;
    private RadioButton rb_msg;

    FrgNow mFrgNow;
    FrgMsg mFrgMsg;


    Fragment mCurFragment;
    //标题栏
    private MyTitleBar mTitlebar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mTitlebar = new MyTitleBar(me);
        mTitlebar.setLeftIcon(null);
        mTitlebar.setLeftText("地图");
        bindViews();
        if (savedInstanceState == null) {
            mCurFragment = mFrgNow = FrgNow.newInstance();
            getFragmentManager().beginTransaction().replace(R.id.fl_main, mFrgNow).commit();
        }
    }

    private void bindViews() {
        fl_main = (FrameLayout) findViewById(R.id.fl_main);
        rg_bottom = (RadioGroup) findViewById(R.id.rg_bottom);
        rb_now = (RadioButton) findViewById(R.id.rb_now);
        rb_msg = (RadioButton) findViewById(R.id.rb_msg);
        rg_bottom.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {
            case R.id.rb_now://当前
                mTitlebar.setLeftText("地图");
                mTitlebar.clearRightSingleIcon();
                if (mFrgNow == null) {
                    mFrgNow = FrgNow.newInstance();
                }
                switchFragments(mFrgNow);
                break;
            case R.id.rb_msg://消息
                mTitlebar.setLeftText("消息");
                mTitlebar.setRightSingleIcon(R.drawable.icon_pen_edit, "发布", new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent i = new Intent(me, ActivityPublish.class);
                        startActivity(i);
                        return false;
                    }
                });
                if (mFrgMsg == null) {
                    mFrgMsg = FrgMsg.newInstance();
                }
                switchFragments(mFrgMsg);
                break;
        }
    }

    /**
     * 用于切换Fragment
     *
     * @param to
     */
    public void switchFragments(Fragment to) {
        if (mCurFragment != to) {
            FragmentTransaction transaction = getFragmentManager()
                    .beginTransaction();//.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (!to.isAdded()) {    // 先判断是否被add过
                transaction.hide(mCurFragment).add(R.id.fl_main, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(mCurFragment).show(to).commit(); // 隐藏当前的fragment，显示下一个
            }
            mCurFragment = to;
        }
    }

    //纪录用户首次点击返回键的时间
    private long firstTime = 0;

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            if (System.currentTimeMillis() - firstTime > 2000) {
//                Toast.makeText(me, "再按一次退出程序", Toast.LENGTH_SHORT).show();
//                firstTime = System.currentTimeMillis();
//            } else {
//                finish();
//                System.exit(0);
//            }
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
