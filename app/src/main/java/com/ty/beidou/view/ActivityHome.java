package com.ty.beidou.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.ty.beidou.R;
import com.ty.beidou.common.API;
import com.ty.beidou.common.BaseActivity;
import com.ty.beidou.common.MApplication;
import com.ty.beidou.model.UserBean;
import com.ty.beidou.preference.ActivityPreferenceHome;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.ty.beidou.R.id.fl_main;

/**
 * Created by ty on 2016/9/21.
 */
public class ActivityHome extends BaseActivity {


    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView mNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (savedInstanceState == null) {
            loadRootFragment(fl_main, MainFragment.newInstance());
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setDrawerMenu(toolbar);
    }

    /**
     * 设置抽屉菜单
     *
     * @param toolbar
     */
    private void setDrawerMenu(final Toolbar toolbar) {
        setSupportActionBar(toolbar);
        final DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar
                , R.string.open
                , R.string.close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.account:
                        startActivity(new Intent(me, ActivityPreferenceHome.class));
                        break;
                    case R.id.workform:
                        startActivity(new Intent(me, ActivityPutWork.class));
                        break;
                    case R.id.workplan:
                        startActivity(new Intent(me, ActivityPutPlan.class));
                        break;
                    case R.id.personapprove:
                        startActivity(new Intent(me, ActivityPutPlan.class));
                        break;

                    case R.id.workchart:
                        startActivity(new Intent(me, ActivityWorkChart.class));
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }


    private void initUserData() {
        View v = mNavigationView.getHeaderView(0);

        CircleImageView iv_header = (CircleImageView) v.findViewById(R.id.iv_head);//头像

        TextView tv_name = (TextView) v.findViewById(R.id.tv_name); //名字

        TextView tv_identity = (TextView) v.findViewById(R.id.tv_identity);//身份

        TextView tv_company = (TextView) v.findViewById(R.id.tv_company);//工作单位

        iv_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(me, ActivityUserInfo.class));
            }
        });
        UserBean user = MApplication.getInstance().getUser();
        Picasso.with(me)
                .load(API.BASE + user.getHead_thumb())
                .fit()
                .into(iv_header);
        tv_name.setText(user.getRealname());
        tv_identity.setText(user.getGtitle());
        tv_company.setText(user.getCompany_name());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_navigation_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initUserData();
//        String a = PreferenceManager.getDefaultSharedPreferences(me)
//                .getString(getResources()
//                        .getString(R.string.key_user_name), "");
//        Toast.makeText(me, a, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    //纪录用户首次点击返回键的时间
    private long firstTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - firstTime > 2000) {
                Toast.makeText(me, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
