package com.ty.beidou.test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;
import com.ty.beidou.R;
import com.ty.beidou.common.BaseActivity;
import com.ty.beidou.common.Flags;
import com.ty.beidou.common.MyTitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ActivityImageViewer extends BaseActivity implements Toolbar.OnMenuItemClickListener {

    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.rl_bottom)
    RelativeLayout rlBottom;
    private MyPageAdapter adapter;
    private int currentPosition = 0;


    private MyTitleBar mTitleBar;
    private ArrayList<String> imagePaths;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        ButterKnife.bind(this);
        initViews();

    }

    /**
     * @param paths
     */
    private void transmitDataBack(ArrayList<String> paths) {
        Intent i = new Intent();
        Bundle bundle = new Bundle();
        Logger.d("返回时传递的图片数量：" + paths.size());
        bundle.putStringArrayList(Flags.Intent.IMAGE_PATHS, paths);
        i.putExtras(bundle);
        me.setResult(RESULT_OK, i);
    }

    private void initViews() {
        Bundle bundle = getIntent().getExtras();
        int pos = bundle.getInt(Flags.Intent.POSITION);
        imagePaths = getIntent().getStringArrayListExtra(Flags.Intent.IMAGE_PATHS);

        Logger.d("pos: " + pos + "---------" + "传过去的图片数量：" + imagePaths.size());

        mTitleBar = new MyTitleBar(me);
        mTitleBar.setLeftIconAsBack();
        mTitleBar.setLeftOnclickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transmitDataBack(imagePaths);
                finish();
            }
        });

        mTitleBar.setRightSingleIcon(R.drawable.icon_delete, "删除", this);

        mViewPager.setOnPageChangeListener(mPageChangeListener);

        currentPosition = pos;
        adapter = new MyPageAdapter(me, imagePaths);// 构造adapter
        mViewPager.setAdapter(adapter);// 设置适配器
        mViewPager.setCurrentItem(pos);
    }


    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            currentPosition = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 点击删除
     *
     * @param item
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (imagePaths.size() == 1) {
            imagePaths.clear();
            Flags.Photo.CURRENT_NUM = 0;
            transmitDataBack(imagePaths);
            finish();
        } else {
            imagePaths.remove(currentPosition);
            Flags.Photo.CURRENT_NUM--;
            adapter.notifyDataSetChanged();
        }
        Logger.d("imagePaths.size: " + imagePaths.size());
        return false;
    }

}


class MyPageAdapter extends PagerAdapter {
    private Context mContext;
    private ArrayList<String> paths;// 数据源
    private SparseArray<View> cacheViews;

    public MyPageAdapter(Context context, ArrayList<String> paths) {// 构造函数
        // 初始化viewpager的时候给的一个页面
        this.paths = paths;
        this.mContext = context;
        cacheViews = new SparseArray<>(paths.size());
    }


    public int getCount() {// 返回数量
        return paths.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {// 销毁view对象
        container.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View view = cacheViews.get(position);
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.item_photoviewer, container, false);
            view.setTag(position);
            final ImageView iv = (ImageView) view.findViewById(R.id.iv);
            Picasso.with(mContext).load("file://" + paths.get(position)).config(Bitmap.Config.RGB_565).into(iv);
            cacheViews.put(position, view);
        }
        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
