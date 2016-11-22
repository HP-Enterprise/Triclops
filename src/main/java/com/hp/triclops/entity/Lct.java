package com.hp.triclops.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by jackl on 2016/11/15.
 */

@Entity
@Table(name = "t_lct")
public class Lct {

    private Long id;
    private String model;
    private String odmModel;
    private String imei;
    private String imsi;
    private String hwVer;
    private String swVer;
    private String odmSwVer;
    private String wifiMac;
    private String btMac;
    private String vendor;
    private String brandName;
    private Date receiveTime;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getHwVer() {
        return hwVer;
    }

    public void setHwVer(String hwVer) {
        this.hwVer = hwVer;
    }

    public String getSwVer() {
        return swVer;
    }

    public void setSwVer(String swVer) {
        this.swVer = swVer;
    }

    public String getOdmSwVer() {
        return odmSwVer;
    }

    public void setOdmSwVer(String odmSwVer) {
        this.odmSwVer = odmSwVer;
    }

    public String getWifiMac() {
        return wifiMac;
    }

    public void setWifiMac(String wifiMac) {
        this.wifiMac = wifiMac;
    }

    public String getBtMac() {
        return btMac;
    }

    public void setBtMac(String btMac) {
        this.btMac = btMac;
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

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }
}
