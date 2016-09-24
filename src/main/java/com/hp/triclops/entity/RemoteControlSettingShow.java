package com.hp.triclops.entity;

import java.util.Date;

/**
 * Created by Administrator on 2015/12/10.
 */
public class RemoteControlSettingShow {

    private short startEngine;//远程启动发动机 0:未激活 1:激活
    private short centralLock;//中控锁 0:未激活 1:激活
    private short findCar;//远程寻车 0:未激活 1:激活
    private short ac;//空调 0:未激活 1:激活
    private short seatHeating;//座椅加热 0:未激活 1:激活
    private short remindFailure;//故障提醒 0:未激活 1:激活
    private short location;//定位 0:未激活 1:激活
    private short sms;//紧急短信 0:未激活 1:激活

    public short getStartEngine() {
        return startEngine;
    }

    public void setStartEngine(short startEngine) {
        this.startEngine = startEngine;
    }

    public short getCentralLock() {
        return centralLock;
    }

    public void setCentralLock(short centralLock) {
        this.centralLock = centralLock;
    }

    public short getFindCar() {
        return findCar;
    }

    public void setFindCar(short findCar) {
        this.findCar = findCar;
    }

    public short getAc() {
        return ac;
    }

    public void setAc(short ac) {
        this.ac = ac;
    }

    public short getSeatHeating() {
        return seatHeating;
    }

    public void setSeatHeating(short seatHeating) {
        this.seatHeating = seatHeating;
    }

    public short getRemindFailure() {
        return remindFailure;
    }

    public void setRemindFailure(short remindFailure) {
        this.remindFailure = remindFailure;
    }

    public short getLocation() {
        return location;
    }

    public void setLocation(short location) {
        this.location = location;
    }

    public short getSms() {
        return sms;
    }

    public void setSms(short sms) {
        this.sms = sms;
    }
}
