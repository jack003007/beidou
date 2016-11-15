package com.ty.beidou.model;

/**
 * Created by ty on 2016/9/12.
 */
public class UserBean {

    /**
     * id : 1
     * realname : 真名
     * nickname : 昵称
     * phone : 1
     * passwd : 1
     * headid : null
     * isonline : 1
     * token : 0
     * longitude : 0
     * latitude : 0
     * ctime : 2016-10-31 14:57:10
     * identity : 0
     * company : 0
     */

    private String id;
    private String realname;
    private String phone;
    private String passwd;
    private String origin;

    public String getHeadthumbnail() {
        return headthumbnail;
    }

    public void setHeadthumbnail(String headthumbnail) {
        this.headthumbnail = headthumbnail;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    private String headthumbnail;
    private String isonline;
    private String longitude;
    private String latitude;
    private String ctime;
    private String identity;
    private String company;

    public String getIdentityid() {
        return identityid;
    }

    public void setIdentityid(String identityid) {
        this.identityid = identityid;
    }

    public String getCompanyid() {
        return companyid;
    }

    public void setCompanyid(String companyid) {
        this.companyid = companyid;
    }

    private String identityid;
    private String companyid;

    /**
     * id : 19
     * passwd : 1
     * phone : 1
     * realname : 测试账号
     * nickname : 一个昵称
     */


    public UserBean() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }


    public String getIsonline() {
        return isonline;
    }

    public void setIsonline(String isonline) {
        this.isonline = isonline;
    }


    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
