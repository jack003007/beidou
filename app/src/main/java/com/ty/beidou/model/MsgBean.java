package com.ty.beidou.model;

/**
 * Created by ty on 2016/9/27.
 */

public class MsgBean {

    /**
     * id : 23
     * ctime : 1476366741
     * title : 题目
     * content : 内容
     * supportnum : 0
     * collectnum : 0
     */

    private String id;
    private String ctime;
    private String title;
    private String content;
    private String supportnum;
    private String collectnum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSupportnum() {
        return supportnum;
    }

    public void setSupportnum(String supportnum) {
        this.supportnum = supportnum;
    }

    public String getCollectnum() {
        return collectnum;
    }

    public void setCollectnum(String collectnum) {
        this.collectnum = collectnum;
    }

}
