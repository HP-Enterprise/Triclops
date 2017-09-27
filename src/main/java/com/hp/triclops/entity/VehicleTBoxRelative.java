package com.hp.triclops.entity;

/**
 * Created by sunjun on 2017/9/27.
 */

import javax.persistence.*;
import java.util.Date;

/**
 * VehicleTBoxRelative
 */
@Entity
@Table(name = "t_vehicle_tbox")
public class VehicleTBoxRelative {
    private int id;
    private String vin;
    private String oldVin;
    private String tboxsn;
    private String oldTboxsn;
    private Date createTime;

    public VehicleTBoxRelative() {
    }

    @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "id", nullable = false, insertable = true, updatable = true)
     public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "vin", nullable = false, insertable = true, updatable = true, length = 20)
    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    @Basic
    @Column(name = "old_vin", nullable = false, insertable = true, updatable = true, length = 20)
    public String getOldVin() {
        return oldVin;
    }

    public void setOldVin(String oldVin) {
        this.oldVin = oldVin;
    }

    @Basic
    @Column(name = "tboxsn", nullable = false, insertable = true, updatable = true, length = 20)
    public String getTboxsn() {
        return tboxsn;
    }

    public void setTboxsn(String tboxsn) {
        this.tboxsn = tboxsn;
    }

    @Basic
    @Column(name = "old_tboxsn", nullable = false, insertable = true, updatable = true, length = 20)
    public String getOldTboxsn() {
        return oldTboxsn;
    }

    public void setOldTboxsn(String oldTboxsn) {
        this.oldTboxsn = oldTboxsn;
    }

    @Basic
    @Column(name = "create_time", nullable = false, insertable = true, updatable = true)
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
