package com.ty.beidou.model;

/**
 * Created by ty on 2016/10/27.
 */

public class FormStyleBean {

    /**
     * id : 1
     * category : 文字填写框
     * hint : 请输入
     * field : name
     * label : 名称
     * unit :
     * ismust : 1
     * isdeprecated : 0
     * value : 小猫
     */

    private String id;
    private String category;
    private String hint;
    private String field;
    private String label;
    private String unit;
    private String ismust;
    private String isdeprecated;
    private String value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getIsmust() {
        return ismust;
    }

    public void setIsmust(String ismust) {
        this.ismust = ismust;
    }

    public String getIsdeprecated() {
        return isdeprecated;
    }

    public void setIsdeprecated(String isdeprecated) {
        this.isdeprecated = isdeprecated;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
