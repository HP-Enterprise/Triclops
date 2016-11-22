package com.hp.triclops.entity;

import java.util.List;

/**
 * Created by jackl on 2016/11/14.
 */
public class Body {

    private String imei;
    private Double longitude;
    private Double latitude;
    private String model;// "lv8918", //设备型号
    private String odmModel;// "HBS-1"
    private String imsi;// "",//
    private String sn;// "",//
    private String hwver;// "1.0.0b", //硬件版本号
    private String swver;// "7.9.8", //固件版本号
    private String odmSwver;// "1.0.0",
    private String wifimac;// "00:50:BA:CD:6H:8D", //WIFI MAC地址
    private String btmac;// "00:50:BA:CD:6H:8E", //BT MAC地址
    private String vendor;// "xxx" //厂商
    private String brandName;// “” //品牌名称
    private String localDate;// "YYYY:MM:DD:HH:MM:SS " //本地时间
    private String timezone;// ""
    private String project;// “” //项目名称

    private String operation;//PHOTO,VIDEO,NAVIGATE
    private String operate;
    private CommandParam param;
    private String result;
    private String moveTime;
    private String sleepTime;

    private String collisionTime;

    private List<App> apps;

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getOdmModel() {
        return odmModel;
    }

    public void setOdmModel(String odmModel) {
        this.odmModel = odmModel;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getHwver() {
        return hwver;
    }

    public void setHwver(String hwver) {
        this.hwver = hwver;
    }

    public String getSwver() {
        return swver;
    }

    public void setSwver(String swver) {
        this.swver = swver;
    }

    public String getOdmSwver() {
        return odmSwver;
    }

    public void setOdmSwver(String odmSwver) {
        this.odmSwver = odmSwver;
    }

    public String getWifimac() {
        return wifimac;
    }

    public void setWifimac(String wifimac) {
        this.wifimac = wifimac;
    }

    public String getBtmac() {
        return btmac;
    }

    public void setBtmac(String btmac) {
        this.btmac = btmac;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getLocalDate() {
        return localDate;
    }

    public void setLocalDate(String localDate) {
        this.localDate = localDate;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getOperate() {
        return operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }



    public String getMoveTime() {
        return moveTime;
    }

    public void setMoveTime(String moveTime) {
        this.moveTime = moveTime;
    }

    public String getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(String sleepTime) {
        this.sleepTime = sleepTime;
    }

    public String getCollisionTime() {
        return collisionTime;
    }

    public void setCollisionTime(String collisionTime) {
        this.collisionTime = collisionTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<App> getApps() {
        return apps;
    }

    public void setApps(List<App> apps) {
        this.apps = apps;
    }

    public CommandParam getParam() {
        return param;
    }

    public void setParam(CommandParam param) {
        this.param = param;
    }
}
