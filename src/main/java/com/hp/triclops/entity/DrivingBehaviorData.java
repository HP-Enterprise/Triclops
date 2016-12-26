package com.hp.triclops.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by jackl on 2016/12/26.
 */
@Entity
@Table(name = "t_data_driving_behavior")
public class DrivingBehaviorData {
    private Long id;
    private String vin;
    private String imei;
    private int applicationId;
    private int messageId;
    private Date sendingTime;
    private Date receiveTime;
    private Short speedUp;
    private Short speedDown;
    private Short speedTurn;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = true, updatable = true)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public Date getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(Date sendingTime) {
        this.sendingTime = sendingTime;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Short getSpeedUp() {
        return speedUp;
    }

    public void setSpeedUp(Short speedUp) {
        this.speedUp = speedUp;
    }

    public Short getSpeedDown() {
        return speedDown;
    }

    public void setSpeedDown(Short speedDown) {
        this.speedDown = speedDown;
    }

    public Short getSpeedTurn() {
        return speedTurn;
    }

    public void setSpeedTurn(Short speedTurn) {
        this.speedTurn = speedTurn;
    }
}
