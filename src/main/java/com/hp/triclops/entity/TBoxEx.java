package com.hp.triclops.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Incar on 2016/3/24.
 */

@Entity
@Table(name = "t_tbox")
public class TBoxEx {

    private int id;
    private int vid;
    private String t_sn;
    private String vin;
    private int is_activated;
    private Date activation_time;
    private String imei;
    private String mobile;
    private String remark;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "vid", nullable = true, insertable = true, updatable = true)
    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }

    @Basic
    @Column(name = "t_sn", nullable = true, insertable = true, updatable = true)
    public String getT_sn() {
        return t_sn;
    }

    public void setT_sn(String t_sn) {
        this.t_sn = t_sn;
    }

    @Basic
    @Column(name = "vin", nullable = true, insertable = true, updatable = true)
    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @Basic
    @Column(name = "is_activated", nullable = false, insertable = true, updatable = true)
    public int getIs_activated() {
        return is_activated;
    }

    public void setIs_activated(int is_activated) {
        this.is_activated = is_activated;
    }

    @Basic
    @Column(name = "activation_time", nullable = true, insertable = true, updatable = true)
    public Date getActivation_time() {
        return activation_time;
    }

    public void setActivation_time(Date activation_time) {
        this.activation_time = activation_time;
    }

    @Basic
    @Column(name = "imei", nullable = true, insertable = true, updatable = true)
    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    @Basic
    @Column(name = "mobile", nullable = true, insertable = true, updatable = true)
    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Basic
    @Column(name = "remark", nullable = true, insertable = true, updatable = true)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
