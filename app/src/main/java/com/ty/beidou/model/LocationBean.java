package com.ty.beidou.model;

/**
 * Created by ty on 2016/9/26.
 */

public class LocationBean {
    /**
     * province : 山东省
     * id : 3
     * area : 市南区
     * city : 青岛市
     */

    private String province;
    private String id;
    private String area;
    private String city;

    public LocationBean() {
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
