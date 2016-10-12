package com.ty.beidou.model;

/**
 * Created by ty on 2016/9/12.
 */
public class UserBean {

    /**
     * id : 19
     * passwd : 1
     * phone : 1
     * realname : 测试账号
     * nickname : 一个昵称
     */

    private String id;
    private String passwd;
    private String phone;
    private String realname;
    private String nickname;

    public UserBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
