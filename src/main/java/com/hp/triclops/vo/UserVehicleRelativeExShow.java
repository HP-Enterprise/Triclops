package com.hp.triclops.vo;

import com.hp.triclops.entity.UserVehicleRelativeEx;

/**
 * Created by Incar on 2016/3/15.
 */
public class UserVehicleRelativeExShow {

    private int id;
    private int uid;
    private int vid;
    private int vflag;
    private int iflag;
    private int puid;

    public UserVehicleRelativeExShow() {}

    public UserVehicleRelativeExShow(int uid, int vid, int vflag, int iflag, int puid) {
        this.uid = uid;
        this.vid = vid;
        this.vflag = vflag;
        this.iflag = iflag;
        this.puid = puid;
    }

    public UserVehicleRelativeExShow(UserVehicleRelativeEx userVehicleRelativeEx) {
        this.id = userVehicleRelativeEx.getId();
        this.uid = userVehicleRelativeEx.getUid();
        this.vid = userVehicleRelativeEx.getVid();
        this.vflag = userVehicleRelativeEx.getVflag();
        this.iflag = userVehicleRelativeEx.getIflag();
        this.puid = userVehicleRelativeEx.getPuid();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }

    public int getVflag() {
        return vflag;
    }

    public void setVflag(int vflag) {
        this.vflag = vflag;
    }

    public int getIflag() {
        return iflag;
    }

    public void setIflag(int iflag) {
        this.iflag = iflag;
    }

    public int getPuid() {
        return puid;
    }

    public void setPuid(int puid) {
        this.puid = puid;
    }
}
