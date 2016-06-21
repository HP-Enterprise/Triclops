package com.hp.triclops.entity;

/**
 * Created by jackl on 2016/6/21.
 */
public class RemoteControlBody {
    private int uid;
    private String vin;//Ŀ�공��vin
    private short cType;//
    private short lightNum;//���ƴ���������cType=10��Ч
    private short lightTime;//����ʱ��������cType=10��Ч
    private short hornNum;//���Ѵ���������cType=10��Ч
    private short hornTime;//����ʱ��������cType=10��Ч
    private short recirMode;//ѭ��ģʽ 0 ��ѭ�� 1 ��ѭ�� Ĭ����ѭ��.����cType=4��Ч
    private short acMode;//ACģʽ  0 �ر�ѹ���� 1 ����ѹ����.����cType=4��Ч
    private short fan;//����  ��Χ 1-7.����cType=4��Ч
    private short mode;//ģʽ  1���� 2ǰ��������+���� 3 ���� 4������+���� 5������.����cType=4��Ч
    private short temp;//�¶� ����cType=4��Ч
    private short masterStat;//���ݼ���״̬ 0�ر� 1 ���� ����cType=6 7��Ч
    private short masterLevel;//���ݼ��ȼ��� 1 �� 2 �� 3�� ����cType=6��Ч
    private short slaveStat;//����ʻ����״̬0�ر� 1 ���� ����cType=6 7��Ч
    private short slaveLevel;//���ݼ��ȼ���  1 �� 2 �� 3�� ����cType=6��Ч

    private double latitude;//γ��
    private double longitude;//����

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

    public short getLightTime() {
        return lightTime;
    }

    public void setLightTime(short lightTime) {
        this.lightTime = lightTime;
    }

    public short getHornNum() {
        return hornNum;
    }

    public void setHornNum(short hornNum) {
        this.hornNum = hornNum;
    }

    public short getHornTime() {
        return hornTime;
    }

    public void setHornTime(short hornTime) {
        this.hornTime = hornTime;
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

    public short getTemp() {
        return temp;
    }

    public void setTemp(short temp) {
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
