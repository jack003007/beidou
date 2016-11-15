package com.ty.beidou.common;

/**
 * Created by ty on 2016/9/20.
 */
public class Urls {

    public static String BASE = "http://192.168.71.1/thinkphp/";

    /**
     * 自动登陆
     */
    public static String URL_AUTOLOGIN = BASE + "index.php/Home/User/autologin";
    /**
     * 登陆
     */
    public static String URL_LOGIN = BASE + "index.php/Home/User/login";

    /**
     * 注册
     */
    public static String URL_REGIS = BASE + "index.php/Home/User/regis";

    /**
     * 修改个人资料
     */
    public static String URL_MODIFY = BASE + "index.php/Home/User/modify";
    /**
     * 获取单位名称列表
     */
    public static String URL_COMPANY = BASE + "index.php/Home/User/company";
    /**
     * 获取名称列表
     */
    public static String URL_IDENTITY = BASE + "index.php/Home/User/identity";

    /**
     * 周围的人
     */
    public static String URL_NEARBY = BASE + "index.php/Home/Location/nearby";
    /**
     * 提交自己的位置信息
     */
    public static String URL_PUT_LOCATION = BASE + "index.php/Home/Location/putlocation";
    /**
     * 上传状态
     */
    public static String URL_MSG_SUBMIT = BASE + "index.php/Home/Msg/submit";

    /**
     * 所有个人动态
     */
    public static String URL_MSG_ALL = BASE + "index.php/Home/Msg/allmsg";

    /**
     * 表单样式
     */
    public static String URL_WORK_FORM = BASE + "index.php/Home/Work/form";

    /**
     * 上传工作表
     */
    public static String URL_WORK_POST = BASE + "index.php/Home/Work/post";

}
