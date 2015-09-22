package com.hp.triclops.entity;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name="t_organization_vehicle")
public class OrganisationVehicleRelatived implements Serializable{
    private int id;
    private Organization org;
    private Vehicle vehicle;

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
    @JoinColumn(name = "vid",referencedColumnName = "id")
    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
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
