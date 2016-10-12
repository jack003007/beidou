package com.ty.beidou.common;

/**
 * Created by ty on 2016/9/20.
 */
public class Urls {
    public static String BASE = "http://192.168.71.1/thinkphp/index.php/";

    /**
     * 登陆
     */
    public static String URL_LOGIN = BASE + "Home/User/login";
    /**
     * 周围的人
     */
    public static String URL_NEARBY = BASE + "Home/Location/nearby";
    /**
     * 上传状态
     */
    public static String URL_MSG_SUBMIT = BASE + "Home/Msg/submit";
}
