package com.ty.beidou.common;

import android.app.Application;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.libs.view.utils.SPUtils;
import com.orhanobut.logger.Logger;
import com.ty.beidou.model.UserBean;

/**
 * Created by ty on 2016/9/20.
 */
public class MApplication extends Application {

    private UserBean user;//用户信息

    private SPUtils sp;

    private String token;

    private static MApplication instance;

    public String getToken() {
        if (!TextUtils.isEmpty(token)) {
            return token;
        } else if (sp.getString("token") != null) {
            return sp.getString("token");
        }
        return "";
    }

    public void setToken(String token) {
        this.token = token;
        sp.putString("token", token);
    }

    public static MApplication getInstance() {
        return instance;
    }

    public UserBean getUser() {
        if (user != null)
            return user;
        String uInfo = sp.getString("userinfo");
        return JSON.parseObject(uInfo, UserBean.class);
    }

    public void setUser(UserBean user) {
        this.user = user;
        sp.putString("userinfo", JSON.toJSONString(user));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Logger.init();//初始化打印工具
        sp = new SPUtils(getApplicationContext(), "User");
    }

}
