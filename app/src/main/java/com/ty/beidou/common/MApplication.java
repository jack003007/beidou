package com.ty.beidou.common;

import android.app.Application;
import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import com.ty.beidou.model.UserBean;
import com.ty.beidou.utils.SPUtils;

/**
 * Created by ty on 2016/9/20.
 */
public class MApplication extends Application {

    private UserBean muser;//用户信息

    private static MApplication instance;//单例

    private static Context context;
    private SPUtils sp;

    public static MApplication getInstance() {

        if (instance != null)
            return instance;
        return new MApplication();
    }

    public UserBean getMuser() {
        if (muser != null)
            return muser;
        String uInfo = sp.getString("userinfo");
        return JSON.parseObject(uInfo, UserBean.class);
    }

    public void setMuser(UserBean muser) {
        this.muser = muser;
        sp.putString("userinfo", JSON.toJSONString(muser));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Logger.init();//初始化打印工具
        sp = new SPUtils(context, "User");
    }
}
