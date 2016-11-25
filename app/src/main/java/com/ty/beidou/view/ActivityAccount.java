package com.ty.beidou.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.libs.view.utils.RegexUtils;
import com.ty.beidou.R;
import com.ty.beidou.common.BaseMvpActivity;
import com.ty.beidou.common.GeneralToolbar;
import com.ty.beidou.common.MApplication;
import com.ty.beidou.presenter.IAccountPresenter;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by ty on 2016/9/12.
 */
public class ActivityAccount extends BaseMvpActivity<IAccountView, IAccountPresenter> implements IAccountView, View.OnClickListener {


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


    private String numStr = "";   //完整Code串
    private int verLen = 4;//Code长度
    private int[] colorArray = new int[6];//随机颜色数组

    private TextView verCodeSrc;   //验证码图像来源
    private TextView tv_count_down;//倒计时
    private LinearLayout ll_ver;//验证码的总布局
    private LinearLayout ll_ver_code;//验证码左侧的父布局

    private GeneralToolbar mToolbar;


    private int status = 1;//1登陆 2注册

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_regis);
//        if (!TextUtils.isEmpty(MApplication.getInstance().getToken())) {
//            autoLogin(MApplication.getInstance().getToken());
//        }
        bindViews();
        initToolbar();
        et_name.setText("13601821141");
        et_passwd.setText("1");
//        loginSuccess("测试需要");
    }

    private void autoLogin(String token) {
        presenter.doAutoLogin(token);
    }

    @Override
    public IAccountPresenter initPresenter() {
        return new IAccountPresenter();
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
    }

    private void initToolbar() {
        mToolbar = GeneralToolbar.newInstance(me);
        mToolbar.setCenterText("登陆");
//        mToolbar.setLeftIcon(R.drawable.icon_return);

    }

    /**
     * 重置验证码
     */
    public void resetVerCodeView() {
        ll_ver_code.removeAllViews();
        numStr = "";
        String s = "";//临时Code
        for (int i = 0; i < verLen; i++) {
            int n = new Random().nextInt(10);
            s = String.valueOf(n);//随机数字
            numStr = numStr + s;
            verCodeSrc.setText("" + s);
            verCodeSrc.setTextColor(randomColor());//随机颜色

            Matrix mMatrix = new Matrix();

            mMatrix.reset();//重置矩阵为单位矩阵

            mMatrix.setRotate(randomAngle());//随机旋转角度

            Bitmap bmNumA = Bitmap.createBitmap(getBitmapFromView(
                    verCodeSrc, 20, 50), 0, 0, 20, 50, mMatrix, true);
            ImageView codeView = new ImageView(ActivityAccount.this);
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
            status = 1;
            iv_regis.setVisibility(View.VISIBLE);
            et_repasswd.setVisibility(View.GONE);
            ll_ver.setVisibility(View.GONE);
            et_ver.setVisibility(View.GONE);
            btn_operation.setText(R.string.login);
            mToolbar.setLeftIcon(null);
//            tv_count_down.setVisibility(View.GONE);
            mToolbar.setCenterText(getResources().getString(R.string.login));
        } else {
            status = 2;
            mToolbar.setLeftIcon(R.drawable.icon_return);
            mToolbar.setLeftOnclickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleUI(true);
                }
            });
            resetVerCodeView();
            iv_regis.setVisibility(View.GONE);
            et_repasswd.setVisibility(View.VISIBLE);
            ll_ver.setVisibility(View.VISIBLE);
//            tv_count_down.setVisibility(View.VISIBLE);
            et_ver.setVisibility(View.VISIBLE);
            btn_operation.setText(R.string.register);
            mToolbar.setCenterText(getResources().getString(R.string.register));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        btn_operation.performClick();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        String u = et_name.getText().toString();
        String p1 = et_passwd.getText().toString();
        String p2 = et_repasswd.getText().toString();
        String ver = et_ver.getText().toString();
        switch (v.getId()) {
            case R.id.btn_operation:
                if (status == 1) {//登陆
                    if (TextUtils.isEmpty(u)) {
                        Toast.makeText(me, "请输入用户名", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (TextUtils.isEmpty(p1)) {
                        Toast.makeText(me, "请输入密码", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!RegexUtils.isMobileExact(u)) {
                        Toast.makeText(me, "账号现仅支持手机号", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!RegexUtils.isMatch("\\w+", p1)) {
                        Toast.makeText(me, "密码不要出现除字母和数字以外的字符", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    HashMap<String, String> hash = new HashMap<>();
                    hash.put("mobile", u);
                    hash.put("passwd", p1);
                    hash.put("device_token", MApplication.getInstance().getDevice_token());

                    presenter.doLogin(JSON.toJSONString(hash));
                } else {//注册
                    if (TextUtils.isEmpty(u) || TextUtils.isEmpty(p1)
                            || TextUtils.isEmpty(p2) || TextUtils.isEmpty(ver)
                            ) {
                        Toast.makeText(me, "请完善信息", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!RegexUtils.isMobileExact(u)) {
                        Toast.makeText(me, "账号现仅支持手机号", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!p1.equals(p2)) {
                        Toast.makeText(me, "前后密码不一致", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!RegexUtils.isMatch("\\w+", p1)) {
                        Toast.makeText(me, "密码不要出现除字母和数字以外的字符", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!ver.equals(numStr)) {
                        Toast.makeText(me, "验证码错误", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    presenter.doRegis(u, p1);
                }
                break;
            case R.id.iv_regis://注册
                toggleUI(false);
                break;
            case R.id.tv_count_down://验证码刷新
                resetVerCodeView();
//                handler.postDelayed(runnable, 1000);
                break;
        }
    }

//    int recLen = 10;
//    Handler handler = new Handler();
//    Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            if (recLen > 0) {
//                recLen--;
//                tv_count_down.setText(recLen + "");
//                handler.postDelayed(this, 1000);
//            } else {
//                recLen = 10;
//                tv_count_down.setText(R.string.refresh);
//            }
//        }
//    };


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

    @Override
    public void onBackPressed() {
        if (status == 2) {
            toggleUI(true);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void error(String msg) {
        Toast.makeText(me, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginSuccess(String msg) {
        Toast.makeText(me, msg, Toast.LENGTH_SHORT).show();
        Intent i = new Intent(me, ActivityHome.class);
        startActivity(i);
        me.finish();
    }

    @Override
    public void regisSuccess(String msg) {
        Toast.makeText(me, msg, Toast.LENGTH_SHORT).show();
        toggleUI(true);
    }

    /**
     * 网络错误
     *
     * @param resourceId
     */
    @Override
    public void netError(int resourceId) {
        Toast.makeText(me, getResources().getString(resourceId), Toast.LENGTH_SHORT).show();
    }
}
