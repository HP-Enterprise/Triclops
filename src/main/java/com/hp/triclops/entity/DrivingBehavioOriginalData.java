package com.hp.triclops.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by jackl on 2016/12/29.
 */
@Entity
@Table(name = "t_data_original_driving_behavior")
public class DrivingBehavioOriginalData {
    private Long id;
    private String vin;
    private String imei;
    private String hexString;
    private Date receiveTime;

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

    @Lob
    public String getHexString() {
        return hexString;
    }

    public void setHexString(String hexString) {
        this.hexString = hexString;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }
}
