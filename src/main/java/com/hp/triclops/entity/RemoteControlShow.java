package com.hp.triclops.entity;

import java.util.Date;

/**
 * Created by Administrator on 2015/12/10.
 */
public class RemoteControlShow {

    private Long id;
    private int uid;
    private String sessionId;
    private String vin;
    private Date sendingTime;


    private Short controlType;
    /*
         控制类别  0：远程启动发动机  1：远程关闭发动机  2：车门上锁  3：车门解锁  4：空调开启  5：空调关闭  6：座椅加热  7：座椅停止加热  8：远程发动机限制  9：远程发动机限制关闭  10：远程寻车
     */
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

    private Short status;
    private String remark;
    private String remarkEn;
    private  String licensePlate;
    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public Date getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(Date sendingTime) {
        this.sendingTime = sendingTime;
    }

    public Short getControlType() {
        return controlType;
    }

    public void setControlType(Short controlType) {
        this.controlType = controlType;
    }

    public Double getAcTemperature() {
        return acTemperature;
    }

    public void setAcTemperature(Double acTemperature) {
        this.acTemperature = acTemperature;
    }

    public Short getStatus() {
        return status;
    }

    public void setStatus(Short status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Short getLightNum() {
        return lightNum;
    }

    public void setLightNum(Short lightNum) {
        this.lightNum = lightNum;
    }

    public Double getLightTime() {
        return lightTime;
    }

    public void setLightTime(Double lightTime) {
        this.lightTime = lightTime;
    }

    public Short getHornNum() {
        return hornNum;
    }

    public void setHornNum(Short hornNum) {
        this.hornNum = hornNum;
    }

    public Double getHornTime() {
        return hornTime;
    }

    public void setHornTime(Double hornTime) {
        this.hornTime = hornTime;
    }

    public Short getRecirMode() {
        return recirMode;
    }

    public void setRecirMode(Short recirMode) {
        this.recirMode = recirMode;
    }

    public Short getAcMode() {
        return acMode;
    }

    public void setAcMode(Short acMode) {
        this.acMode = acMode;
    }

    public Short getFan() {
        return fan;
    }

    public void setFan(Short fan) {
        this.fan = fan;
    }

    public Short getMode() {
        return mode;
    }

    public void setMode(Short mode) {
        this.mode = mode;
    }

    public Short getMasterStat() {
        return masterStat;
    }

    public void setMasterStat(Short masterStat) {
        this.masterStat = masterStat;
    }

    public Short getMasterLevel() {
        return masterLevel;
    }

    public void setMasterLevel(Short masterLevel) {
        this.masterLevel = masterLevel;
    }

    public Short getSlaveStat() {
        return slaveStat;
    }

    public void setSlaveStat(Short slaveStat) {
        this.slaveStat = slaveStat;
    }

    public Short getSlaveLevel() {
        return slaveLevel;
    }

    public void setSlaveLevel(Short slaveLevel) {
        this.slaveLevel = slaveLevel;
    }

    public String getRemarkEn() {
        return remarkEn;
    }

    public void setRemarkEn(String remarkEn) {
        this.remarkEn = remarkEn;
    }
}
