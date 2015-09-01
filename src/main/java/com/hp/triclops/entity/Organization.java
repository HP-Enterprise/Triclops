package com.hp.triclops.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 用户类
 */
@Entity
@Table(name = "t_organization")
public class Organization implements Serializable {
    private int id;
    private String orgName;
    private String breCode;
    private int typeKey;
    private int subOid;
    private int parOid;

    public Organization() {}

    public Organization(String orgName, String breCode, int typeKey, int subOid, int parOid) {
        this.orgName = orgName;
        this.breCode = breCode;
        this.typeKey = typeKey;
        this.subOid = subOid;
        this.parOid = parOid;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "O_Id", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "org_name", nullable = false, insertable = true, updatable = true, length = 128)
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Basic
    @Column(name = "type_key", nullable = true, insertable = true, updatable = true)
    public int getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(int typeKey) {
        this.typeKey = typeKey;
    }

    @Basic
    @Column(name = "bre_code", nullable = true, insertable = true, updatable = true, length = 8)
    public String getBreCode() {
        return breCode;
    }

    public void setBreCode(String breCode) {
        this.breCode = breCode;
    }

    @Basic
    @Column(name = "sub_oid", nullable = true, insertable = true, updatable = true)
    public int getSubOid() {
        return subOid;
    }

    public void setSubOid(int subOid) {
        this.subOid = subOid;
    }

    @Basic
    @Column(name = "par_oid", nullable = true, insertable = true, updatable = true)
    public int getParOid() {
        return parOid;
    }

    public void setParOid(int parOid) {
        this.parOid = parOid;
    }
}
