package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * Created by Teemol on 2016/1/22.
 */

@Entity
@Table(name="t_organization_vehicle")
public class OrganisationVehicleRelativeEx {

    private int id;
    private int oid;
    private int vid;

    public OrganisationVehicleRelativeEx(int oid, int vid) {
        this.oid = oid;
        this.vid = vid;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id",nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "oid", nullable = false, insertable = true, updatable = true)
    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    @Basic
    @Column(name = "vid", nullable = false, insertable = true, updatable = true)
    public int getVid() {
        return vid;
    }

    public void setVid(int vid) {
        this.vid = vid;
    }
}
