package com.ty.beidou.common;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ty.beidou.R;

/**
 * Created by ty on 2016/9/18.
 */
public class GeneralToolbar {
    private static GeneralToolbar generalToolbar;

    Toolbar mToolbar = null;

    TextView tv_center = null;

    private final AppCompatActivity mActivity;


    public static GeneralToolbar newInstance(Context context) {
        if (generalToolbar == null) {
            generalToolbar = new GeneralToolbar(context);
        }
        return generalToolbar;
    }

    /**
     * 请确保已调用Activity.setContentView方法
     *
     * @param context
     */
    public GeneralToolbar(Context context) {
        mActivity = (AppCompatActivity) context;
        mToolbar = (Toolbar) mActivity.findViewById(R.id.toolbar);
        tv_center = (TextView) mActivity.findViewById(R.id.tv_top_center);
    }


    public void inflateMenu(int MenuId) {
        mToolbar.inflateMenu(MenuId);

    }

    public void setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener listener) {
        mToolbar.setOnMenuItemClickListener(listener);
    }

    /**
     * 左侧图标
     *
     * @param resourceId
     */
    public void setLeftIcon(int resourceId) {
        mToolbar.setNavigationIcon(resourceId);
    }

    /**
     * 左侧图标
     *
     * @param drawable
     */
    public void setLeftIcon(Drawable drawable) {
        mToolbar.setNavigationIcon(drawable);
    }

    /**
     * 将左侧图标设置为返回键
     */
    public void setLeftIconAsBack() {
        setLeftIcon(R.drawable.ic_arrow_back);
    }

    public void setLeftOnclickListener(View.OnClickListener l) {
        if (l != null) mToolbar.setNavigationOnClickListener(l);
    }

    /**
     * 设置左侧文字
     *
     * @param str
     */
    public void setLeftText(String str) {
        mToolbar.setTitle(str);
    }


    /**
     * 设置标题文字
     *
     * @param s
     */
    public void setCenterText(String s) {
        tv_center.setText(s);
    }

    public void showLeftIcon(boolean show) {
        if (show) {
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } else {
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    /**
     * 右侧图标及点击事件
     *
     * @param resId
     * @param onMenuItemClickListener
     */
    public void setRightSingleIcon(int resId, String desc, Toolbar.OnMenuItemClickListener onMenuItemClickListener) {
        if (mToolbar.getMenu().size() <= 0) mToolbar.inflateMenu(R.menu.menu_default_titlebar);
        mToolbar.getMenu().findItem(R.id.item_default).setIcon(resId);
        mToolbar.getMenu().findItem(R.id.item_default).setTitle(desc);
        mToolbar.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    public void clearRightSingleIcon() {
        if (mToolbar.getMenu().size() <= 0) {
            return;
        }
        mToolbar.getMenu().findItem(R.id.item_default).setIcon(0);
        mToolbar.getMenu().findItem(R.id.item_default).setTitle("");
        mToolbar.setOnMenuItemClickListener(null);
    }
}
