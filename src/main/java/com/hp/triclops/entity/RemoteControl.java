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
    private String sessionId;
    private String vin;
    private Date sendingTime;
    private Short controlType;
    /*
         控制类别  0：远程启动发动机  1：远程关闭发动机  2：车门上锁  3：车门解锁  4：空调开启  5：空调关闭  6：座椅加热  7：座椅停止加热  8：远程发动机限制  9：远程发动机限制关闭  10：远程寻车
     */

    private Short status;
    private String remark;
    private String remarkEn;
    private Short available;// 0 失效 1 有效 默认为1
    private Double acTemperature;
    //0625新增加的控制指令
    private Short lightNum;//闪灯次数，仅在cType=10生效
    private Double lightTime;//闪灯时长，仅在cType=10生效
    private Short hornNum;//鸣笛次数，仅在cType=10生效
    private Double hornTime;//鸣笛时长，仅在cType=10生效
    private Short recirMode;//循环模式 0 内循环 1 外循环 默认外循环.仅在cType=4生效
    private Short acMode;//AC模式  0 关闭压缩机 1 开启压缩机.仅在cType=4生效
    private Short fan;//风速  范围 1-7.仅在cType=4生效
    private Short mode;//模式  1除雾 2前玻璃除雾+吹脚 3 吹脚 4吹身体+吹脚 5吹身体.仅在cType=4生效
    private Short masterStat;//主驾加热状态 0关闭 1 开启 仅在cType=6 7生效
    private Short masterLevel;//主驾加热级别 1 低 2 中 3高 仅在cType=6生效
    private Short slaveStat;//附驾驶加热状态0关闭 1 开启 仅在cType=6 7生效
    private Short slaveLevel;//附驾加热级别  1 低 2 中 3高 仅在cType=6生效
    private Long refId;


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
    @Column(name = "session_id", nullable = false, insertable = true, updatable = true, length = 15)
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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
    public Double getAcTemperature() {
        return acTemperature;
    }

    public void setAcTemperature(Double acTemperature) {
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

    @Basic
    @Column(name = "available", nullable = false, insertable = true, updatable = true)
    public Short getAvailable() {
        return available;
    }

    public void setAvailable(Short available) {
        this.available = available;
    }


    @Basic
    @Column(name = "light_num", nullable = false, insertable = true, updatable = true)
    public Short getLightNum() {
        return lightNum;
    }

    public void setLightNum(Short lightNum) {
        this.lightNum = lightNum;
    }

    @Basic
    @Column(name = "light_time", nullable = false, insertable = true, updatable = true)
    public Double getLightTime() {
        return lightTime;
    }

    public void setLightTime(Double lightTime) {
        this.lightTime = lightTime;
    }

    @Basic
    @Column(name = "horn_num", nullable = false, insertable = true, updatable = true)
    public Short getHornNum() {
        return hornNum;
    }

    public void setHornNum(Short hornNum) {
        this.hornNum = hornNum;
    }

    @Basic
    @Column(name = "horn_time", nullable = false, insertable = true, updatable = true)
    public Double getHornTime() {
        return hornTime;
    }

    public void setHornTime(Double hornTime) {
        this.hornTime = hornTime;
    }

    @Basic
    @Column(name = "recir_mode", nullable = false, insertable = true, updatable = true)
    public Short getRecirMode() {
        return recirMode;
    }

    public void setRecirMode(Short recirMode) {
        this.recirMode = recirMode;
    }

    @Basic
    @Column(name = "ac_mode", nullable = false, insertable = true, updatable = true)
    public Short getAcMode() {
        return acMode;
    }

    public void setAcMode(Short acMode) {
        this.acMode = acMode;
    }

    @Basic
    @Column(name = "fan", nullable = false, insertable = true, updatable = true)
    public Short getFan() {
        return fan;
    }

    public void setFan(Short fan) {
        this.fan = fan;
    }

    @Basic
    @Column(name = "mode", nullable = false, insertable = true, updatable = true)
    public Short getMode() {
        return mode;
    }

    public void setMode(Short mode) {
        this.mode = mode;
    }

    @Basic
    @Column(name = "master_stat", nullable = false, insertable = true, updatable = true)
    public Short getMasterStat() {
        return masterStat;
    }

    public void setMasterStat(Short masterStat) {
        this.masterStat = masterStat;
    }

    @Basic
    @Column(name = "master_level", nullable = false, insertable = true, updatable = true)
    public Short getMasterLevel() {
        return masterLevel;
    }

    public void setMasterLevel(Short masterLevel) {
        this.masterLevel = masterLevel;
    }

    @Basic
    @Column(name = "slave_stat", nullable = false, insertable = true, updatable = true)
    public Short getSlaveStat() {
        return slaveStat;
    }

    public void setSlaveStat(Short slaveStat) {
        this.slaveStat = slaveStat;
    }

    @Basic
    @Column(name = "slave_level", nullable = false, insertable = true, updatable = true)
    public Short getSlaveLevel() {
        return slaveLevel;
    }

    public void setSlaveLevel(Short slaveLevel) {
        this.slaveLevel = slaveLevel;
    }

    @Basic
    @Column(name = "remark_en", nullable = false, insertable = true, updatable = true, length = 200)
    public String getRemarkEn() {
        return remarkEn;
    }

    public void setRemarkEn(String remarkEn) {
        this.remarkEn = remarkEn;
    }

    public Long getRefId() {
        return refId;
    }
    @Basic
    @Column(name = "ref_id", nullable = false, insertable = true, updatable = true)
    public void setRefId(Long refId) {
        this.refId = refId;
    }
}
