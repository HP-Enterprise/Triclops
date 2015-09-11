package com.hp.triclops.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by samsung on 2015/9/6.
 */
@Entity
@Table(name = "t_user_vehicle_relatived")
public class UserVehicleRelatived implements Serializable{
    private int id;
    private User uid;
    private Vehicle vid;
    private int iflag;
    private User parentuser;

    public UserVehicleRelatived() {
    }

    public UserVehicleRelatived(User uid, Vehicle vid, int iflag, User parentuser) {
        this.uid = uid;
        this.vid = vid;
        this.iflag = iflag;
        this.parentuser = parentuser;
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

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "userid",referencedColumnName="Id")
    public User getUid() {
        return uid;
    }

    public void setUid(User uid) {
        this.uid = uid;
    }

    @Basic
    @Column(name = "iflag", nullable = false, insertable = true, updatable = true, length = 5)
    public int getIflag() {
        return iflag;
    }

    public void setIflag(int iflag) {
        this.iflag = iflag;
    }

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "vid",referencedColumnName="id")
    public Vehicle getVid() {
        return vid;
    }

    public void setVid(Vehicle vid) {
        this.vid = vid;
    }

    @ManyToOne(cascade = CascadeType.PERSIST, optional = false)
    @JoinColumn(name = "parent_user_id",referencedColumnName="Id")
    public User getParentuser() {
        return parentuser;
    }

    public void setParentuser(User parentuser) {
        this.parentuser = parentuser;
    }
}
