package com.ty.beidou.common;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.libs.view.utils.SPUtils;
import com.orhanobut.logger.Logger;
import com.ty.beidou.MainActivity;
import com.ty.beidou.R;
import com.ty.beidou.model.UserBean;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.MsgConstant;
import com.umeng.message.PushAgent;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.common.UmLog;
import com.umeng.message.entity.UMessage;

/**
 * Created by ty on 2016/9/20.
 */
public class MApplication extends Application {
    private static final String TAG = MApplication.class.getName();
    public static final String UPDATE_STATUS_ACTION = "com.umeng.message.example.action.UPDATE_STATUS";

    private UserBean user;//用户信息

    private SPUtils sp;

    private String device_token = null;//推送标识码

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    private static MApplication instance;
    private PushAgent mPushAgent;


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
        //保存用户实例时，设置推送Alias
        mPushAgent.addAlias(user.getId(), "ID", new UTrack.ICallBack() {
            @Override
            public void onMessage(boolean b, String s) {
                Logger.d("addAlias:" + b + " message:" + s);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Logger.init();//初始化打印工具
        sp = new SPUtils(getApplicationContext(), "User");
        //推送初始化
        mPushAgent = PushAgent.getInstance(this);
        mPushAgent.setDebugMode(true);
        mPushAgent.setNotificationPlaySound(MsgConstant.NOTIFICATION_PLAY_SDK_ENABLE);
        UmengMessageHandler msgHandler = new UmengMessageHandler() {
            /**
             * 自定义消息的回调方法
             * */
            @Override
            public void dealWithCustomMessage(final Context context, final UMessage uMessage) {
                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        // 对自定义消息的处理方式，点击或者忽略
                        boolean isClickOrDismissed = true;
                        if (isClickOrDismissed) {
                            //自定义消息的点击统计
                            UTrack.getInstance(getApplicationContext()).trackMsgClick(uMessage);
                        } else {
                            //自定义消息的忽略统计
                            UTrack.getInstance(getApplicationContext()).trackMsgDismissed(uMessage);
                        }
                        Toast.makeText(context, uMessage.custom, Toast.LENGTH_LONG).show();
                        Logger.d("dealWithCustomMessage", uMessage);
                    }
                });
            }

            /**
             * 自定义通知栏样式的回调方法
             * 在后台设置通知样式栏编号0~99
             * */
            @Override
            public Notification getNotification(Context context, UMessage msg) {
                Logger.d(msg.toString());
                switch (msg.builder_id) {
                    case 5:
                        return notificationFive(msg);
//                        Notification.Builder builder = new Notification.Builder(context);
//                        RemoteViews myNotificationView = new RemoteViews(context.getPackageName(), R.layout.notification_view);
//                        myNotificationView.setTextViewText(R.id.notification_title, msg.title);
//                        myNotificationView.setTextViewText(R.id.notification_text, msg.text);
//                        myNotificationView.setImageViewBitmap(R.id.notification_large_icon, getLargeIcon(context, msg));
//                        myNotificationView.setImageViewResource(R.id.notification_small_icon, getSmallIconId(context, msg));
//                        builder.setContent(myNotificationView)
//                                .setSmallIcon(getSmallIconId(context, msg))
//                                .setTicker(msg.ticker)
//                                .setAutoCancel(true);
//                        return builder.getNotification();
                    default:
                        //默认为0，若填写的builder_id并不存在，也使用默认。
                        return super.getNotification(context, msg);
                }
            }
        };
        mPushAgent.setMessageHandler(msgHandler);
        /**
         * 自定义行为的回调处理
         * 在后台设置 后续动作-自定义行为
         * UmengNotificationClickHandler是在BroadcastReceiver中被调用，故
         * 如果需启动Activity，需添加Intent.FLAG_ACTIVITY_NEW_TASK
         * */
        UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler() {
            @Override
            public void dealWithCustomAction(Context context, UMessage msg) {
                Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
            }
        };
        //使用自定义的NotificationHandler，来结合友盟统计处理消息通知
        //参考http://bbs.umeng.com/thread-11112-1-1.html
        //CustomNotificationHandler notificationClickHandler = new CustomNotificationHandler();
        mPushAgent.setNotificationClickHandler(notificationClickHandler);


        //注册推送服务 每次调用register都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {
            @Override
            public void onSuccess(String deviceToken) {
                setDevice_token(deviceToken);
                UmLog.i(TAG, "device token: " + deviceToken);
                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }

            @Override
            public void onFailure(String s, String s1) {
                UmLog.i(TAG, "register failed: " + s + " " + s1);
                sendBroadcast(new Intent(UPDATE_STATUS_ACTION));
            }
        });

        mPushAgent.setNoDisturbMode(0, 0, 0, 0);
        //默认情况下，同一台设备在1分钟内收到同一个应用的多条通知时，不会重复提醒
        //同时在通知栏里新的通知会替换掉旧的通知。可以通过如下方法来设置冷却时间：
        //mPushAgent.setMuteDurationSeconds(int seconds);
//        例如设置通知栏最多显示两条通知（当通知栏已经有两条通知，
//          此时若第三条通知到达，则会把第一条通知隐藏）
        mPushAgent.setDisplayNotificationNumber(2);
        //此处是完全自定义处理设置
//        mPushAgent.setPushIntentServiceClass(MyPushIntentService.class);

    }

    /**
     * Notification类型5
     */
    private Notification notificationFive(UMessage msg) {
        Intent mainIntent = new Intent(this, MainActivity.class);

//        FLAG_CANCEL_CURRENT:如果当前系统中已经存在一个相同的 PendingIntent 对象,那么就将先将已有的 PendingIntent 取消，然后重新生成一个 PendingIntent 对象。
//        FLAG_NO_CREATE:如果当前系统中不存在相同的 PendingIntent 对象,系统将不会创建该 PendingIntent 对象而是直接返回 null 。
//        FLAG_ONE_SHOT:该 PendingIntent 只作用一次。
//        FLAG_UPDATE_CURRENT:如果系统中已存在该 PendingIntent 对象,那么系统将保留该 PendingIntent 对象，但是会使用新的 Intent 来更新之前 PendingIntent 中的 Intent 对象数据，例如更新 Intent 中的 Extras 。
        PendingIntent pi = PendingIntent.getActivity(
                this, 0, mainIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this)
                //设置小图标
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                //设置通知标题
                .setTicker(msg.ticker)
                .setContentTitle(msg.title)
                .setContentText(msg.text)
//                .setStyle(new Notification.BigTextStyle()
//                        .bigText(msg.text)
//                        .setBigContentTitle(msg.title))
                .setDefaults(Notification.DEFAULT_ALL)
                .addAction(R.drawable.icon_close,
                        "No", pi)
                .addAction(R.drawable.icon_check,
                        "Yes", pi)
                .setAutoCancel(true);
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        Notification notification = builder.build();
        return notification;

        //设置通知时间，默认为系统发出通知的时间，通常不用设置
        //.setWhen(System.currentTimeMillis());
        //通过builder.build()方法生成Notification对象,并发送通知,id=1
//        notifyManager.notify(1, notification);
    }


}
