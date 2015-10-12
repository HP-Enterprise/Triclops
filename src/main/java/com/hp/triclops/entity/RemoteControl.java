package com.hp.triclops.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by luj on 2015/10/10.
 */
@Entity
@Table(name = "t_remote_control")
public class RemoteControl {
    private Long id;
    private int uid;
    private String vin;
    private Date sendingTime;
    private Short controlType;
    /*
            0：远程启动发动机
            1：远程关闭发动机
            2：车门上锁
            3：车门解锁
            4：空调开启
            5：空调关闭
            6：座椅加热
            7：座椅停止加热
            8：远程鸣笛
            9：远程鸣笛关闭
            10：远程开灯
            11：远程关闭
            12：远程发动机限制
            13：远程发动机限制关闭
            14：远程寻车
            15：设置空调温度
             */
    private Short acTemperature;
    private Short status;
    private String remark;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "uid", nullable = false, insertable = true, updatable = true)
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Basic
    @Column(name = "vin", nullable = false, insertable = true, updatable = true, length = 50)
    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @Basic
    @Column(name = "sending_time", nullable = false, insertable = true, updatable = true)
    public Date getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(Date sendingTime) {
        this.sendingTime = sendingTime;
    }

    @Basic
    @Column(name = "control_type", nullable = false, insertable = true, updatable = true)
    public Short getControlType() {
        return controlType;
    }

    public void setControlType(Short controlType) {
        this.controlType = controlType;
    }

    @Basic
    @Column(name = "ac_temperature", nullable = false, insertable = true, updatable = true)
    public Short getAcTemperature() {
        return acTemperature;
    }

    public void setAcTemperature(Short acTemperature) {
        this.acTemperature = acTemperature;
    }

    @Basic
    @Column(name = "status", nullable = false, insertable = true, updatable = true)
    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    @Basic
    @Column(name = "remark", nullable = false, insertable = true, updatable = true, length = 200)
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }


}
