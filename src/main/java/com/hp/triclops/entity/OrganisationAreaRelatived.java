package com.hp.triclops.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Liujingguo91Year on 2015/11/17.
 */
@Entity
@Table(name="t_organization_area")
public class OrganisationAreaRelatived implements Serializable {
    private int id;
    private Organization org;
    private Area area;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id",nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "areaid",referencedColumnName = "id")
    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "oid",referencedColumnName = "Id")
    public Organization getOrg() {
        return org;
    }

    public void setOrg(Organization org) {
        this.org = org;
    }

}
