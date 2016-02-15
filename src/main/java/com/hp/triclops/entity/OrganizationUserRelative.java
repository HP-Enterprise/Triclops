package com.hp.triclops.entity;

import javax.persistence.*;

/**
 * Created by Teemol on 2016/1/22.
 */
@Entity
@Table(name="t_organization_user")
public class OrganizationUserRelative {

    private int id;
    private int oid;
    private int uid;

    public OrganizationUserRelative(int oid, int uid) {
        this.oid = oid;
        this.uid = uid;
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
    @Column(name = "uid", nullable = false, insertable = true, updatable = true)
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
