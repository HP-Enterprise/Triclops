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
    private int tripId;
    private Date sendingTime;
    private Date receiveTime;
    private Short speedUp;
    private Short speedDown;
    private Short speedTurn;
    private float tripA;
    private float tripB;
    private String seatbeltFl;
    private String seatbeltFr;
    private String seatbeltRl;
    private String seatbeltRm;
    private String seatbeltRr;
    private int drivingRange;
    private float fuelOil;
    private float avgOilA;
    private float avgOilB;
    private int speed_1_count;
    private int speed_1_45_count;
    private int speed_45_90_count;
    private int speed_90_count;


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

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }


    @Basic
    @Column(name = "trip_a", nullable = false, insertable = true, updatable = true)
    public float getTripA() {
        return tripA;
    }

    public void setTripA(float tripA) {
        this.tripA = tripA;
    }

    @Basic
    @Column(name = "trip_b", nullable = false, insertable = true, updatable = true)
    public float getTripB() {
        return tripB;
    }

    public void setTripB(float tripB) {
        this.tripB = tripB;
    }

    @Basic
    @Column(name = "seatbelt_fl", nullable = false, insertable = true, updatable = true, length = 1)
    public String getSeatbeltFl() {
        return seatbeltFl;
    }

    public void setSeatbeltFl(String seatbeltFl) {
        this.seatbeltFl = seatbeltFl;
    }

    @Basic
    @Column(name = "seatbelt_fr", nullable = false, insertable = true, updatable = true, length = 1)
    public String getSeatbeltFr() {
        return seatbeltFr;
    }

    public void setSeatbeltFr(String seatbeltFr) {
        this.seatbeltFr = seatbeltFr;
    }

    @Basic
    @Column(name = "seatbelt_rl", nullable = false, insertable = true, updatable = true, length = 1)
    public String getSeatbeltRl() {
        return seatbeltRl;
    }

    public void setSeatbeltRl(String seatbeltRl) {
        this.seatbeltRl = seatbeltRl;
    }

    @Basic
    @Column(name = "seatbelt_rm", nullable = false, insertable = true, updatable = true, length = 1)
    public String getSeatbeltRm() {
        return seatbeltRm;
    }

    public void setSeatbeltRm(String seatbeltRm) {
        this.seatbeltRm = seatbeltRm;
    }

    @Basic
    @Column(name = "seatbelt_rr", nullable = false, insertable = true, updatable = true, length = 1)
    public String getSeatbeltRr() {
        return seatbeltRr;
    }

    public void setSeatbeltRr(String seatbeltRr) {
        this.seatbeltRr = seatbeltRr;
    }

    public int getDrivingRange() {
        return drivingRange;
    }

    public void setDrivingRange(int drivingRange) {
        this.drivingRange = drivingRange;
    }

    public float getFuelOil() {
        return fuelOil;
    }

    public void setFuelOil(float fuelOil) {
        this.fuelOil = fuelOil;
    }

    @Basic
    @Column(name = "avg_oil_a", nullable = false, insertable = true, updatable = true)
    public float getAvgOilA() {
        return avgOilA;
    }

    public void setAvgOilA(float avgOilA) {
        this.avgOilA = avgOilA;
    }

    @Basic
    @Column(name = "avg_oil_b", nullable = false, insertable = true, updatable = true)
    public float getAvgOilB() {
        return avgOilB;
    }

    public void setAvgOilB(float avgOilB) {
        this.avgOilB = avgOilB;
    }

    @Basic
    @Column(name = "speed_1_count", nullable = false, insertable = true, updatable = true)
    public int getSpeed_1_count() {
        return speed_1_count;
    }

    public void setSpeed_1_count(int speed_1_count) {
        this.speed_1_count = speed_1_count;
    }

    @Basic
    @Column(name = "speed_1_45_count", nullable = false, insertable = true, updatable = true)
    public int getSpeed_1_45_count() {
        return speed_1_45_count;
    }

    public void setSpeed_1_45_count(int speed_1_45_count) {
        this.speed_1_45_count = speed_1_45_count;
    }

    @Basic
    @Column(name = "speed_45_90_count", nullable = false, insertable = true, updatable = true)
    public int getSpeed_45_90_count() {
        return speed_45_90_count;
    }

    public void setSpeed_45_90_count(int speed_45_90_count) {
        this.speed_45_90_count = speed_45_90_count;
    }
    @Basic
    @Column(name = "speed_90_count", nullable = false, insertable = true, updatable = true)
    public int getSpeed_90_count() {
        return speed_90_count;
    }

    public void setSpeed_90_count(int speed_90_count) {
        this.speed_90_count = speed_90_count;
    }
}
