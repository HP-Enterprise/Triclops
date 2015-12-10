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
         �������  0��Զ������������  1��Զ�̹رշ�����  2����������  3�����Ž���  4���յ�����  5���յ��ر�  6�����μ���  7������ֹͣ����  8��Զ�̷���������  9��Զ�̷��������ƹر�  10��Զ��Ѱ��
     */
    private Short acTemperature;
    private Short status;
    private String remark;
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

    public Short getAcTemperature() {
        return acTemperature;
    }

    public void setAcTemperature(Short acTemperature) {
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
}
