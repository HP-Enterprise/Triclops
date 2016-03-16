package com.hp.triclops.entity;

import com.hp.triclops.vo.UserVehicleRelativeExShow;

import javax.persistence.*;

/**
 * Created by Teemol on 2016/1/22.
 */

@Entity
@Table(name = "t_user_vehicle_relatived")
public class UserVehicleRelativeEx {

    private int id;
    private int uid;
    private int vid;
    private int vflag;
    private int iflag;
    private int puid;

    public UserVehicleRelativeEx() {
    }

    public UserVehicleRelativeEx(UserVehicleRelativeExShow userVehicleRelativeExShow) {
        this.id = userVehicleRelativeExShow.getId();
        this.uid = userVehicleRelativeExShow.getUid();
        this.vid = userVehicleRelativeExShow.getVid();
        this.vflag = userVehicleRelativeExShow.getVflag();
        this.iflag = userVehicleRelativeExShow.getIflag();
        this.puid = userVehicleRelativeExShow.getPuid();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "userid", nullable = false, insertable = true, updatable = true)
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    @Basic
    @Column(name = "vid", nullable = false, insertable = true, updatable = true)
    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }

    @Basic
    @Column(name = "vflag", nullable = true, insertable = true, updatable = true)
    public int getVflag() {
        return vflag;
    }

    public void setVflag(int vflag) {
        this.vflag = vflag;
    }

    @Basic
    @Column(name = "iflag", nullable = false, insertable = true, updatable = true)
    public int getIflag() {
        return iflag;
    }

    public void setIflag(int iflag) {
        this.iflag = iflag;
    }

    @Basic
    @Column(name = "parent_user_id", nullable = false, insertable = true, updatable = true)
    public int getPuid() {
        return puid;
    }

    public void setPuid(int puid) {
        this.puid = puid;
    }
}
