package com.hp.triclops.entity;

/**
 * Created by jackl on 2016/6/21.
 */
public class RemoteControlBody {
    private int uid;
    private String vin;//目标车辆vin
    private short cType;//
    private short lightNum;//闪灯次数，仅在cType=10生效
    private short hornNum;//鸣笛次数，仅在cType=10生效
    private double actTime;//闪灯鸣笛时长，仅在cType=10生效 0.2~0.5s
    private short deActive;//关闭闪灯鸣笛，0 无效 1 生效 仅在cType=10生效
    private short autoMode;//空调自动模式 0 手动 1 自动 默认 0
    private short recirMode;//循环模式 0 内循环 1 外循环 默认外循环.仅在cType=4生效
    private short acMode;//AC模式  0 关闭压缩机 1 开启压缩机.仅在cType=4生效
    private short fan;//风速  范围 1-7.仅在cType=4生效
    private short mode;//模式  1除雾 2前玻璃除雾+吹脚 3 吹脚 4吹身体+吹脚 5吹身体.仅在cType=4生效
    private double temp;//温度 仅在cType=4生效 15.5~32.5
    private short masterStat;//主驾加热状态 0关闭 1 开启 仅在cType=6 7生效
    private short masterLevel;//主驾加热级别 1 低 2 中 3高 仅在cType=6生效
    private short slaveStat;//附驾驶加热状态0关闭 1 开启 仅在cType=6 7生效
    private short slaveLevel;//附驾加热级别  1 低 2 中 3高 仅在cType=6生效
    private short windowStat;//车窗状态 0开 1关 cType=11、12生效
    private long refId;


    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public short getcType() {
        return cType;
    }

    public void setcType(short cType) {
        this.cType = cType;
    }

    public short getLightNum() {
        return lightNum;
    }

    public void setLightNum(short lightNum) {
        this.lightNum = lightNum;
    }

    public short getHornNum() {
        return hornNum;
    }

    public void setHornNum(short hornNum) {
        this.hornNum = hornNum;
    }

    public short getRecirMode() {
        return recirMode;
    }

    public void setRecirMode(short recirMode) {
        this.recirMode = recirMode;
    }

    public short getAcMode() {
        return acMode;
    }

    public void setAcMode(short acMode) {
        this.acMode = acMode;
    }

    public short getFan() {
        return fan;
    }

    public void setFan(short fan) {
        this.fan = fan;
    }

    public short getMode() {
        return mode;
    }

    public void setMode(short mode) {
        this.mode = mode;
    }

    public double getActTime() {
        return actTime;
    }

    public void setActTime(double actTime) {
        this.actTime = actTime;
    }

    public short getDeActive() {
        return deActive;
    }

    public void setDeActive(short deActive) {
        this.deActive = deActive;
    }

    public short getAutoMode() {
        return autoMode;
    }

    public void setAutoMode(short autoMode) {
        this.autoMode = autoMode;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public short getMasterStat() {
        return masterStat;
    }

    public void setMasterStat(short masterStat) {
        this.masterStat = masterStat;
    }

    public short getMasterLevel() {
        return masterLevel;
    }

    public void setMasterLevel(short masterLevel) {
        this.masterLevel = masterLevel;
    }

    public short getSlaveStat() {
        return slaveStat;
    }

    public void setSlaveStat(short slaveStat) {
        this.slaveStat = slaveStat;
    }

    public short getSlaveLevel() {
        return slaveLevel;
    }

    public void setSlaveLevel(short slaveLevel) {
        this.slaveLevel = slaveLevel;
    }

    public long getRefId() {
        return refId;
    }

    public void setRefId(long refId) {
        this.refId = refId;
    }

    public short getWindowStat() {
        return windowStat;
    }

    public void setWindowStat(short windowStat) {
        this.windowStat = windowStat;
    }
}
