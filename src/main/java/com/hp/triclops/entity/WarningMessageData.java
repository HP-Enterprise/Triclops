package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * Created by luj on 2015/10/3.
 */
@Entity
@Table(name = "t_data_warning_message")
public class WarningMessageData {
    private Long id;
    private String vin;
    private String serialNumber;
    private String imei;
    private int applicationId;
    private int messageId;
    private int sendingTime;
///////////////////
    private Short isLocation;
    private Long latitude;
    private Long longitude;
    private int speed;
    private int heading;
    private String bcm1;
    private String ems;
    private String tcu;
    private String ic;
    private String abs;
    private String pdc;
    private String bcm2;


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
    @Column(name = "vin", nullable = false, insertable = true, updatable = true, length = 50)
    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @Basic
    @Column(name = "serial_number", nullable = false, insertable = true, updatable = true, length = 50)
    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Basic
    @Column(name = "imei", nullable = false, insertable = true, updatable = true, length = 50)
    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    @Basic
    @Column(name = "application_id", nullable = false, insertable = true, updatable = true)
    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    @Basic
    @Column(name = "message_id", nullable = false, insertable = true, updatable = true)
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    @Basic
    @Column(name = "sending_time", nullable = false, insertable = true, updatable = true)
    public int getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(int sendingTime) {
        this.sendingTime = sendingTime;
    }

    @Basic
    @Column(name = "is_location", nullable = false, insertable = true, updatable = true)
    public Short getIsLocation() {
        return isLocation;
    }

    public void setIsLocation(Short isLocation) {
        this.isLocation = isLocation;
    }

    @Basic
    @Column(name = "latitude", nullable = false, insertable = true, updatable = true)
    public Long getLatitude() {
        return latitude;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    @Basic
    @Column(name = "longitude", nullable = false, insertable = true, updatable = true)
    public Long getLongitude() {
        return longitude;
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }

    @Basic
    @Column(name = "speed", nullable = false, insertable = true, updatable = true)
    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Basic
    @Column(name = "heading", nullable = false, insertable = true, updatable = true)
    public int getHeading() {
        return heading;
    }

    public void setHeading(int heading) {
        this.heading = heading;
    }

    @Basic
    @Column(name = "bcm1", nullable = false, insertable = true, updatable = true, length = 8)
    public String getBcm1() {
        return bcm1;
    }

    public void setBcm1(String bcm1) {
        this.bcm1 = bcm1;
    }

    @Basic
    @Column(name = "ems", nullable = false, insertable = true, updatable = true, length = 8)
    public String getEms() {
        return ems;
    }

    public void setEms(String ems) {
        this.ems = ems;
    }

    @Basic
    @Column(name = "tcu", nullable = false, insertable = true, updatable = true, length = 8)
    public String getTcu() {
        return tcu;
    }

    public void setTcu(String tcu) {
        this.tcu = tcu;
    }

    @Basic
    @Column(name = "ic", nullable = false, insertable = true, updatable = true, length = 8)
    public String getIc() {
        return ic;
    }

    public void setIc(String ic) {
        this.ic = ic;
    }

    @Basic
    @Column(name = "abs", nullable = false, insertable = true, updatable = true, length = 8)
    public String getAbs() {
        return abs;
    }

    public void setAbs(String abs) {
        this.abs = abs;
    }

    @Basic
    @Column(name = "pdc", nullable = false, insertable = true, updatable = true, length = 8)
    public String getPdc() {
        return pdc;
    }

    public void setPdc(String pdc) {
        this.pdc = pdc;
    }

    @Basic
    @Column(name = "bcm2", nullable = false, insertable = true, updatable = true, length = 8)
    public String getBcm2() {
        return bcm2;
    }

    public void setBcm2(String bcm2) {
        this.bcm2 = bcm2;
    }
}
