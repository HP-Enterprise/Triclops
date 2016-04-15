package com.hp.triclops.vo;

import com.hp.triclops.entity.TBoxEx;

import java.util.Date;

/**
 * Created by Incar on 2016/3/24.
 */
public class TBoxExShow {

    private int id;
    private Integer vid;
    private String t_sn;
    private String vin;
    private int is_activated;
    private Date activation_time;
    private String imei;
    private String mobile;
    private String remark;

    public TBoxExShow() {}

    public TBoxExShow(TBoxEx tBoxEx) {
        this.id = tBoxEx.getId();
        this.vid = tBoxEx.getVid();
        this.t_sn = tBoxEx.getT_sn();
        this.vin = tBoxEx.getVin();
        this.is_activated = tBoxEx.getIs_activated();
        this.activation_time = tBoxEx.getActivation_time();
        this.imei = tBoxEx.getImei();
        this.mobile = tBoxEx.getMobile();
        this.remark = tBoxEx.getRemark();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getVid() {
        return vid;
    }

    public void setVid(Integer vid) {
        this.vid = vid;
    }

    public String getT_sn() {
        return t_sn;
    }

    public void setT_sn(String t_sn) {
        this.t_sn = t_sn;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public int getIs_activated() {
        return is_activated;
    }

    public void setIs_activated(int is_activated) {
        this.is_activated = is_activated;
    }

    public Date getActivation_time() {
        return activation_time;
    }

    public void setActivation_time(Date activation_time) {
        this.activation_time = activation_time;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
