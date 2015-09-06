package com.hp.triclops.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Teemol on 2015/9/6.
 */

@Entity
@Table(name = "t_user_vehicle_relatived")
public class UserVehicleRelatived implements Serializable {

    private int id;
    private int userid;
    private String vin;
    private int iflag;
    private int parent_user_id;

    public UserVehicleRelatived() {}

    public UserVehicleRelatived(int id,int userid,String vin,int iflag,int parent_user_id) {
        this.id = id;
        this.userid = userid;
        this.vin = vin;
        this.iflag = iflag;
        this.parent_user_id = parent_user_id;
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
    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    @Basic
    @Column(name = "vin", nullable = false, insertable = true, updatable = true, length = 30)
    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
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
    public int getParent_user_id() {
        return parent_user_id;
    }

    public void setParent_user_id(int parent_user_id) {
        this.parent_user_id = parent_user_id;
    }
}
