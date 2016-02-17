package com.hp.triclops.entity;

import com.hp.triclops.vo.OrganizationShow;

import javax.persistence.*;

/**
 * Created by Teemol on 2016/1/28.
 */

@Entity
@Table(name = "t_organization")
public class OrganizationEx {

    private int id;
    private String orgName;
    private String breCode;
    private int typeKey;
    private String descript;
    private int areaid;
    private int available;

    public OrganizationEx() {
    }

    public OrganizationEx(OrganizationShow organizationShow) {
        this.id = organizationShow.getId();
        this.orgName = organizationShow.getOrgName();
        this.breCode = organizationShow.getBreCode();
        this.typeKey = organizationShow.getTypeKey();
        this.descript = organizationShow.getDescript();
        this.areaid = organizationShow.getAreaid();
        this.available = organizationShow.getAvailable();
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
    @Column(name = "org_name", nullable = false, insertable = true, updatable = true)
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Basic
    @Column(name = "bre_code", nullable = false, insertable = true, updatable = true)
    public String getBreCode() {
        return breCode;
    }

    public void setBreCode(String breCode) {
        this.breCode = breCode;
    }

    @Basic
    @Column(name = "type_key", nullable = false, insertable = true, updatable = true)
    public int getTypeKey() {
        return typeKey;
    }

    public void setTypeKey(int typeKey) {
        this.typeKey = typeKey;
    }

    @Basic
    @Column(name = "descript", nullable = false, insertable = true, updatable = true)
    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }

    @Basic
    @Column(name = "areaid", nullable = true, insertable = true, updatable = true)
    public int getAreaid() {
        return areaid;
    }

    public void setAreaid(int areaid) {
        this.areaid = areaid;
    }

    @Basic
    @Column(name = "available", nullable = true, insertable = true, updatable = true)
    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }
}
