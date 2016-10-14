package com.ty.beidou.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ty.beidou.R;
import com.ty.beidou.common.BaseActivity;
import com.ty.beidou.common.MyTitleBar;
import com.ty.beidou.presenter.ILoginPresenter;
import com.ty.beidou.presenter.IRegisPresenter;
import com.ty.beidou.presenter.LoginPresenterCompl;
import com.ty.beidou.presenter.RegisPresenterCompl;

import java.util.Random;

/**
 * Created by ty on 2016/9/12.
 */
public class ActivityLoginRegis extends BaseActivity implements ILoginView, View.OnClickListener {

    private Toolbar mToolbar;

    /**
     * 注册
     */
    private ImageButton iv_regis;
    /**
     * 操作
     */
    private Button btn_operation;
    /**
     * 用户名
     */
    private EditText et_name;
    /**
     * 密码
     */
    private EditText et_passwd;
    /**
     * 确认密码
     */


    private EditText et_repasswd;
    /**
     * 验证码输入
     */
    private EditText et_ver;
    ILoginPresenter loginPresenter;
    IRegisPresenter regisPresenter;

    private String numStrTmp = "";//临时Code
    private String numStr = "";   //完整Code串
    private int verLen = 4;//Code长度
    private int[] colorArray = new int[6];//随机颜色数组

    private TextView verCodeSrc;   //验证码图像来源
    private TextView tv_count_down;//倒计时
    private LinearLayout ll_ver;//验证码的总布局
    private LinearLayout ll_ver_code;//验证码左侧的父布局
    private MyTitleBar mTopbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login_regis);
        me = this;
        bindViews();
        initToolbar();
    }

    /**
     * 绑定View
     */
    private void bindViews() {


        iv_regis = (ImageButton) findViewById(R.id.iv_regis);
        btn_operation = (Button) findViewById(R.id.btn_operation);
        et_name = (EditText) findViewById(R.id.et_name);
        et_passwd = (EditText) findViewById(R.id.et_passwd);
        et_repasswd = (EditText) findViewById(R.id.et_repasswd);

        //验证码
        verCodeSrc = (TextView) findViewById(R.id.tv_bitmap_src);
        tv_count_down = (TextView) findViewById(R.id.tv_count_down);
        ll_ver_code = (LinearLayout) findViewById(R.id.ll_ver_code);
        ll_ver = (LinearLayout) findViewById(R.id.ll_ver);
        et_ver = (EditText) findViewById(R.id.et_ver);

        iv_regis.setOnClickListener(this);
        btn_operation.setOnClickListener(this);
        tv_count_down.setOnClickListener(this);
        loginPresenter = new LoginPresenterCompl(this);
        regisPresenter = new RegisPresenterCompl(this);
    }

    private void initToolbar() {
        setTitle("");
        setSupportActionBar(mToolbar);
//        改变返回键的颜色
////        Drawable iconBack = ContextCompat.getDrawable(ActivityLoginRegis.this, R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        iconBack.setColorFilter(ContextCompat.getColor(ActivityLoginRegis.this, R.color.white), PorterDuff.Mode.SRC_ATOP);
//        getSupportActionBar().setHomeAsUpIndicator(iconBack);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mTopbar = new MyTitleBar(ActivityLoginRegis.this);
        mTopbar.setCenterText("登陆");
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTopbar.showLeftIcon(false);
                toggleUI(true);
            }
        });

    }

    /**
     * 重置验证码
     */
    public void initVerCodeView() {
        ll_ver_code.removeAllViews();

        numStr = "";
        numStrTmp = "";
        for (int i = 0; i < verLen; i++) {
            int numIntTmp = new Random().nextInt(10);
            numStrTmp = String.valueOf(numIntTmp);//随机数字
            numStr = numStr + numStrTmp;
            verCodeSrc.setText("" + numStrTmp);
            verCodeSrc.setTextColor(randomColor());//随机颜色

            Matrix mMatrix = new Matrix();

            mMatrix.reset();//重置矩阵为单位矩阵

            mMatrix.setRotate(randomAngle());//随机旋转角度

            Bitmap bmNumA = Bitmap.createBitmap(getBitmapFromView(
                    verCodeSrc, 20, 50), 0, 0, 20, 50, mMatrix, true);
            ImageView codeView = new ImageView(ActivityLoginRegis.this);
            codeView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
            codeView.setImageBitmap(bmNumA);
            ll_ver_code.addView(codeView);
        }
    }

    /**
     * 随机角度
     *
     * @return
     */
    public int randomAngle() {
        return 15 * (new Random().nextInt(3) - new Random().nextInt(3));
//        return 0;
    }

    /**
     * 随机颜色
     *
     * @return
     */
    public int randomColor() {
        colorArray[0] = 0xFF000000;//BLACK
        colorArray[1] = 0xFFFF00FF;//MAGENTA
        colorArray[2] = 0xFFFF0000;//RED
        colorArray[3] = 0xFF00FF00;//GREEN
        colorArray[4] = 0xFF0000FF;//BLUE
        colorArray[5] = 0xFF00FFFF;//CYAN
        //colorArray[6] = 0xFFFFFF00;//YELLOW 看不清楚
        int tmp = new Random().nextInt(6);
        return colorArray[tmp];

    }

    /**
     * View转Bitmap
     *
     * @param view
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getBitmapFromView(View view, int width,
                                           int height) {
        int widthSpec = View.MeasureSpec.makeMeasureSpec(width,
                View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(height,
                View.MeasureSpec.EXACTLY);
        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    /**
     * 切换主界面 注册|登陆
     */
    private void toggleUI(boolean isLogin) {
        if (isLogin) {
            iv_regis.setVisibility(View.VISIBLE);
            et_repasswd.setVisibility(View.GONE);
            ll_ver.setVisibility(View.GONE);
            et_ver.setVisibility(View.GONE);
            btn_operation.setText(R.string.login);

//            tv_count_down.setVisibility(View.GONE);
            mTopbar.setCenterText(getResources().getString(R.string.login));
        } else {
            mTopbar.showLeftIcon(true);
            iv_regis.setVisibility(View.GONE);
            et_repasswd.setVisibility(View.VISIBLE);
            ll_ver.setVisibility(View.VISIBLE);
//            tv_count_down.setVisibility(View.VISIBLE);
            et_ver.setVisibility(View.VISIBLE);
            btn_operation.setText(R.string.register);
            mTopbar.setCenterText(getResources().getString(R.string.register));
        }
    }

    /**
     * 注册
     */
    @Override
    public void onRegisResult() {
        Toast.makeText(this, "注册", Toast.LENGTH_SHORT).show();
        mTopbar.setCenterText(getString(R.string.register));
    }

    /**
     * 登陆请求
     *
     * @param result
     * @param msg
     */
    @Override
    public void onLoginResult(boolean result, String msg) {
        btn_operation.setEnabled(true);
        if (result) {
            Intent i = new Intent(me, ActivityHome.class);
            startActivity(i);
        } else {
            Toast.makeText(this, "登陆失败,重新登陆", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        String name = et_name.getText().toString();
        String passwd = et_passwd.getText().toString();
        switch (v.getId()) {
            case R.id.btn_operation://登陆注册

                String btnName = btn_operation.getText().toString();
                String login = getResources().getString(R.string.login);
                String register = getResources().getString(R.string.register);
                if (btnName.equals(login)) {
                    loginPresenter.doLogin(name, passwd);
                } else if (login.equals(register)) {
                    regisPresenter.doRegis();
                } else {
                    //Nothing
                }
                break;
            case R.id.iv_regis://注册

                toggleUI(false);
                regisPresenter.doRegis();
                break;
            case R.id.tv_count_down://验证码刷新
                initVerCodeView();
                handler.postDelayed(runnable, 1000);
                break;
        }
    }

    int recLen = 10;
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (recLen > 0) {
                recLen--;
                tv_count_down.setText(recLen + "");
                handler.postDelayed(this, 1000);
            } else {
                recLen = 10;
                tv_count_down.setText(R.string.refresh);
            }
        }
    };


    /**
     * 点击空白处隐藏软键盘
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }
}
