package com.hp.triclops.entity;

/**
 * Created by jackl on 2016/11/15.
 */
public  class LctApp {
    private String appId;
    private String version;
    private String url;
    private int  size;
    private String md5;
    private String desc;

    public LctApp(){

    }

    public LctApp(String appId, String version, String url, int size, String md5, String desc) {
        this.appId = appId;
        this.version = version;
        this.url = url;
        this.size = size;
        this.md5 = md5;
        this.desc = desc;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}