package com.hp.triclops.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by luj on 2015/10/3.
 */
@Entity
@Table(name = "t_data_warning_message")
public class WarningMessageData {
    private Long id;
    private String vin;
    private String imei;
    private int applicationId;
    private int messageId;
    private Date sendingTime;
    private Date receiveTime;
    private Short isLocation;
    private String northSouth;
    private String eastWest;
    private double latitude;
    private double longitude;
    private float speed;
    private int heading;

    private Short srsWarning;
    private Short ataWarning;

    private Short safetyBeltCount;
    private int  vehicleHitSpeed;

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
    public Date getSendingTime() {
        return sendingTime;
    }

    public void setSendingTime(Date sendingTime) {
        this.sendingTime = sendingTime;
    }

    @Basic
    @Column(name = "receive_time", nullable = false, insertable = true, updatable = true)
    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
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
    @Column(name = "north_south", nullable = false, insertable = true, updatable = true, length = 1)
    public String getNorthSouth() {
        return northSouth;
    }

    public void setNorthSouth(String northSouth) {
        this.northSouth = northSouth;
    }
    @Basic
    @Column(name = "east_west", nullable = false, insertable = true, updatable = true, length = 1)
    public String getEastWest() {
        return eastWest;
    }

    public void setEastWest(String eastWest) {
        this.eastWest = eastWest;
    }

    @Basic
    @Column(name = "latitude", nullable = false, insertable = true, updatable = true)
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Basic
    @Column(name = "longitude", nullable = false, insertable = true, updatable = true)
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Basic
    @Column(name = "speed", nullable = false, insertable = true, updatable = true)
    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
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
    @Column(name = "srs_warning", nullable = false, insertable = true, updatable = true)
    public Short getSrsWarning() {
        return srsWarning;
    }

    public void setSrsWarning(Short srsWarning) {
        this.srsWarning = srsWarning;
    }

    @Basic
    @Column(name = "ata_warning", nullable = false, insertable = true, updatable = true)
    public Short getAtaWarning() {
        return ataWarning;
    }

    public void setAtaWarning(Short ataWarning) {
        this.ataWarning = ataWarning;
    }

    @Basic
    @Column(name = "vehicle_hit_speed", nullable = false, insertable = true, updatable = true)
    public int getVehicleHitSpeed() {
        return vehicleHitSpeed;
    }

    public void setVehicleHitSpeed(int vehicleHitSpeed) {
        this.vehicleHitSpeed = vehicleHitSpeed;
    }

    @Basic
    @Column(name = "safety_belt_count", nullable = false, insertable = true, updatable = true)
    public Short getSafetyBeltCount() {
        return safetyBeltCount;
    }

    public void setSafetyBeltCount(Short safetyBeltCount) {
        this.safetyBeltCount = safetyBeltCount;
    }
}
